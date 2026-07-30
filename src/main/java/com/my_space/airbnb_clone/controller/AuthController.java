package com.my_space.airbnb_clone.controller;

import com.my_space.airbnb_clone.dto.AuthResponseDto;
import com.my_space.airbnb_clone.dto.LoginRequestDto;
import com.my_space.airbnb_clone.dto.SignupRequestDto;
import com.my_space.airbnb_clone.dto.TokenDto;
import com.my_space.airbnb_clone.service.interfaces.IAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @RequestBody LoginRequestDto loginRequestDto,
            HttpServletResponse response
    ) {
        TokenDto tokenDto = authService.login(loginRequestDto);
        Cookie cookie = new Cookie("refreshToken", tokenDto.getRefreshToken());
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResponseEntity.ok(new AuthResponseDto(tokenDto.getAccessToken()));
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> signup(
            @RequestBody SignupRequestDto signupRequestDto,
            HttpServletResponse response
    ) {
        TokenDto tokenDto = authService.signup(signupRequestDto);
        Cookie cookie = new Cookie("refreshToken", tokenDto.getRefreshToken());
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponseDto(tokenDto.getAccessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        TokenDto tokenDto = authService.refresh(refreshToken);
        Cookie cookie = new Cookie("refreshToken", tokenDto.getRefreshToken());
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResponseEntity.ok(new AuthResponseDto(tokenDto.getAccessToken()));
    }
}
