package com.my_space.airbnb_clone.mapper;

import com.my_space.airbnb_clone.dto.HotelRequestDto;
import com.my_space.airbnb_clone.dto.HotelResponseDto;
import com.my_space.airbnb_clone.entity.Hotel;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HotelMapper {
    Hotel toHotel(HotelRequestDto hotelRequestDto);
    HotelResponseDto toHotelResponseDto(Hotel hotel);
}
