package org.mabr.messengerservice.serivce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mabr.messengerservice.dto.MessageDto;
import org.mabr.messengerservice.entity.Message;
import org.mabr.messengerservice.event.MessageSentEvent;
import org.mabr.messengerservice.repository.ChatRepository;
import org.mabr.messengerservice.repository.MessageRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final KafkaTemplate<String, MessageSentEvent> kafkaTemplate;

    public void sendMessage(MessageDto messageDto) {
        var chat = chatService.getChatById(messageDto.chatId());

        var message = Message.builder()
                .sentAt(messageDto.sentAt())
                .senderUsername(messageDto.senderUsername())
                .content(messageDto.content())
                .build();

        message = messageRepository.save(message);

        log.info("{} sent message to chat {}", message.getSenderUsername(), chat.getId());
        chat.getMessages().add(message);
        chatRepository.save(chat);

        log.info("Sending message to notification service");
        kafkaTemplate.send("messages", new MessageSentEvent(chat.getRecipientUsername(), message.getContent()));
    }
}
