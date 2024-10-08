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
import org.mabr.messengerservice.repository.MessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final AttachmentService attachmentService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void sendMessage(MessageDto messageDto) {
        var chat = chatService.getChatById(messageDto.chatId(), messageDto.senderUsername());
        saveMessage(chat, messageDto);

        notificationService.sendMessage(chat, messageDto);
    }

    @Override
    public List<Message> getMessages(String chatId, int page, int size) {
        return messageRepository.findByChatId(chatId,
                        PageRequest.of(page, size, Sort.by("sentAt").descending()))
                .orElseThrow();
    }

    @Override
    public void updateMessage(UpdateMessageDto messageDto) {
        var message = getMessageById(messageDto.messageId());

        message.setContent(messageDto.content());
        message.setType(messageDto.type());
        message.setEdited(true);

        var attachments = attachmentService.updateAttachments(message, messageDto);
        message.getAttachments().addAll(attachments);

        message = messageRepository.save(message);
        log.info("Message {} was updated", message.getId());
    }

    @Async("taskExecutor")
    public void saveMessage(Chat chat, MessageDto messageDto) {
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

        message = messageRepository.save(message);
        log.info("Message {} saved into chat {}", message.getId(), chat.getChatId());
    }

    public Message getMessageById(int id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Message not found with id " + id));
    }

    @Override
    public Message replyToMessage(ReplyMessageDto dto) {
        var originalMessage = getMessageById(dto.originalMessageId());

        var replyMessage = Message.builder()
                .chatId(dto.message().chatId())
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

    @Override
    public Message forwardMessage(ForwardMessageDto dto) {
        var forwardedMessages = dto.forwardedMessagesIds().stream()
                .map(this::getMessageById)
                .toList();

        var forwardMessage = Message.builder()
                .chatId(dto.message().chatId())
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

    @Override
    public void deleteMessage(int messageId) {
        messageRepository.deleteById(messageId);
    }
}
