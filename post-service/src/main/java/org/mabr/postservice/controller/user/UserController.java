package org.mabr.postservice.controller.user;

import org.mabr.postservice.dto.user.AuthUserDto;
import org.mabr.postservice.dto.user.UserProfile;
import org.mabr.postservice.entity.security.User;
import org.mabr.postservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("{username}")
    public ResponseEntity<User> getUserProfile(@PathVariable String username) {
        var user = userService.getUserProfile(username);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody AuthUserDto authUserDto) {
        return userService.create(authUserDto);
    }

    @PostMapping
    public ResponseEntity<User> update(@RequestBody UserProfile user) {
        return ResponseEntity.ok(userService.updateInfo(user));
    }

    @PostMapping("/{username}/avatar")
    public ResponseEntity<User> updateAvatar(@PathVariable String username, @RequestBody String imageLink) {
        var user = userService.updateAvatar(username, imageLink);
        return ResponseEntity.ok(user);
    }
}
