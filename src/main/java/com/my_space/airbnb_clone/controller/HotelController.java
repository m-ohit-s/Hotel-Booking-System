package com.my_space.airbnb_clone.controller;

import com.my_space.airbnb_clone.dto.BookingResponseDto;
import com.my_space.airbnb_clone.dto.HotelReportDto;
import com.my_space.airbnb_clone.dto.HotelRequestDto;
import com.my_space.airbnb_clone.dto.HotelResponseDto;
import com.my_space.airbnb_clone.service.interfaces.IHotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/hotels")
public class HotelController {
    private final IHotelService hotelService;

    @PostMapping
    ResponseEntity<List<HotelResponseDto>> createHotels(@RequestBody List<HotelRequestDto> hotelRequestDtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.createHotels(hotelRequestDtos));
    }

    @PostMapping("/hotel")
    ResponseEntity<HotelResponseDto> createHotel(@RequestBody HotelRequestDto hotelRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.createHotel(hotelRequestDto));
    }

    @PutMapping("/{hotelId}")
    ResponseEntity<HotelResponseDto> updateHotel(@PathVariable Long hotelId, @RequestBody HotelRequestDto hotelRequestDto) {
        return ResponseEntity.ok(hotelService.updateHotelById(hotelId, hotelRequestDto));
    }

    @DeleteMapping("/{hotelId}")
    ResponseEntity<Void> deleteHotelById(@PathVariable Long hotelId) {
        hotelService.deleteHotelById(hotelId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/active/{hotelId}")
    ResponseEntity<Void> activateHotel(@PathVariable Long hotelId) {
        hotelService.activateHotel(hotelId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/inactive/{hotelId}")
    ResponseEntity<Void> deactivateHotel(@PathVariable Long hotelId) {
        hotelService.deactivateHotelById(hotelId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping
    ResponseEntity<Page<HotelResponseDto>> getAllHotels(
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return ResponseEntity.ok(hotelService.getAllHotels(
                active,
                page,
                pageSize
        ));
    }

    @GetMapping("/{hotelId}/bookings")
    ResponseEntity<Page<BookingResponseDto>> getAllBookingsOfHotel(
            @PathVariable Long hotelId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return ResponseEntity.ok(hotelService.getBookingsOfHotel(hotelId, page, pageSize));
    }

    @GetMapping("/{hotelId}/report")
    ResponseEntity<HotelReportDto> getHotelReport(
            @PathVariable Long hotelId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
            ) {
        return ResponseEntity.ok(hotelService.getHotelReport(
                hotelId,
                startDate,
                endDate
        ));
    }
}
