package ru.aston.userservicespringboot.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.aston.userservicespringboot.exception.*;
import ru.aston.userservicespringboot.mapper.UserMapper;
import ru.aston.userservicespringboot.model.dto.UserCreateReadDto;
import ru.aston.userservicespringboot.model.dto.UserDto;
import ru.aston.userservicespringboot.model.entity.User;
import ru.aston.userservicespringboot.repository.UserRepository;
import ru.aston.userservicespringboot.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(rollbackFor = UserSaveException.class)
    public UserDto save(UserCreateReadDto userCreateReadDto) throws UserSaveException {
        User user = UserMapper.INSTANCE.toUser(userCreateReadDto);
        LOGGER.info("trying to save user {} service level", userCreateReadDto);
        try {
            UserDto userDto = UserMapper.INSTANCE.toUserDto(userRepository.save(user));
            LOGGER.info("user saved successfully service level");
            return userDto;
        } catch (Exception e) {
            LOGGER.error("user not saved service level", e);
            throw new UserSaveException(e);
        }
    }

    @Override
    @Transactional
    public List<UserCreateReadDto> findAll() throws UserFindAllException {
        LOGGER.info("trying to find all users service level");
        try {
            List<UserCreateReadDto> users = UserMapper.INSTANCE.toUserCreateReadDtoList(userRepository.findAll());
            LOGGER.info("users was found successfully service level");
            return users;
        } catch (Exception e) {
            LOGGER.error("users weren't found service level", e);
            throw new UserFindAllException(e);
        }
    }

    @Override
    @Transactional
    public Optional<UserCreateReadDto> findById(Integer id) {
        LOGGER.info("trying to find user by id {} service level", id);
        return userRepository.findById(id).map(user -> {
            Optional<UserCreateReadDto> optionalUserCreateReadDto = Optional.of(UserMapper.INSTANCE.toUserCreateReadDto(user));
            LOGGER.info("user found successfully service level");
            return optionalUserCreateReadDto;
        }).orElseGet(() -> {
            LOGGER.error("user not found service level");
            return Optional.empty();
        });

    }

    @Override
    @Transactional(rollbackFor = UserUpdateException.class)
    public UserDto update(Integer id, UserCreateReadDto userCreateReadDto) throws UserUpdateException {
        LOGGER.info("trying to change user by id {} service level", id);
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isEmpty()) throw new UserNotFoundException();
            User user = optionalUser.get();
            if (userCreateReadDto.name() != null) user.setName(userCreateReadDto.name());
            if (userCreateReadDto.email() != null) user.setEmail(userCreateReadDto.email());
            if (userCreateReadDto.age() != null) user.setAge(userCreateReadDto.age());
            UserDto updatedUserDto = UserMapper.INSTANCE.toUserDto(userRepository.save(user));
            LOGGER.info("user was saved or updated successfully service level: {}", updatedUserDto);
            return updatedUserDto;
        } catch (Exception e) {
            LOGGER.error("user wasn't updated or save service level", e);
            throw new UserUpdateException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = UserDeleteException.class)
    public void removeById(Integer id) throws UserDeleteException {
        LOGGER.info("trying to delete user by id {} service level", id);
        try {
            userRepository.deleteById(id);
            LOGGER.info("user was deleted successfully service level");
        } catch (Exception e) {
            LOGGER.error("user wasn't to deleted service level", e);
            throw new UserDeleteException(e);
        }
    }
}
