package ru.aston.user_service_spring_boot.model.dto;

public record UserCreateReadDto (
        Integer id,
        String name,
        String email,
        Integer age
) {
}
