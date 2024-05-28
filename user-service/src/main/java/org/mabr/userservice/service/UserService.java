package org.mabr.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mabr.userservice.dto.AuthUserDto;
import org.mabr.userservice.entity.User;
import org.mabr.userservice.exception.UserAlreadyExistException;
import org.mabr.userservice.exception.UserNotFoundException;
import org.mabr.userservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public User getUserByName(String username) {
        return repository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    public void createUser(AuthUserDto userDto) {
        var existUser = repository.findByUsername(userDto.username());
        if (existUser.isPresent()) {
            throw new UserAlreadyExistException(userDto.username());
        }

        var user = new User();
        user.setUsername(userDto.username());
        user.setPassword(passwordEncoder.encode(userDto.password()));

        repository.save(user);
    }
}
