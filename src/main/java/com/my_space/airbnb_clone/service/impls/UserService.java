package com.my_space.airbnb_clone.service.impls;

import com.my_space.airbnb_clone.dto.SignupRequestDto;
import com.my_space.airbnb_clone.dto.UserDto;
import com.my_space.airbnb_clone.dto.UserRequestDto;
import com.my_space.airbnb_clone.dto.UserResponseDto;
import com.my_space.airbnb_clone.entity.User;
import com.my_space.airbnb_clone.enums.UserRole;
import com.my_space.airbnb_clone.exceptions.ResourceNotFoundException;
import com.my_space.airbnb_clone.exceptions.ResourcePresent;
import com.my_space.airbnb_clone.mapper.UserMapper;
import com.my_space.airbnb_clone.repository.UserRepository;
import com.my_space.airbnb_clone.service.interfaces.IUserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @NonNull
    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
    }

    @Override
    public UserDto createUser(SignupRequestDto signupRequestDto) {
        boolean isExist = userRepository.existsByEmailIgnoreCase(signupRequestDto.getEmail());
        if (isExist) {
            throw new ResourcePresent("Email already exists");
        }
        User user = User.builder()
                .email(signupRequestDto.getEmail())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .name(signupRequestDto.getName())
                .roles(Set.of(UserRole.GUEST))
                .build();

        user = userRepository.save(user);
        return userMapper.userToUserDto(user);
    }

    @Override
    public UserResponseDto getLoggedInUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            throw new ResourceNotFoundException("user", "user");
        }
        return userMapper.userToUserResponseDto(user);
    }

    @Override
    public UserResponseDto updateProfile(UserRequestDto userRequestDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            throw new ResourceNotFoundException("user", "user");
        }
        user.setName(userRequestDto.getName());
        return userMapper.userToUserResponseDto(userRepository.save(user));
    }
}
