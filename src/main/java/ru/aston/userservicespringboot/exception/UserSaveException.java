package ru.aston.userservicespringboot.exception;

public class UserSaveException extends Exception{
    public UserSaveException(String message) {
        super(message);
    }

    public UserSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserSaveException(Throwable cause) {
        super(cause);
    }
}
