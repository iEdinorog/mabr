package org.mabr.userservice.exception;

public class UserAlreadyExistException extends RuntimeException {

    public UserAlreadyExistException(String username) {
        super(String.format("User {} already exist", username));
    }
}
