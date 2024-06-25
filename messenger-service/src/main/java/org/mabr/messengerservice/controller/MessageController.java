package org.mabr.messengerservice.controller;

import lombok.RequiredArgsConstructor;
import org.mabr.messengerservice.dto.MessageDto;
import org.mabr.messengerservice.dto.UpdateMessageDto;
import org.mabr.messengerservice.entity.Attachment;
import org.mabr.messengerservice.entity.AttachmentType;
import org.mabr.messengerservice.entity.Message;
import org.mabr.messengerservice.serivce.AttachmentService;
import org.mabr.messengerservice.serivce.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final AttachmentService attachmentService;

    @PostMapping("/messages")
    public ResponseEntity<HttpStatus> sendMessage(@RequestBody MessageDto messageDto) {
        messageService.sendMessage(messageDto);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @GetMapping("{chatId}/messages")
    public ResponseEntity<List<Message>> getMessages(@PathVariable String chatId,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        var messages = messageService.getMessages(chatId, page, size);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/message/update")
    public ResponseEntity<Message> updateMessage(@RequestBody UpdateMessageDto messageDto) {
        var message = messageService.updateMessage(messageDto);
        return ResponseEntity.ok(message);
    }

    @GetMapping("{chatId}/attachments")
    public ResponseEntity<List<Attachment>> getAttachments(@PathVariable String chatId,
                                                           @RequestParam AttachmentType type,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        var images = attachmentService.getAttachments(chatId, type, page, size);
        return ResponseEntity.ok(images);
    }

}
