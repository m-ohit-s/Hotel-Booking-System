package com.my_space.airbnb_clone.service.impls;

import com.my_space.airbnb_clone.dto.BookingRequestDto;
import com.my_space.airbnb_clone.dto.BookingResponseDto;
import com.my_space.airbnb_clone.dto.GuestDto;
import com.my_space.airbnb_clone.dto.HotelReportDto;
import com.my_space.airbnb_clone.entity.*;
import com.my_space.airbnb_clone.enums.BookingStatus;
import com.my_space.airbnb_clone.exceptions.ResourceNotFoundException;
import com.my_space.airbnb_clone.mapper.BookingMapper;
import com.my_space.airbnb_clone.mapper.GuestMapper;
import com.my_space.airbnb_clone.repository.*;
import com.my_space.airbnb_clone.service.interfaces.IBookingService;
import com.my_space.airbnb_clone.service.interfaces.ICheckoutService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.my_space.airbnb_clone.util.AppUtils.getCurrentUser;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final GuestMapper guestMapper;
    private final BookingMapper bookingMapper;
    private final InventoryRepository inventoryRepository;
    private final GuestRepository guestRepository;
    private final ICheckoutService checkoutService;
    private final PricingService pricingService;

    @Value("${frontend.payment-success-url}")
    private String paymentSuccessUrl;

    @Value("${frontend.payment-failure-url}")
    private String paymentFailureUrl;


    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) {
        Hotel hotel = getHotel(bookingRequestDto.getHotelId());
        Room room = getRoom(bookingRequestDto.getRoomId());

        if (!hotel.getRooms().contains(room)) {
            throw new ResourceNotFoundException("Room in Hotel", bookingRequestDto.getRoomId().toString());
        }

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(
                room.getId(),
                bookingRequestDto.getCheckInDate(),
                bookingRequestDto.getCheckOutDate().minusDays(1),
                bookingRequestDto.getRoomsCount()
        );

        long daysCount = ChronoUnit.DAYS.between(bookingRequestDto.getCheckInDate(), bookingRequestDto.getCheckOutDate());
        if (inventoryList.size() != daysCount) {
            throw new IllegalStateException("Room is not available anymore");
        }

        inventoryRepository.initBooking(
                room.getId(),
                bookingRequestDto.getCheckInDate(),
                bookingRequestDto.getCheckOutDate().minusDays(1),
                bookingRequestDto.getRoomsCount()
        );

        Booking booking = Booking.builder()
                .hotel(hotel)
                .room(room)
                .user(getCurrentUser())
                .roomsCount(bookingRequestDto.getRoomsCount())
                .checkInDate(bookingRequestDto.getCheckInDate())
                .checkOut(bookingRequestDto.getCheckOutDate())
                .bookingStatus(BookingStatus.RESERVED)
                .amount(pricingService.totalPrice(inventoryList, bookingRequestDto.getRoomsCount()))
                .build();

        return bookingMapper.bookingToBookingResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDto addGuests(List<GuestDto> guestDtos, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId.toString()));
        User user = getCurrentUser();
        if (!user.getUserId().equals(booking.getUser().getUserId())) {
            throw new IllegalArgumentException("Adding Guest is not authorized for this user");
        }
        if (isBookingExpired(booking)) {
            throw new IllegalStateException("Booking is expired");
        }
        if (!booking.getBookingStatus().equals(BookingStatus.RESERVED)) {
            throw new IllegalStateException("Booking is not reserved");
        }
        Set<Guest> guests = guestDtos.stream().map(guestDto -> {
            Guest guest = guestMapper.GuestDtoToGuest(guestDto);
            guest.setUser(user);
            guest = guestRepository.save(guest);
            return guest;
        }).collect(Collectors.toSet());

        booking.setGuests(guests);
        booking.setBookingStatus(BookingStatus.GUEST_ADDED);
        return bookingMapper.bookingToBookingResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public String initiatePayments(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId.toString()));
        User user = getCurrentUser();
        if (!user.getUserId().equals(booking.getUser().getUserId())) {
            throw new IllegalArgumentException("Adding Guest is not authorized for this user");
        }
        if (isBookingExpired(booking)) {
            throw new IllegalStateException("Booking is expired");
        }
        String sessionUrl = checkoutService.getCheckoutSession(booking, paymentSuccessUrl, paymentFailureUrl);
        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);
        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        if (event.getType().equals("checkout.session.completed")) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session == null) return;
            String sessionId = session.getId();
            Booking booking = bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(() -> new ResourceNotFoundException("Booking", sessionId));
            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
            inventoryRepository.findAndLockReservedInventory(
                    booking.getRoom().getId(),
                    booking.getCheckInDate(),
                    booking.getCheckOut().minusDays(1),
                    booking.getRoomsCount()
            );
            inventoryRepository.confirmBooking(
                    booking.getRoom().getId(),
                    booking.getCheckInDate(),
                    booking.getCheckOut().minusDays(1),
                    booking.getRoomsCount()
            );
        } else {
            log.warn("Unhandled event type: {}", event.getType());
        }

    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId.toString()));
        User user = getCurrentUser();
        if (!user.getUserId().equals(booking.getUser().getUserId())) {
            throw new IllegalArgumentException("Cancellation not authorized for this user");
        }
        if (!booking.getBookingStatus().equals(BookingStatus.CONFIRMED)) {
            throw new IllegalStateException("Booking is not confirmed");
        }

        inventoryRepository.findAndLockReservedInventory(
                booking.getRoom().getId(),
                booking.getCheckInDate(),
                booking.getCheckOut().minusDays(1),
                booking.getRoomsCount()
        );
        inventoryRepository.cancelBooking(
                booking.getRoom().getId(),
                booking.getCheckInDate(),
                booking.getCheckOut().minusDays(1),
                booking.getRoomsCount()
        );

        try {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundCreateParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();
            Refund.create(refundCreateParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    public Page<Booking> findByHotel(Hotel hotel, Pageable pageable) {
        return bookingRepository.findByHotel(hotel, pageable);
    }

    @Override
    public HotelReportDto getHotelReport(Hotel hotel, LocalDate startDate, LocalDate endDate) {
        List<Booking> bookings;
        if (startDate == null && endDate == null) {
            bookings = bookingRepository.findByHotel(hotel);
        } else if (endDate == null) {
            bookings = bookingRepository.findByHotelAndCreatedAtAfter(
                    hotel,
                    startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
            );
        } else if (startDate == null) {
            bookings = bookingRepository.findByHotelAndCreatedAtBefore(
                    hotel,
                    endDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()
            );
        } else {
            bookings = bookingRepository.findByHotelAndCreatedAtBetween(
                    hotel,
                    startDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                    endDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()
            );
        }

        long bookingCount = bookings.stream().filter(booking -> booking.getBookingStatus().equals(BookingStatus.CONFIRMED)).count();
        BigDecimal totalRevenue = bookings
                .stream()
                .filter(booking -> booking.getBookingStatus().equals(BookingStatus.CONFIRMED))
                .map(Booking::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgRevenue = bookingCount == 0 ?
                BigDecimal.valueOf(0) :
                totalRevenue.divide(BigDecimal.valueOf(bookingCount), 2, RoundingMode.HALF_UP);

        return new HotelReportDto(
                bookingCount,
                totalRevenue,
                avgRevenue
        );
    }

    @Override
    public List<BookingResponseDto> getMyBookings() {
        User user = getCurrentUser();
        List<Booking> bookings = bookingRepository.findByUser(user);
        return bookings.stream().map(bookingMapper::bookingToBookingResponse).collect(Collectors.toList());
    }

    /// Utility Methods

    private Hotel getHotel(Long id) {
        return hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel", id.toString()));
    }

    private Room getRoom(Long id) {
        return roomRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room", id.toString()));
    }

    private boolean isBookingExpired(Booking booking) {
        return booking.getCreatedAt().plus(10, ChronoUnit.MINUTES).isBefore(Instant.now());
    }
}
