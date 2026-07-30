package com.my_space.airbnb_clone.service.impls;

import com.my_space.airbnb_clone.dto.HotelResponseDto;
import com.my_space.airbnb_clone.dto.HotelSearchRequest;
import com.my_space.airbnb_clone.dto.InventoryResponseDto;
import com.my_space.airbnb_clone.dto.UpdateInventoryRequestDto;
import com.my_space.airbnb_clone.entity.Hotel;
import com.my_space.airbnb_clone.entity.Inventory;
import com.my_space.airbnb_clone.entity.Room;
import com.my_space.airbnb_clone.entity.User;
import com.my_space.airbnb_clone.enums.UserRole;
import com.my_space.airbnb_clone.exceptions.ResourceNotFoundException;
import com.my_space.airbnb_clone.mapper.HotelMapper;
import com.my_space.airbnb_clone.mapper.InventoryMapper;
import com.my_space.airbnb_clone.repository.InventoryRepository;
import com.my_space.airbnb_clone.repository.RoomRepository;
import com.my_space.airbnb_clone.service.interfaces.IInventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.my_space.airbnb_clone.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
public class InventoryService implements IInventoryService {
    private final InventoryRepository inventoryRepository;
    private final HotelMapper hotelMapper;
    private final RoomRepository roomRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional
    public void initializeRoomForAnYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);
        List<Inventory> inventories = new ArrayList<>();
        for ( ; !today.isAfter(endDate); today = today.plusDays(1)) {
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .date(today)
                    .bookedCount(0)
                    .reservedCount(0)
                    .totalCount(room.getTotalCount())
                    .surgeFactor(BigDecimal.ONE)
                    .price(room.getBasePrice())
                    .city(room.getHotel().getCity())
                    .closed(false)
                    .build();
            inventories.add(inventory);
        }
        inventoryRepository.saveAll(inventories);
    }

    @Override
    public void deleteAllInventoriesOfRoom(Room room) {
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelResponseDto> searchAvailableHotels(HotelSearchRequest hotelSearchRequest, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page-1, size);
        long daysCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate()) + 1;
        Page<Hotel> hotelPage = inventoryRepository.getAvailableHotels(
                hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                hotelSearchRequest.getRoomsCount(),
                daysCount,
                pageable
        );


        return hotelPage.map(hotelMapper::toHotelResponseDto);
    }

    @Override
    public void updateInventoryOfTheRoom(Room updatedRoom, BigDecimal updatedBasePrice) {
        inventoryRepository.updateInventoryOfUpdatedRoom(
                updatedRoom.getId(),
                updatedBasePrice
        );
    }

    @Override
    public List<InventoryResponseDto> getInventoriesOfRoom(Long roomId) {
        User user = getCurrentUser();
        if (!user.getRoles().contains(UserRole.HOTEL_MANAGER)) {
            throw new AccessDeniedException("You are not allowed to access this resource");
        }
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", roomId.toString()));
        if (!room.getHotel().getOwner().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("You are not owner of this resource");
        }
        List<Inventory> inventories = inventoryRepository.findByRoomAndClosedFalseOrderByDate(room);
        return inventoryMapper.toDto(inventories);
    }

    @Override
    @Transactional
    public void updateInventoryOfRoomForAdmin(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto) {
        User user = getCurrentUser();
        if (!user.getRoles().contains(UserRole.HOTEL_MANAGER)) {
            throw new AccessDeniedException("You are not allowed to access this resource");
        }
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", roomId.toString()));
        if (!room.getHotel().getOwner().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("You are not owner of this resource");
        }
        inventoryRepository.updateInventoryForAdmin(
                roomId,
                updateInventoryRequestDto.getSurgeFactor(),
                updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate().plusDays(1),
                updateInventoryRequestDto.getClosed()
        );
    }
}
