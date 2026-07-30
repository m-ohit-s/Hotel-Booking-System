package com.my_space.airbnb_clone.mapper;

import com.my_space.airbnb_clone.dto.RoomRequestDto;
import com.my_space.airbnb_clone.dto.RoomResponseDto;
import com.my_space.airbnb_clone.entity.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    RoomResponseDto roomToRoomResponseDto(Room room);
    Room roomRequestDtoToRoom(RoomRequestDto roomRequestDto);
}
