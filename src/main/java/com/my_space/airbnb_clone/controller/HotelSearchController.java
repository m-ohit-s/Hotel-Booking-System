package com.my_space.airbnb_clone.controller;

import com.my_space.airbnb_clone.dto.*;
import com.my_space.airbnb_clone.service.interfaces.IHotelSearchService;
import com.my_space.airbnb_clone.service.interfaces.IHotelService;
import com.my_space.airbnb_clone.service.interfaces.IInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("hotel/search")
public class HotelSearchController {
    private final IHotelService hotelService;
    private final IInventoryService inventoryService;
    private final IHotelSearchService hotelSearchService;

    @GetMapping("/{id}")
    ResponseEntity<HotelResponseDto> getHotelById(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @PostMapping("/with-room-availablity")
    ResponseEntity<Page<HotelResponseDto>> getAllFilteredHotelsWithRoomAvailability(
            @RequestBody HotelSearchRequest hotelSearchRequest,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Page<HotelResponseDto> searchedPage = inventoryService.searchAvailableHotels(hotelSearchRequest, page, size);
        return ResponseEntity.ok(searchedPage);
    }

    @PostMapping
    ResponseEntity<Page<HotelMinPriceDto>> getAllFilteredHotels(
            @RequestBody HotelSearchRequest hotelSearchRequest,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Page<HotelMinPriceDto> searchedPage = hotelSearchService.searchAvailableHotels(hotelSearchRequest, page, size);
        return ResponseEntity.ok(searchedPage);
    }

    @GetMapping("/info/{id}")
    ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotelInfo(id));
    }
}
