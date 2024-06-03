package org.mabr.messengerservice.serivce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mabr.messengerservice.dto.MessageDto;
import org.mabr.messengerservice.entity.Chat;
import org.mabr.messengerservice.entity.Message;
import org.mabr.messengerservice.event.MessageSentEvent;
import org.mabr.messengerservice.repository.MessageRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final KafkaTemplate<String, MessageSentEvent> kafkaTemplate;

    public void sendMessage(MessageDto messageDto) {
        var chat = chatService.getChatById(messageDto.chatId(), messageDto.senderUsername());

        var message = Message.builder()
                .chatId(chat.getChatId())
                .sentAt(messageDto.sentAt())
                .senderUsername(chat.getSenderUsername())
                .content(messageDto.content())
                .type(messageDto.type())
                .build();

        log.info("{} sent message to chat {}", message.getSenderUsername(), chat.getChatId());
        message = messageRepository.save(message);

        log.info("Sending message to notification service");
        sendMessageToNotification(chat, message);
    }

    private void sendMessageToNotification(Chat chat, Message message) {
        var messageContent = switch (message.getType()) {
            case PHOTO -> "Photo";
            case VIDEO -> "Video";
            case VIDEO_MESSAGE -> "Video message";
            case VOICE_MESSAGE -> "Voice message";
            default -> message.getContent();
        };

        kafkaTemplate.send("messages", new MessageSentEvent(
                chat.getSenderUsername(), chat.getRecipientUsername(), messageContent, message.getType().name()));
    }

    public List<Message> getMessages(String chatId) {
        return messageRepository.findByChatId(chatId).orElseThrow();
    }
}
