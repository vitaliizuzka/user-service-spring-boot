package ru.aston.userservicespringboot;

import org.springframework.boot.SpringApplication;

public class TestUserServiceSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.from(UserServiceSpringBootApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
