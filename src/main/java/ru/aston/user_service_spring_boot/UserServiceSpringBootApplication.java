package ru.aston.user_service_spring_boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceSpringBootApplication {

    public static void main(String[] args) {
        var context =  SpringApplication.run(UserServiceSpringBootApplication.class, args);
    }

}
