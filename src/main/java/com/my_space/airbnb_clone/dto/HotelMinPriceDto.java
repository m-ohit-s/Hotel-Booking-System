package com.my_space.airbnb_clone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelMinPriceDto {
    private HotelResponseDto hotelResponseDto;
    private double price;
}
