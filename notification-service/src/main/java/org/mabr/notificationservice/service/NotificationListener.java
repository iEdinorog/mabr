package org.mabr.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.mabr.notificationservice.dto.MessageSentEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final NotificationService service;

    @KafkaListener(topics = "messages", containerFactory = "messageSentEventConcurrentKafkaListenerContainerFactory")
    public void messageSentEventListener(MessageSentEvent messageSentEvent) {
        service.saveMessage(messageSentEvent);
    }
}
