package ru.aston.userservicespringboot.service;

import ru.aston.userservicespringboot.exception.UserDeleteException;
import ru.aston.userservicespringboot.exception.UserFindAllException;
import ru.aston.userservicespringboot.exception.UserSaveException;
import ru.aston.userservicespringboot.exception.UserUpdateException;
import ru.aston.userservicespringboot.model.dto.UserCreateReadDto;
import ru.aston.userservicespringboot.model.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto save(UserCreateReadDto createUserDto) throws UserSaveException;
    List<UserCreateReadDto> findAll() throws UserFindAllException;
    Optional<UserCreateReadDto> findById(Integer id);
    UserDto update(Integer id, UserCreateReadDto createReadDto) throws UserUpdateException;
    void removeById(Integer id) throws UserDeleteException;
}
