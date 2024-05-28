package org.mabr.messengerservice.controller;

import lombok.RequiredArgsConstructor;
import org.mabr.messengerservice.dto.ChatDto;
import org.mabr.messengerservice.entity.Chat;
import org.mabr.messengerservice.serivce.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService service;

    @GetMapping("/{id}")
    public ResponseEntity<Chat> getChat(@PathVariable int id) {
        var chat = service.getChatById(id);
        return ResponseEntity.ok(chat);
    }

    @GetMapping()
    public ResponseEntity<List<Chat>> getChats(@RequestHeader("authorUser") String username) {
        var chats = service.getChatList(username);
        return ResponseEntity.ok(chats);
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> createChat(@RequestBody ChatDto chatDto) {
        service.createChat(chatDto);

        return ResponseEntity.ok(HttpStatus.CREATED);
    }
}
