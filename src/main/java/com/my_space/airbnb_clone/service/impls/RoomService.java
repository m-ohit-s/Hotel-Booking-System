package com.my_space.airbnb_clone.service.impls;

import com.my_space.airbnb_clone.dto.RoomRequestDto;
import com.my_space.airbnb_clone.dto.RoomResponseDto;
import com.my_space.airbnb_clone.entity.Hotel;
import com.my_space.airbnb_clone.entity.Room;
import com.my_space.airbnb_clone.entity.User;
import com.my_space.airbnb_clone.enums.UserRole;
import com.my_space.airbnb_clone.exceptions.ResourceNotFoundException;
import com.my_space.airbnb_clone.mapper.RoomMapper;
import com.my_space.airbnb_clone.repository.HotelRepository;
import com.my_space.airbnb_clone.repository.RoomRepository;
import com.my_space.airbnb_clone.service.interfaces.IInventoryService;
import com.my_space.airbnb_clone.service.interfaces.IRoomService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService implements IRoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomMapper roomMapper;
    private final IInventoryService inventoryService;

    @Override
    @Transactional
    public RoomResponseDto createRoom(RoomRequestDto roomRequestDto, Long hotelId) {
        checkUserIsManager();
        Hotel hotel = getHotelById(hotelId);
        Room room = roomMapper.roomRequestDtoToRoom(roomRequestDto);
        room.setHotel(hotel);
        room = roomRepository.save(room);
        if (room.getHotel().getActive()) {
            createInventoryOfRoom(room);
        }
        return roomMapper.roomToRoomResponseDto(room);
    }

    @Override
    public RoomResponseDto getRoomById(Long id, Long hotelId) {
        checkUserIsManager();
        isHotelValid(hotelId);
        Room room = getRoomById(id);
        return roomMapper.roomToRoomResponseDto(room);
    }

    @Override
    @Transactional
    public RoomResponseDto updateRoomById(Long hotelId, Long id, RoomRequestDto roomRequestDto) {
        checkUserIsManager();
        isHotelValid(hotelId);
        Room room = getRoomById(id);
        Room updatedRoom = roomMapper.roomRequestDtoToRoom(roomRequestDto);
        updatedRoom.setId(room.getId());
        updatedRoom.setHotel(room.getHotel());
        if (!room.getBasePrice().equals(updatedRoom.getBasePrice()) && room.getHotel().getActive()) {
            inventoryService.updateInventoryOfTheRoom(
                    updatedRoom,
                    updatedRoom.getBasePrice()
            );
        }
        updatedRoom = roomRepository.save(updatedRoom);
        return roomMapper.roomToRoomResponseDto(updatedRoom);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long hotelId, Long id) {
        checkUserIsManager();
        isHotelValid(hotelId);
        Room room = getRoomById(id);
        inventoryService.deleteAllInventoriesOfRoom(room);
        roomRepository.delete(room);
    }

    @Override
    public List<RoomResponseDto> getRoomsOfHotel(Long hotelId) {
        Hotel hotel = getHotelById(hotelId);
        List<Room> rooms = roomRepository.findByHotel(hotel);
        return rooms.stream().map(roomMapper::roomToRoomResponseDto).collect(Collectors.toList());
    }

    @Override
    public List<RoomResponseDto> createRooms(List<RoomRequestDto> roomRequestDtos, Long hotelId) {
        checkUserIsManager();
        return roomRequestDtos.stream().map(roomRequestDto -> createRoom(roomRequestDto, hotelId)).toList();
    }

    /// Utility Methods

    private Hotel getHotelById(Long hotelId) {
        return hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("hotel", hotelId.toString()));
    }

    private void isHotelValid(Long hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new ResourceNotFoundException("hotel", hotelId.toString());
        }
    }

    private Room getRoomById(Long roomId) {
        return roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("room", roomId.toString()));
    }

    private void createInventoryOfRoom(Room room) {
        inventoryService.initializeRoomForAnYear(room);
    }

    private User checkUserIsManager() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null || user.getRoles() == null || !user.getRoles().contains(UserRole.HOTEL_MANAGER)) {
            throw new AuthorizationDeniedException("You are not authorized to perform this action");
        }
        return user;
    }
}
