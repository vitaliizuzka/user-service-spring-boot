package ru.aston.user_service_spring_boot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.aston.user_service_spring_boot.exception.UserDeleteException;
import ru.aston.user_service_spring_boot.exception.UserSaveException;
import ru.aston.user_service_spring_boot.exception.UserUpdateException;
import ru.aston.user_service_spring_boot.model.dto.UserCreateReadDto;
import ru.aston.user_service_spring_boot.model.dto.UserDto;
import ru.aston.user_service_spring_boot.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserCreateReadDto>> findAll() {
        LOGGER.info("trying to find all users controller level");
        try {
            List<UserCreateReadDto> users = userService.findAll();
            LOGGER.info("users found successfully service level");
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            LOGGER.error("users weren't found service level", e);
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserCreateReadDto> findById(@PathVariable Integer id) {
        LOGGER.info("trying to find user by id {} controller level", id);
        return userService.findById(id).map(user -> {
            LOGGER.info("user found successfully controller level");
            return ResponseEntity.ok(user);
        }).orElseGet(() -> {
            LOGGER.error("user not found controller level");
            return ResponseEntity.noContent().build();
        });
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody UserCreateReadDto userCreateReadDto){
        LOGGER.info("trying to save user {} controller level", userCreateReadDto);
        try {
            UserDto userDto = userService.save(userCreateReadDto);
            LOGGER.info("user saved successfully controller level");
            return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
        } catch (UserSaveException e) {
            LOGGER.error("user not saved controller level", e);
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getClass().getSimpleName());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id){
        LOGGER.info("trying to delete user by id {} controller level", id);
        try {
            userService.removeById(id);
            LOGGER.info("user was deleted successfully controller level");
            return ResponseEntity.noContent().build();
        } catch (UserDeleteException e) {
            LOGGER.error("user wasn't to deleted controller level", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody UserCreateReadDto userCreateReadDto){
        LOGGER.info("trying to change user by id {} controller level", id);
        try {
            UserDto updatedUserDto = userService.update(id, userCreateReadDto);
            LOGGER.info("user was saved or updated successfully controller level: {}", updatedUserDto);
            return ResponseEntity.ok(updatedUserDto);
        } catch (UserUpdateException e) {
            LOGGER.error("user wasn't updated or save controller level", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getClass().getSimpleName());
        }
    }

}
