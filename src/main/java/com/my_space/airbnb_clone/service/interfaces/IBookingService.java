package com.my_space.airbnb_clone.service.interfaces;

import com.my_space.airbnb_clone.dto.BookingRequestDto;
import com.my_space.airbnb_clone.dto.BookingResponseDto;
import com.my_space.airbnb_clone.dto.GuestDto;
import com.my_space.airbnb_clone.dto.HotelReportDto;
import com.my_space.airbnb_clone.entity.Booking;
import com.my_space.airbnb_clone.entity.Hotel;
import com.stripe.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IBookingService {

    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto);

    BookingResponseDto addGuests(List<GuestDto> guestDtos, Long bookingId);

    String initiatePayments(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId);

    Page<Booking> findByHotel(Hotel hotel, Pageable pageable);

    HotelReportDto getHotelReport(Hotel hotel, LocalDate startDate, LocalDate endDate);

    List<BookingResponseDto> getMyBookings();
}
