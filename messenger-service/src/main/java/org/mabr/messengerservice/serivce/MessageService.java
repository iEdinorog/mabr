package org.mabr.messengerservice.serivce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mabr.messengerservice.dto.AttachmentDto;
import org.mabr.messengerservice.dto.MessageDto;
import org.mabr.messengerservice.entity.Attachment;
import org.mabr.messengerservice.entity.Chat;
import org.mabr.messengerservice.entity.Message;
import org.mabr.messengerservice.event.MessageSentEvent;
import org.mabr.messengerservice.repository.AttachmentRepository;
import org.mabr.messengerservice.repository.MessageRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final AttachmentRepository attachmentRepository;
    private final ChatService chatService;
    private final KafkaTemplate<String, MessageSentEvent> kafkaTemplate;

    public void sendMessage(MessageDto messageDto) {
        var chat = chatService.getChatById(messageDto.chatId(), messageDto.senderUsername());

        var content = StringUtils.hasText(messageDto.content()) ? messageDto.content() : "";

        var attachments = saveAttachments(messageDto.attachments());

        var message = Message.builder()
                .chatId(chat.getChatId())
                .sentAt(messageDto.sentAt())
                .senderUsername(chat.getSenderUsername())
                .content(content)
                .type(messageDto.type())
                .attachments(attachments)
                .build();

        log.info("{} sent message to chat {}", message.getSenderUsername(), chat.getChatId());
        message = messageRepository.save(message);

        log.info("Sending message to notification service");
        sendMessageNotification(chat, message);
    }

    private List<Attachment> saveAttachments(List<AttachmentDto> attachmentDtos) {
        var attachments = new ArrayList<Attachment>();
        for (var dto : attachmentDtos) {
            var attachment = Attachment.builder()
                    .addedAt(Instant.now())
                    .content(dto.content())
                    .type(dto.type())
                    .build();

            attachments.add(attachment);
        }

        return attachmentRepository.saveAll(attachments);
    }

    private void sendMessageNotification(Chat chat, Message message) {
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

    @Cacheable(value = "messages")
    public List<Message> getMessages(String chatId, int page, int size) {
        return fetchAndCacheMessages(chatId, page, size);
    }

    @CachePut(value = "messages")
    public List<Message> fetchAndCacheMessages(String chatId, int page, int size) {
        System.out.println("get message from db");
        return messageRepository.findByChatId(chatId,
                PageRequest.of(page, size, Sort.by("sentAt").descending()))
                .orElseThrow();
    }
}
