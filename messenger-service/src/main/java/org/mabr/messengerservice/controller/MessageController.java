package org.mabr.messengerservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("api/messenger")
@RequiredArgsConstructor
public class MessageController {

    private final RestTemplate restTemplate;
    private final WebClient.Builder webClient;

    @GetMapping
    public ResponseEntity<String> getMessage(){
        var rest = restTemplate.getForEntity("http://post-service/api/data/city", String.class);

        var web = webClient.build().get()
                .uri("http://post-service/api/data/city")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return ResponseEntity.ok(web);
    }
}
