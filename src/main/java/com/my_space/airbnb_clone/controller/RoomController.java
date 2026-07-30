package com.my_space.airbnb_clone.controller;

import com.my_space.airbnb_clone.dto.RoomRequestDto;
import com.my_space.airbnb_clone.dto.RoomResponseDto;
import com.my_space.airbnb_clone.service.interfaces.IRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/hotel/{hotelId}/room")
public class RoomController {
    private final IRoomService roomService;

    @PostMapping("/rooms")
    public ResponseEntity<List<RoomResponseDto>> createRooms(@RequestBody List<RoomRequestDto> roomRequestDtos, @PathVariable Long hotelId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRooms(roomRequestDtos, hotelId));
    }

    @PostMapping
    public ResponseEntity<RoomResponseDto> createRoom(@RequestBody RoomRequestDto roomRequestDto, @PathVariable Long hotelId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(roomRequestDto, hotelId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDto> updateRoom(@PathVariable Long id, @RequestBody RoomRequestDto roomRequestDto, @PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.updateRoomById(hotelId, id, roomRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id, @PathVariable Long hotelId) {
        roomService.deleteRoomById(hotelId, id);
        return ResponseEntity.ok().build();
    }
}
