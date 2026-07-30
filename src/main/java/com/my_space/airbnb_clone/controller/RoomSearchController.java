package com.my_space.airbnb_clone.controller;

import com.my_space.airbnb_clone.dto.RoomResponseDto;
import com.my_space.airbnb_clone.service.interfaces.IRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hotel/{hotelId}/room/search")
public class RoomSearchController {
    private final IRoomService roomService;

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDto> getRoom(@PathVariable Long id, @PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.getRoomById(id, hotelId));
    }

    @GetMapping
    public ResponseEntity<List<RoomResponseDto>> getRooms(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.getRoomsOfHotel(hotelId));
    }
}
