package ru.aston.user_service_spring_boot.model.dto;

public record UserCreateReadDto(
        String name,
        String email,
        Integer age
) {
}
