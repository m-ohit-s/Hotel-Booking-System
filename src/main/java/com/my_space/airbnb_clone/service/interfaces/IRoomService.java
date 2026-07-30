package com.my_space.airbnb_clone.service.interfaces;

import com.my_space.airbnb_clone.dto.RoomRequestDto;
import com.my_space.airbnb_clone.dto.RoomResponseDto;

import java.util.List;

public interface IRoomService {
    RoomResponseDto createRoom(RoomRequestDto roomRequestDto, Long hotelId);

    RoomResponseDto getRoomById(Long id, Long hotelId);

    RoomResponseDto updateRoomById(Long hotelId, Long id, RoomRequestDto roomRequestDto);

    void deleteRoomById(Long hotelId, Long id);

    List<RoomResponseDto> getRoomsOfHotel(Long hotelId);

    List<RoomResponseDto> createRooms(List<RoomRequestDto> roomRequestDtos, Long hotelId);
}
