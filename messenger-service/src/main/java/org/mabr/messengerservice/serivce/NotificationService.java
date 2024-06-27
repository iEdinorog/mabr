package org.mabr.messengerservice.serivce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mabr.messengerservice.dto.MessageDto;
import org.mabr.messengerservice.entity.Chat;
import org.mabr.messengerservice.event.MessageSentEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final KafkaTemplate<String, MessageSentEvent> kafkaTemplate;

    @Async("taskExecutor")
    public void sendMessage(Chat chat, MessageDto message) {
        var messageContent = switch (message.type()) {
            case PHOTO -> "Photo";
            case VIDEO -> "Video";
            case VIDEO_MESSAGE -> "Video message";
            case VOICE_MESSAGE -> "Voice message";
            default -> message.content();
        };

        log.info("Sending message to notification service");
        kafkaTemplate.send("messages", new MessageSentEvent(
                chat.getSenderUsername(), chat.getRecipientUsername(), messageContent, message.type().name()));
    }
}
