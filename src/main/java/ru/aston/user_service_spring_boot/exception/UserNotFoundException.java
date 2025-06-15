package ru.aston.user_service_spring_boot.exception;

public class UserNotFoundException  extends Exception{
    public UserNotFoundException() {
    }

    public UserNotFoundException(Throwable cause) {
        super(cause);
    }
}
