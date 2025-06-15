package ru.aston.user_service_spring_boot.exception;

public class UserSaveException extends Exception{
    public UserSaveException(String message) {
        super(message);
    }

    public UserSaveException() {
    }

    public UserSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserSaveException(Throwable cause) {
        super(cause);
    }
}
