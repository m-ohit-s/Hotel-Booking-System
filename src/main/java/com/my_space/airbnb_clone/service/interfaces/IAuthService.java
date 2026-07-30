package com.my_space.airbnb_clone.service.interfaces;

import com.my_space.airbnb_clone.dto.AuthResponseDto;
import com.my_space.airbnb_clone.dto.LoginRequestDto;
import com.my_space.airbnb_clone.dto.SignupRequestDto;
import com.my_space.airbnb_clone.dto.TokenDto;

public interface IAuthService {
    TokenDto login(LoginRequestDto loginRequestDto);
    TokenDto signup(SignupRequestDto signupRequestDto);
    TokenDto refresh(String refreshToken);
}
