package ru.aston.user_service_spring_boot.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.aston.user_service_spring_boot.exception.*;
import ru.aston.user_service_spring_boot.mapper.UserMapper;
import ru.aston.user_service_spring_boot.model.UserKafkaEvents;
import ru.aston.user_service_spring_boot.model.dto.UserCreateReadDto;
import ru.aston.user_service_spring_boot.model.dto.UserDto;
import ru.aston.user_service_spring_boot.model.entity.User;
import ru.aston.user_service_spring_boot.repository.UserRepository;
import ru.aston.user_service_spring_boot.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class UserServiceImpl implements UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional(rollbackFor = UserSaveException.class)
    public UserDto save(UserCreateReadDto userCreateReadDto) throws UserSaveException {
        User user = UserMapper.INSTANCE.toUser(userCreateReadDto);
        LOGGER.info("trying to save user {} service level", userCreateReadDto);
        try {
            UserDto userDto = UserMapper.INSTANCE.toUserDto(userRepository.save(user));

            sendAsyncKafkaMessage(UserKafkaEvents.CREATE, userDto.email());

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
            User deleteUser = userRepository.findById(id).map(user -> user).orElseThrow(() -> new UserDeleteException());
            userRepository.deleteById(id);

            sendAsyncKafkaMessage(UserKafkaEvents.DELETE, deleteUser.getEmail());

            LOGGER.info("user was deleted successfully service level");
        } catch (Exception e) {
            LOGGER.error("user wasn't to deleted service level", e);
            throw new UserDeleteException(e);
        }
    }

    public void sendAsyncKafkaMessage(UserKafkaEvents userKafkaEvents, String email){
        String kafkaMessage = userKafkaEvents + ":" + email;
        LOGGER.info("trying to send message to kafka {}", kafkaMessage);
        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send("user-create-delete-events-topic", null, kafkaMessage);
        future.whenComplete((result, exception) -> {
            if (exception != null) {
                LOGGER.error("Failed to send message to kafka {}", exception.getMessage());
            } else {
                LOGGER.info("Message sent to kafka successfully {}", result.getRecordMetadata());
            }
        });
    }
}
