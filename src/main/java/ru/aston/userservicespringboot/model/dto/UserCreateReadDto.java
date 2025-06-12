package ru.aston.userservicespringboot.model.dto;

import java.time.LocalDateTime;

public record UserCreateReadDto(
        String name,
        String email,
        Integer age
) {
}
