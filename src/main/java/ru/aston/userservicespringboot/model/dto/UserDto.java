package ru.aston.userservicespringboot.model.dto;

import java.time.LocalDateTime;

public record UserDto (
        Integer id,
        String name,
        String email,
        Integer age,
        LocalDateTime createdAt
){
}
