package org.mabr.messengerservice.serivce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mabr.messengerservice.dto.AttachmentDto;
import org.mabr.messengerservice.dto.MessageDto;
import org.mabr.messengerservice.dto.UpdateMessageDto;
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
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

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

    @CachePut(value = "messages")
    public Message updateMessage(UpdateMessageDto messageDto) {
        var message = messageRepository.findById(messageDto.messageId()).orElseThrow(NoSuchElementException::new);

        message.setContent(messageDto.content());
        message.setType(messageDto.type());
        message.setEdited(true);

        var attachments = updateAttachments(message, messageDto);
        message.getAttachments().addAll(attachments);

        return messageRepository.save(message);
    }

    private List<Attachment> updateAttachments(Message message, UpdateMessageDto messageDto) {
        var attachmentDtoSet = new HashSet<>(messageDto.attachments());
        var attachmentToRemove = new ArrayList<Attachment>();

        for (var attachment : message.getAttachments()) {
            if (!attachmentDtoSet.removeIf(dto -> attachment.getContent().equals(dto.content()) &&
                    attachment.getType().equals(dto.type()))) {

                attachmentToRemove.add(attachment);
            }
        }

        message.getAttachments().removeAll(attachmentToRemove);

        return getAttachments(message, new ArrayList<>(attachmentDtoSet));
    }

    @CachePut(value = "messages")
    public Message saveMessage(Chat chat, MessageDto messageDto) {
        var content = StringUtils.hasText(messageDto.content()) ? messageDto.content() : "";

        var message = Message.builder()
                .chatId(chat.getChatId())
                .sentAt(messageDto.sentAt())
                .senderUsername(chat.getSenderUsername())
                .content(content)
                .type(messageDto.type())
                .build();

        var attachments = getAttachments(message, messageDto.attachments());

        message.setAttachments(attachments);
        message.setStatusType(MessageStatusType.SENT);

        log.info("{} sent message to chat {}", message.getSenderUsername(), chat.getChatId());
        return messageRepository.save(message);
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

    private Attachment getAttachment(Message message, AttachmentDto attachmentDto) {
        return Attachment.builder()
                .addedAt(Instant.now())
                .chatId(message.getChatId())
                .content(attachmentDto.content())
                .type(attachmentDto.type())
                .message(message)
                .build();
    }

    private List<Attachment> getAttachments(Message message, List<AttachmentDto> attachmentDtos) {
        return attachmentDtos.stream()
                .map(dto -> getAttachment(message, dto))
                .toList();
    }
}
