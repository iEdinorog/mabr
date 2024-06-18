package org.mabr.messengerservice.serivce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mabr.messengerservice.dto.AttachmentDto;
import org.mabr.messengerservice.dto.MessageDto;
import org.mabr.messengerservice.entity.*;
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
        var message = saveMessage(chat, messageDto);

        sendMessageNotification(chat, message);
    }

    private Message saveMessage(Chat chat, MessageDto messageDto) {
        var content = StringUtils.hasText(messageDto.content()) ? messageDto.content() : "";

        var attachments = saveAttachments(chat.getChatId(), messageDto.attachments());

        var message = Message.builder()
                .chatId(chat.getChatId())
                .sentAt(messageDto.sentAt())
                .senderUsername(chat.getSenderUsername())
                .content(content)
                .type(messageDto.type())
                .attachments(attachments)
                .build();

        message.setStatusType(MessageStatusType.SENT);

        log.info("{} sent message to chat {}", message.getSenderUsername(), chat.getChatId());
        return messageRepository.save(message);
    }

    private List<Attachment> saveAttachments(String chatId, List<AttachmentDto> attachmentDtos) {
        var attachments = new ArrayList<Attachment>();
        for (var dto : attachmentDtos) {
            var attachment = Attachment.builder()
                    .addedAt(Instant.now())
                    .chatId(chatId)
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

        log.info("Sending message with id {} to notification service", message.getId());
        kafkaTemplate.send("messages", new MessageSentEvent(
                chat.getSenderUsername(), chat.getRecipientUsername(), messageContent, message.getType().name()));
    }

    @Cacheable(value = "messages")
    public List<Message> getMessages(String chatId, int page, int size) {
        return fetchAndCacheMessages(chatId, page, size);
    }

    @CachePut(value = "messages")
    public List<Message> fetchAndCacheMessages(String chatId, int page, int size) {
        return messageRepository.findByChatId(chatId,
                        PageRequest.of(page, size, Sort.by("sentAt").descending()))
                .orElseThrow();
    }

    public List<Attachment> getImagesAttachments(String chatId, int page, int size) {
        return attachmentRepository.findByChatIdAndType(chatId, AttachmentType.PHOTO,
                PageRequest.of(page, size, Sort.by("addedAt").descending()));
    }
}
