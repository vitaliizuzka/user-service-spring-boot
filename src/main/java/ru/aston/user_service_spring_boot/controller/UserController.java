package ru.aston.user_service_spring_boot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.aston.user_service_spring_boot.exception.UserDeleteException;
import ru.aston.user_service_spring_boot.exception.UserSaveException;
import ru.aston.user_service_spring_boot.exception.UserUpdateException;
import ru.aston.user_service_spring_boot.model.assembler.UserModelAssembler;
import ru.aston.user_service_spring_boot.model.dto.UserCreateReadDto;
import ru.aston.user_service_spring_boot.model.dto.UserDto;
import ru.aston.user_service_spring_boot.service.UserService;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/users")
@Tag(name = "User Controller", description = "CRUD operations for users")
public class UserController {

    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    private final UserModelAssembler assembler;

    @Autowired
    public UserController(UserService userService, UserModelAssembler assembler) {
        this.userService = userService;
        this.assembler = assembler;
    }

    @Operation(summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No users found")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserCreateReadDto>>> findAll() {
        LOGGER.info("trying to find all users controller level");
        try {
            List<UserCreateReadDto> users = userService.findAll();
            List<EntityModel<UserCreateReadDto>> userModels = users.stream()
                    .map(assembler::toModel)
                    .toList();
            CollectionModel<EntityModel<UserCreateReadDto>> collectionModel = CollectionModel.of(userModels,
                    linkTo(methodOn(UserController.class).findAll()).withSelfRel());
            LOGGER.info("users found successfully service level");

            return ResponseEntity.ok(collectionModel);
        } catch (Exception e) {
            LOGGER.error("users weren't found service level", e);
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(summary = "Get user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "204", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserCreateReadDto>> findById(@PathVariable Integer id) {
        LOGGER.info("trying to find user by id {} controller level", id);
        return userService.findById(id).map(user -> {
            LOGGER.info("user found successfully controller level");
            return ResponseEntity.ok(assembler.toModel(user));
        }).orElseGet(() -> {
            LOGGER.error("user not found controller level");
            return ResponseEntity.noContent().build();
        });
    }

    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "500", description = "Error occurred while saving user")
    })
    @PostMapping
    public ResponseEntity<Object> create(@RequestBody UserCreateReadDto userCreateReadDto) {
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

    @Operation(summary = "Delete user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
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

    @Operation(summary = "Update user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "500", description = "Error occurred while updating user")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody UserCreateReadDto userCreateReadDto) {
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
