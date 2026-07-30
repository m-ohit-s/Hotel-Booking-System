package com.my_space.airbnb_clone.controller;

import com.my_space.airbnb_clone.dto.BookingRequestDto;
import com.my_space.airbnb_clone.dto.BookingResponseDto;
import com.my_space.airbnb_clone.dto.GuestDto;
import com.my_space.airbnb_clone.service.interfaces.IBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController {
    private final IBookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@RequestBody BookingRequestDto bookingRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(bookingRequestDto));
    }

    @PostMapping("/{bookingId}/guests")
    public ResponseEntity<BookingResponseDto> addGuests(@RequestBody List<GuestDto> guestDtos, @PathVariable Long bookingId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.addGuests(guestDtos, bookingId));
    }

    @PostMapping("/payments/{bookingId}")
    public ResponseEntity<Map<String, String>> initiatePayment(@PathVariable Long bookingId) {
        String sessionUrl = bookingService.initiatePayments(bookingId);
        return ResponseEntity.ok(Map.of("sessionUrl", sessionUrl));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
}
