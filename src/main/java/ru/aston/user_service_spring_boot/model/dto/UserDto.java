package ru.aston.user_service_spring_boot.model.dto;

import java.time.LocalDateTime;

public record UserDto (
        Integer id,
        String name,
        String email,
        Integer age,
        LocalDateTime createdAt
){
}
