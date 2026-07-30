package com.my_space.airbnb_clone.dto;

import com.my_space.airbnb_clone.entity.HotelContactInfo;
import lombok.Data;

@Data
public class HotelResponseDto {
    private Long id;
    private String name;
    private String city;
    private String[] photos;
    private String[] amenities;
    private Boolean active;
    private HotelContactInfo contactInfo;
}
