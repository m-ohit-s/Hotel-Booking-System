package com.my_space.airbnb_clone.service.impls;

import com.my_space.airbnb_clone.dto.*;
import com.my_space.airbnb_clone.entity.User;
import com.my_space.airbnb_clone.mapper.UserMapper;
import com.my_space.airbnb_clone.security.JwtService;
import com.my_space.airbnb_clone.service.interfaces.IAuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final UserService userService;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;

    @Override
    public TokenDto login(LoginRequestDto loginRequestDto) {
        Authentication  authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );
        if (!authentication.isAuthenticated()) {
            throw new BadCredentialsException("Bad credentials");
        }
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            throw new BadCredentialsException("returned User is null");
        }
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user.getUserId());
        return new TokenDto(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public TokenDto signup(SignupRequestDto signupRequestDto) {
        UserDto userDto = userService.createUser(signupRequestDto);
        String accessToken = jwtService.generateAccessToken(userMapper.userDtoToUser(userDto));
        String refreshToken = jwtService.generateRefreshToken(userDto.getUserId());
        return new TokenDto(accessToken, refreshToken);
    }

    @Override
    public TokenDto refresh(String refreshToken) {
        Long id = jwtService.getUserIdFromToken(refreshToken);
        User user = userService.findById(id);
        String accessToken = jwtService.generateAccessToken(user);
        return new TokenDto(accessToken, refreshToken);
    }
}
