package org.mabr.authenticationservice.dto;

public record JwtRequest(
        String username,
        String password
) {
}
