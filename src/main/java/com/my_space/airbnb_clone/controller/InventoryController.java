package com.my_space.airbnb_clone.controller;

import com.my_space.airbnb_clone.dto.InventoryResponseDto;
import com.my_space.airbnb_clone.dto.RoomResponseDto;
import com.my_space.airbnb_clone.dto.UpdateInventoryRequestDto;
import com.my_space.airbnb_clone.service.impls.InventoryService;
import com.my_space.airbnb_clone.service.interfaces.IInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final IInventoryService inventoryService;

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<InventoryResponseDto>> getInventoryOfRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(inventoryService.getInventoriesOfRoom(roomId));
    }

    @PatchMapping("/room/{roomId}")
    public ResponseEntity<Void> updateInventoryOfRoom(@PathVariable Long roomId, @RequestBody UpdateInventoryRequestDto updateInventoryRequestDto) {
        inventoryService.updateInventoryOfRoomForAdmin(roomId, updateInventoryRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
