package org.mabr.notificationservice.service;

import lombok.RequiredArgsConstructor;

import org.mabr.notificationservice.dto.MessageSentEvent;
import org.mabr.notificationservice.entity.MessageNotification;
import org.mabr.notificationservice.repository.MessageNotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MessageNotificationRepository messageNotificationRepository;

    public void saveMessage(MessageSentEvent messageSentEvent) {
        var message = MessageNotification.builder()
                .senderUsername(messageSentEvent.getSenderUsername())
                .recipientUsername(messageSentEvent.getRecipientUsername())
                .message(messageSentEvent.getMessage())
                .type(messageSentEvent.getMessageType())
                .build();

        messageNotificationRepository.save(message);
    }

    public List<MessageNotification> getMessageNotification(String username) {
        return messageNotificationRepository.findAllByRecipientUsername(username);
    }
}
