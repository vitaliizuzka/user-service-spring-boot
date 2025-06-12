package ru.aston.userservicespringboot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.aston.userservicespringboot.model.dto.UserCreateReadDto;
import ru.aston.userservicespringboot.model.dto.UserDto;
import ru.aston.userservicespringboot.model.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    UserDto toUserDto(User user);
    UserCreateReadDto toUserCreateReadDto(User user);
    User toUser(UserDto userDto);
    User toUser(UserCreateReadDto userCreateReadDto);

    List<UserCreateReadDto> toUserCreateReadDtoList(List<User> users);

}
