package org.mabr.authenticationservice.service;

import lombok.RequiredArgsConstructor;
import org.mabr.authenticationservice.dto.UserDto;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final WebClient.Builder webClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = webClient.build().get()
                .uri("http://user-service/", uriBuilder -> uriBuilder.pathSegment("user", username).build())
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return User.builder()
                .username(user.username())
                .password(user.password())
                .authorities(new ArrayList<>())
                .build();
    }
}
