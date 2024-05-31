package org.mabr.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import org.mabr.notificationservice.entity.MessageNotification;
import org.mabr.notificationservice.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping("/list")
    public ResponseEntity<List<MessageNotification>> getList(@RequestHeader("authorUser") String username) {
        var list = service.getMessageNotification(username);

        return ResponseEntity.ok(list);
    }
}
