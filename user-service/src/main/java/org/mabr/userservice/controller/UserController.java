package org.mabr.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.mabr.userservice.dto.AuthUserDto;
import org.mabr.userservice.entity.User;
import org.mabr.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> createUser(@RequestBody AuthUserDto userDto) {
        service.createUser(userDto);

        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByName(@PathVariable String username) {
        var user = service.getUserByName(username);

        return ResponseEntity.ok(user);
    }
}
