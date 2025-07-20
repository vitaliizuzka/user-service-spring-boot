package ru.aston.user_service_spring_boot.model.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ru.aston.user_service_spring_boot.controller.UserController;
import ru.aston.user_service_spring_boot.model.dto.UserCreateReadDto;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserCreateReadDto, EntityModel<UserCreateReadDto>> {
    @Override
    public EntityModel<UserCreateReadDto> toModel(UserCreateReadDto user) {
        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).findById(user.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).findAll()).withRel("users")
        );
    }
}
