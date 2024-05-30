package org.mabr.postservice.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mabr.postservice.exception.UserNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final WebClient.Builder webClient;

    public void checkUsername(String username) {
        webClient.build().get()
                .uri("http://user-service/", uriBuilder -> uriBuilder.pathSegment("user", username).build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new UserNotFoundException(username)))
                .toBodilessEntity()
                .block();

    }
}
