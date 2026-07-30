package com.my_space.airbnb_clone.mapper;

import com.my_space.airbnb_clone.dto.HotelMinPriceDto;
import com.my_space.airbnb_clone.dto.HotelPriceDto;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface HotelPageMapper {
    HotelMinPriceDto hotelPageDtoToHotelResponseDto(HotelPriceDto hotelPage);
}
