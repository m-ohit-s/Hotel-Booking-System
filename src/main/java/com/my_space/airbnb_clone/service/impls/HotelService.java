package com.my_space.airbnb_clone.service.impls;

import com.my_space.airbnb_clone.dto.*;
import com.my_space.airbnb_clone.entity.Booking;
import com.my_space.airbnb_clone.entity.Hotel;
import com.my_space.airbnb_clone.entity.Room;
import com.my_space.airbnb_clone.entity.User;
import com.my_space.airbnb_clone.enums.UserRole;
import com.my_space.airbnb_clone.exceptions.ResourceNotFoundException;
import com.my_space.airbnb_clone.mapper.BookingMapper;
import com.my_space.airbnb_clone.mapper.HotelMapper;
import com.my_space.airbnb_clone.mapper.RoomMapper;
import com.my_space.airbnb_clone.repository.HotelRepository;
import com.my_space.airbnb_clone.repository.RoomRepository;
import com.my_space.airbnb_clone.service.interfaces.IBookingService;
import com.my_space.airbnb_clone.service.interfaces.IHotelService;
import com.my_space.airbnb_clone.service.interfaces.IInventoryService;
import com.my_space.airbnb_clone.util.AppUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService implements IHotelService {
    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;
    private final BookingMapper bookingMapper;
    private final IInventoryService inventoryService;
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final IBookingService bookingService;


    @Override
    public HotelResponseDto createHotel(HotelRequestDto hotelRequestDto) {
        User user = checkUserIsManager();
        Hotel hotel = hotelMapper.toHotel(hotelRequestDto);
        hotel.setActive(false);
        hotel.setOwner(user);
        return hotelMapper.toHotelResponseDto(hotelRepository.save(hotel));
    }

    @Override
    public HotelResponseDto getHotelById(Long id) {
        return hotelMapper.toHotelResponseDto(getHotel(id));
    }

    @Override
    public HotelResponseDto updateHotelById(Long id, HotelRequestDto hotelRequestDto) {
        checkUserIsManager();
        Hotel hotel = getHotel(id);
        Hotel updatedHotel = hotelMapper.toHotel(hotelRequestDto);
        updatedHotel.setId(hotel.getId());
        updatedHotel.setActive(hotel.getActive());
        return hotelMapper.toHotelResponseDto(hotelRepository.save(updatedHotel));
    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {
        checkUserIsManager();
        Hotel hotel = getHotel(id);
        for (Room room : hotel.getRooms()) {
            deleteInventoryOfRoom(room);
        }
        roomRepository.deleteByHotel(hotel);
        hotelRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void activateHotel(Long id) {
        checkUserIsManager();
        Hotel hotel = getHotel(id);
        if (hotel.getActive()) return;
        hotel.setActive(true);
        Hotel savedHotel = hotelRepository.save(hotel);
        List<Room> rooms = savedHotel.getRooms();
        for (Room room : rooms) {
            createInventoryOfRoom(room);
        }
    }

    @Override
    @Transactional
    public void deactivateHotelById(Long id) {
        checkUserIsManager();
        Hotel hotel = getHotel(id);
        if (!hotel.getActive()) return;
        hotel.setActive(false);
        hotelRepository.save(hotel);
        for (Room room : hotel.getRooms()) {
            deleteInventoryOfRoom(room);
        }
    }

    @Override
    public List<HotelResponseDto> createHotels(List<HotelRequestDto> hotelRequestDtos) {
        checkUserIsManager();
        return hotelRequestDtos.stream().map(this::createHotel).toList();
    }

    @Override
    public HotelInfoDto getHotelInfo(Long id) {
        Hotel hotel = getHotel(id);
        HotelResponseDto hotelResponseDto = hotelMapper.toHotelResponseDto(hotel);
        List<RoomResponseDto> rooms = hotel.getRooms().stream().map(roomMapper::roomToRoomResponseDto).toList();
        return new HotelInfoDto(hotelResponseDto, rooms);
    }

    @Override
    public Page<HotelResponseDto> getAllHotels(Boolean active, int page, int pageSize) {
        checkUserIsManager();
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Hotel> hotelPage;
        if (active == null) {
            hotelPage = hotelRepository.findAll(pageable);
        } else if (active) {
            hotelPage = hotelRepository.findByActiveTrue(pageable);
        } else {
            hotelPage = hotelRepository.findByActiveFalse(pageable);
        }
        return hotelPage.map(hotelMapper::toHotelResponseDto);
    }

    @Override
    public Page<BookingResponseDto> getBookingsOfHotel(Long hotelId, Integer page, Integer pageSize) {
        User user = checkUserIsManager();
        Hotel hotel = getHotel(hotelId);
        if (!user.getUserId().equals(hotel.getOwner().getUserId())) {
            throw new AccessDeniedException("You are not allowed to access this resource");
        }
        Pageable pageable = PageRequest.of(0, 1);
        Page<Booking> bookings = bookingService.findByHotel(hotel, pageable) ;
        return bookings.map(bookingMapper::bookingToBookingResponse);
    }

    @Override
    public HotelReportDto getHotelReport(
            Long hotelId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        User user = checkUserIsManager();
        Hotel hotel = getHotel(hotelId);
        if (!user.getUserId().equals(hotel.getOwner().getUserId())) {
            throw new AccessDeniedException("You are not allowed to access this resource");
        }
        return bookingService.getHotelReport(hotel, startDate, endDate);
    }

    /// Utility Methods

    private Hotel getHotel(Long id) {
        return hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel", id.toString()));
    }

    private void createInventoryOfRoom(Room room) {
        inventoryService.initializeRoomForAnYear(room);
    }

    private void deleteInventoryOfRoom(Room room) {
        inventoryService.deleteAllInventoriesOfRoom(room);
    }

    private User checkUserIsManager() {
        User user = AppUtils.getCurrentUser();
        if (user == null || user.getRoles() == null || !user.getRoles().contains(UserRole.HOTEL_MANAGER)) {
            throw new AccessDeniedException("You are not authorized to perform this action");
        }
        return user;
    }
}
