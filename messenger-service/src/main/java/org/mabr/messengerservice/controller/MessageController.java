package org.mabr.messengerservice.controller;

import lombok.RequiredArgsConstructor;
import org.mabr.messengerservice.dto.MessageDto;
import org.mabr.messengerservice.serivce.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService service;

    @PostMapping()
    public ResponseEntity<HttpStatus> sendMessage(@RequestBody MessageDto messageDto) {
        service.sendMessage(messageDto);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

}
