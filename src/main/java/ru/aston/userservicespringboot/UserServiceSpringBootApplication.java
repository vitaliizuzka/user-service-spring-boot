package ru.aston.userservicespringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.aston.userservicespringboot.repository.UserRepository;

@SpringBootApplication
public class UserServiceSpringBootApplication {

    public static void main(String[] args) {
        var context =  SpringApplication.run(UserServiceSpringBootApplication.class, args);
        System.out.println();
        UserRepository userRepository;

    }

}
