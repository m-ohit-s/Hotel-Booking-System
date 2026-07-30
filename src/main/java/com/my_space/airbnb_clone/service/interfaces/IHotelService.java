package com.my_space.airbnb_clone.service.interfaces;

import com.my_space.airbnb_clone.dto.*;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface IHotelService {
    HotelResponseDto createHotel(HotelRequestDto hotelRequestDto);

    HotelResponseDto getHotelById(Long id);

    HotelResponseDto updateHotelById(Long id, HotelRequestDto hotelRequestDto);

    void deleteHotelById(Long id);

    void activateHotel(Long id);

    void deactivateHotelById(Long id);

    List<HotelResponseDto> createHotels(List<HotelRequestDto> hotelRequestDtos);

    HotelInfoDto getHotelInfo(Long id);

    Page<HotelResponseDto> getAllHotels(Boolean active, int page, int pageSize);

    Page<BookingResponseDto> getBookingsOfHotel(Long hotelId, Integer page, Integer pageSize);

    HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);
}
