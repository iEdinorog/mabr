package org.mabr.authenticationservice.controller;

import lombok.RequiredArgsConstructor;
import org.mabr.authenticationservice.dto.UserDto;
import org.mabr.authenticationservice.entity.User;
import org.mabr.authenticationservice.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> createUser(@RequestBody UserDto userDto) {
        var user = new User();
        user.setUsername(userDto.username());
        user.setPassword(passwordEncoder.encode(userDto.password()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
