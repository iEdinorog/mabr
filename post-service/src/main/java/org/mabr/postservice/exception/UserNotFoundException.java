package org.mabr.postservice.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String username) {
        super(String.format("User not found with username %s", username));
    }
}
