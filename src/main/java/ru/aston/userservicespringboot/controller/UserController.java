package ru.aston.userservicespringboot.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.aston.userservicespringboot.exception.UserDeleteException;
import ru.aston.userservicespringboot.exception.UserSaveException;
import ru.aston.userservicespringboot.exception.UserUpdateException;
import ru.aston.userservicespringboot.model.dto.UserCreateReadDto;
import ru.aston.userservicespringboot.model.dto.UserDto;
import ru.aston.userservicespringboot.service.UserService;
import ru.aston.userservicespringboot.service.impl.UserServiceImpl;

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
        LOGGER.info("try to find all users controller level");
        try {
            List<UserCreateReadDto> users = userService.findAll();
            LOGGER.info("users was found successfully service level");
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            LOGGER.error("users wasn't found service level", e);
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserCreateReadDto> findById(@PathVariable Integer id) {
        LOGGER.info("try to find user by id {} controller level", id);
        return userService.findById(id).map(user -> {
            LOGGER.info("user found successfully controller level");
            return ResponseEntity.ok(user);
        }).orElseGet(() -> {
            LOGGER.error("user wasn't updated or save controller level");
            return ResponseEntity.noContent().build();
        });
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody UserCreateReadDto userCreateReadDto){
        LOGGER.info("try to save user {} controller level", userCreateReadDto);
        try {
            UserDto userDto = userService.save(userCreateReadDto);
            LOGGER.info("user saved successfully controller level");
            return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
        } catch (UserSaveException e) {
            LOGGER.error("user wasn't save controller level", e);
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getClass().getSimpleName());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id){
        LOGGER.info("try to delete user by id {} controller level", id);
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
        LOGGER.info("try to change user by id {} controller level", id);
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
