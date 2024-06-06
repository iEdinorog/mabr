package org.mabr.messengerservice.controller;

import lombok.RequiredArgsConstructor;
import org.mabr.messengerservice.dto.MessageDto;
import org.mabr.messengerservice.entity.Message;
import org.mabr.messengerservice.serivce.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService service;

    @PostMapping("/messages")
    public ResponseEntity<HttpStatus> sendMessage(@RequestBody MessageDto messageDto) {
        service.sendMessage(messageDto);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @GetMapping("{chatId}/messages")
    public ResponseEntity<List<Message>> getMessages(@PathVariable String chatId,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue=  "10") int size) {
        var messages = service.getMessages(chatId, page, size);
        return ResponseEntity.ok(messages);
    }
}
