package com.my_space.airbnb_clone.service.interfaces;

import com.my_space.airbnb_clone.dto.HotelResponseDto;
import com.my_space.airbnb_clone.dto.HotelSearchRequest;
import com.my_space.airbnb_clone.dto.InventoryResponseDto;
import com.my_space.airbnb_clone.dto.UpdateInventoryRequestDto;
import com.my_space.airbnb_clone.entity.Room;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface IInventoryService {

    void initializeRoomForAnYear(Room room);

    void deleteAllInventoriesOfRoom(Room room);

    Page<HotelResponseDto> searchAvailableHotels(HotelSearchRequest hotelSearchRequest, Integer page, Integer size);

    void updateInventoryOfTheRoom(Room updatedRoom, BigDecimal updatedBasePrice);

    List<InventoryResponseDto> getInventoriesOfRoom(Long roomId);

    void updateInventoryOfRoomForAdmin(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto);
}
