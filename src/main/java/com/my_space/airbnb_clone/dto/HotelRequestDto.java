package com.my_space.airbnb_clone.dto;

import com.my_space.airbnb_clone.entity.HotelContactInfo;
import lombok.Data;

@Data
public class HotelRequestDto {
    private String name;
    private String city;
    private String[] photos;
    private String[] amenities;
    private HotelContactInfo contactInfo;
}
