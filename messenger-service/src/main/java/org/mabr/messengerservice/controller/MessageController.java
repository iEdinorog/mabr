package org.mabr.messengerservice.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.mabr.messengerservice.dto.ForwardMessageDto;
import org.mabr.messengerservice.dto.MessageDto;
import org.mabr.messengerservice.dto.ReplyMessageDto;
import org.mabr.messengerservice.dto.UpdateMessageDto;
import org.mabr.messengerservice.entity.Attachment;
import org.mabr.messengerservice.entity.AttachmentType;
import org.mabr.messengerservice.entity.Message;
import org.mabr.messengerservice.serivce.AttachmentService;
import org.mabr.messengerservice.serivce.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/chats")
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;
    private final AttachmentService attachmentService;

    public MessageController(@Qualifier("cachedMessageService") MessageService messageService,
                             AttachmentService attachmentService) {
        this.messageService = messageService;
        this.attachmentService = attachmentService;
    }

    @PostMapping("/messages")
    public ResponseEntity<HttpStatus> sendMessage(@RequestBody MessageDto messageDto) {
        messageService.sendMessage(messageDto);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @PostMapping("/message/update")
    public ResponseEntity<HttpStatus> updateMessage(@RequestBody UpdateMessageDto messageDto) {
        messageService.updateMessage(messageDto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/message/reply")
    public ResponseEntity<Message> replyToMessage(@RequestBody ReplyMessageDto dto) {
        var message = messageService.replyToMessage(dto);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/message/forward")
    public ResponseEntity<Message> forwardMessage(@RequestBody ForwardMessageDto dto) {
        var message = messageService.forwardMessage(dto);
        return ResponseEntity.ok(message);
    }

    @GetMapping("{chatId}/messages")
    @CircuitBreaker(name = "message", fallbackMethod = "recoveryGetMessages")
    public ResponseEntity<List<Message>> getMessages(@PathVariable String chatId,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        var messages = messageService.getMessages(chatId, page, size);
        return ResponseEntity.ok(messages);
    }

    private ResponseEntity<List<Message>> recoveryGetMessages(Exception ex) {
        log.warn(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ArrayList<>());
    }

    @GetMapping("{chatId}/attachments")
    public ResponseEntity<List<Attachment>> getAttachments(@PathVariable String chatId,
                                                           @RequestParam AttachmentType type,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        var images = attachmentService.getAttachments(chatId, type, page, size);
        return ResponseEntity.ok(images);
    }

    @PostMapping("/message/{messageId}/delete")
    public ResponseEntity<?> deleteMessage(@PathVariable int messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.ok().build();
    }
}
