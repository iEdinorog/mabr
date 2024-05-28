package org.mabr.userservice.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String username) {
        super(String.format("User not found with username {}", username));
    }
}
