package com.my_space.airbnb_clone.dto;

import com.my_space.airbnb_clone.enums.BookingStatus;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Data
public class BookingResponseDto {
    private Long id;
    private String userEmail;
    private Integer roomsCount;
    private LocalDate checkInDate;
    private LocalDate checkOut;
    private BookingStatus bookingStatus;
    private Set<GuestDto> guests;
    private Instant createdAt;
}
