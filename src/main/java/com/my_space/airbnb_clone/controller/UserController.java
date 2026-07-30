package com.my_space.airbnb_clone.controller;

import com.my_space.airbnb_clone.dto.BookingResponseDto;
import com.my_space.airbnb_clone.dto.UserRequestDto;
import com.my_space.airbnb_clone.dto.UserResponseDto;
import com.my_space.airbnb_clone.service.interfaces.IBookingService;
import com.my_space.airbnb_clone.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final IUserService userService;
    private final IBookingService bookingService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getLoggedInUser() {
        return ResponseEntity.ok(userService.getLoggedInUser());
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponseDto> updateProfile(@RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.updateProfile(userRequestDto));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponseDto>> getBookings() {
        return ResponseEntity.ok(bookingService.getMyBookings());
    }
}
