package com.my_space.airbnb_clone.dto;

import com.my_space.airbnb_clone.enums.UserRole;
import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private Long userId;
    private String email;
    private String name;
    private Set<UserRole> roles;
}
