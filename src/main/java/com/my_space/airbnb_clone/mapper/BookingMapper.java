package com.my_space.airbnb_clone.mapper;

import com.my_space.airbnb_clone.dto.BookingResponseDto;
import com.my_space.airbnb_clone.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "userEmail", source = "user.email")
    BookingResponseDto bookingToBookingResponse(Booking booking);
}
