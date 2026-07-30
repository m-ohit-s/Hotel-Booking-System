package com.my_space.airbnb_clone.mapper;

import com.my_space.airbnb_clone.dto.GuestDto;
import com.my_space.airbnb_clone.entity.Guest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GuestMapper {
    Guest GuestDtoToGuest(GuestDto guestDto);
    GuestDto GuestToGuestDto(Guest guest);
}
