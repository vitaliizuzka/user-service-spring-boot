package ru.aston.user_service_spring_boot.service;

import ru.aston.user_service_spring_boot.exception.UserDeleteException;
import ru.aston.user_service_spring_boot.exception.UserFindAllException;
import ru.aston.user_service_spring_boot.exception.UserSaveException;
import ru.aston.user_service_spring_boot.exception.UserUpdateException;
import ru.aston.user_service_spring_boot.model.dto.UserCreateReadDto;
import ru.aston.user_service_spring_boot.model.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto save(UserCreateReadDto createUserDto) throws UserSaveException;
    List<UserCreateReadDto> findAll() throws UserFindAllException;
    Optional<UserCreateReadDto> findById(Integer id);
    UserDto update(Integer id, UserCreateReadDto createReadDto) throws UserUpdateException;
    void removeById(Integer id) throws UserDeleteException;
}
