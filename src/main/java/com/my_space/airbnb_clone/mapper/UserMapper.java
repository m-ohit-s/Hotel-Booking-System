package com.my_space.airbnb_clone.mapper;

import com.my_space.airbnb_clone.dto.UserDto;
import com.my_space.airbnb_clone.dto.UserResponseDto;
import com.my_space.airbnb_clone.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto userToUserDto(User user);
    User userDtoToUser(UserDto userDto);
    UserResponseDto userToUserResponseDto(User user);
}
