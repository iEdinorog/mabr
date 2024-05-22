package org.mabr.postservice.service.security;

import org.mabr.postservice.entity.security.User;
import org.mabr.postservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserSessionService {

    private final UserService userService;

    public String currentUserName() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AccessDeniedException("Необходимо аутентификация");
        }

        return auth.getName();
    }

    public User currentUser() {
        var username = currentUserName();
        return userService.getUserProfile(username);
    }

}
