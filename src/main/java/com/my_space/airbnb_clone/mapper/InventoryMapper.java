package com.my_space.airbnb_clone.mapper;

import com.my_space.airbnb_clone.dto.InventoryResponseDto;
import com.my_space.airbnb_clone.entity.Inventory;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
    List<InventoryResponseDto> toDto(List<Inventory> inventories);
}
