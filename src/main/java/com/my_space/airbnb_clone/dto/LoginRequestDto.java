package com.my_space.airbnb_clone.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
}
