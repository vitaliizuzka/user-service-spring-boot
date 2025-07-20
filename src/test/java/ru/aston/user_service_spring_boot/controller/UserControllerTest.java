package ru.aston.user_service_spring_boot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.aston.user_service_spring_boot.exception.UserDeleteException;
import ru.aston.user_service_spring_boot.exception.UserSaveException;
import ru.aston.user_service_spring_boot.exception.UserUpdateException;
import ru.aston.user_service_spring_boot.model.dto.UserCreateReadDto;
import ru.aston.user_service_spring_boot.model.dto.UserDto;
import ru.aston.user_service_spring_boot.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void findAllShouldSizeEqualsTwo() throws Exception {
        List<UserCreateReadDto> users = List.of(
                new UserCreateReadDto(1,"Ignat", "ignat@gmail.com", 27),
                new UserCreateReadDto(2,"Vika", "vika@mail.ru", 30)
        );

        Mockito.when(userService.findAll()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void findAllShouldNoContent() throws Exception {
        Mockito.when(userService.findAll()).thenThrow(new RuntimeException());

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findByIdShouldFindUser() throws Exception {
        UserCreateReadDto user = new UserCreateReadDto(1,"Ignat", "ignat@gmail.com", 35);
        Mockito.when(userService.findById(1)).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ignat"))
                .andExpect(jsonPath("$.email").value("ignat@gmail.com"));
    }

    @Test
    void findByIdShouldNotFindUser() throws Exception {
        Mockito.when(userService.findById(10)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void createShouldCreateUser() throws Exception {
        UserCreateReadDto userCreateReadDto = new UserCreateReadDto(1, "Ignat", "ignat@gmail.com", 35);
        UserDto createdDto = new UserDto(1, "Ignat", "ignat@gmail.com", 35, LocalDateTime.now());

        Mockito.when(userService.save(userCreateReadDto)).thenReturn(createdDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateReadDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ignat"))
                .andExpect(jsonPath("$.email").value("ignat@gmail.com"));
    }

    @Test
    void createShouldNotCreateUser() throws Exception {
        UserCreateReadDto userCreateReadDto = new UserCreateReadDto(1, "Ignat", "ignat@gmail.com", 35);

        Mockito.when(userService.save(userCreateReadDto))
                .thenThrow(new UserSaveException());

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateReadDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("UserSaveException"));
    }

    @Test
    void deleteByIdShouldDeleteUser() throws Exception {

        Mockito.doNothing().when(userService).removeById(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteByIdShouldNotDeleteUser() throws Exception {
        Mockito.doThrow(new UserDeleteException()).when(userService).removeById(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateShouldUpdateUser() throws Exception {
        UserCreateReadDto userCreateReadDto = new UserCreateReadDto(1,"Vik", "vik@gmail.com", 78);
        UserDto updatedDto = new UserDto(1, "Vik", "vik@gmail.com", 78, LocalDateTime.now());

        Mockito.when(userService.update(1, userCreateReadDto))
                .thenReturn(updatedDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateReadDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Vik"))
                .andExpect(jsonPath("$.email").value("vik@gmail.com"))
                .andExpect(jsonPath("$.age").value(78));
    }

    @Test
    void updateShouldNotUpdateUser() throws Exception {
        UserCreateReadDto userCreateReadDto = new UserCreateReadDto(1, "Vik", "vik@gmail.com", 78);

        Mockito.when(userService.update(10, userCreateReadDto))
                .thenThrow(new UserUpdateException());

        mockMvc.perform(MockMvcRequestBuilders.put("/users/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateReadDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("UserUpdateException"));
    }


}