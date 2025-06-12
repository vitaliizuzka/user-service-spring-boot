package ru.aston.userservicespringboot.exception;

public class UserNotFoundException  extends Exception{
    public UserNotFoundException() {
    }

    public UserNotFoundException(Throwable cause) {
        super(cause);
    }
}
