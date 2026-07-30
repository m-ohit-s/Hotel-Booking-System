package com.my_space.airbnb_clone.dto;

import com.my_space.airbnb_clone.enums.Gender;
import lombok.Data;

@Data
public class GuestDto {
    private String name;
    private Gender gender;
    private Integer age;
}
