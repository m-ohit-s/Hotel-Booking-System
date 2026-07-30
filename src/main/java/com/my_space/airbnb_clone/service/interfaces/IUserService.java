package com.my_space.airbnb_clone.service.interfaces;

import com.my_space.airbnb_clone.dto.SignupRequestDto;
import com.my_space.airbnb_clone.dto.UserDto;
import com.my_space.airbnb_clone.dto.UserRequestDto;
import com.my_space.airbnb_clone.dto.UserResponseDto;
import com.my_space.airbnb_clone.entity.User;
import org.springframework.http.ResponseEntity;

public interface IUserService {
    User findById(Long id);

    UserDto createUser(SignupRequestDto signupRequestDto);

    UserResponseDto getLoggedInUser();

    UserResponseDto updateProfile(UserRequestDto userRequestDto);
}
