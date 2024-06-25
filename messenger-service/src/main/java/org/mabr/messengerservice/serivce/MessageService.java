package org.mabr.messengerservice.serivce;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mabr.messengerservice.dto.ForwardMessageDto;
import org.mabr.messengerservice.dto.MessageDto;
import org.mabr.messengerservice.dto.ReplyMessageDto;
import org.mabr.messengerservice.dto.UpdateMessageDto;
import org.mabr.messengerservice.entity.Chat;
import org.mabr.messengerservice.entity.Message;
import org.mabr.messengerservice.entity.MessageStatusType;
import org.mabr.messengerservice.event.MessageSentEvent;
import org.mabr.messengerservice.repository.MessageRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final AttachmentService attachmentService;
    private final KafkaTemplate<String, MessageSentEvent> kafkaTemplate;

    @Transactional
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

    @CachePut(value = "messages")
    public Message updateMessage(UpdateMessageDto messageDto) {
        var message = getMessageById(messageDto.messageId());

        message.setContent(messageDto.content());
        message.setType(messageDto.type());
        message.setEdited(true);

        var attachments = attachmentService.updateAttachments(message, messageDto);
        message.getAttachments().addAll(attachments);

        return messageRepository.save(message);
    }

    @CachePut(value = "messages")
    public Message saveMessage(Chat chat, MessageDto messageDto) {
        var content = StringUtils.hasText(messageDto.content()) ? messageDto.content() : "";

        var message = Message.builder()
                .chatId(chat.getChatId())
                .sentAt(Instant.now())
                .senderUsername(chat.getSenderUsername())
                .content(content)
                .type(messageDto.type())
                .build();

        var attachments = attachmentService.buildAttachments(message, messageDto.attachments());

        message.setAttachments(attachments);
        message.setStatusType(MessageStatusType.SENT);

        log.info("{} sent message to chat {}", message.getSenderUsername(), chat.getChatId());
        return messageRepository.save(message);
    }

    public Message getMessageById(int id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Message not found with id " + id));
    }

    public Message replyToMessage(String chatId, ReplyMessageDto dto) {
        var originalMessage = getMessageById(dto.originalMessageId());

        var replyMessage = Message.builder()
                .chatId(chatId)
                .sentAt(Instant.now())
                .senderUsername(dto.message().senderUsername())
                .content(dto.message().content())
                .type(dto.message().type())
                .reply(originalMessage)
                .build();

        var attachments = attachmentService.buildAttachments(replyMessage, dto.message().attachments());
        replyMessage.setAttachments(attachments);

        return messageRepository.save(replyMessage);
    }

    public Message forwardMessage(String chatId, ForwardMessageDto dto) {
        var forwardedMessages  = dto.forwardedMessagesIds().stream()
                .map(this::getMessageById)
                .toList();

        var forwardMessage = Message.builder()
                .chatId(chatId)
                .sentAt(Instant.now())
                .senderUsername(dto.message().senderUsername())
                .content(dto.message().content())
                .type(dto.message().type())
                .forwarded(forwardedMessages)
                .build();

        var attachments = attachmentService.buildAttachments(forwardMessage, dto.message().attachments());
        forwardMessage.setAttachments(attachments);

        return messageRepository.save(forwardMessage);
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
}
