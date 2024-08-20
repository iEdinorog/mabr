package org.mabr.messengerservice.serivce;

import lombok.extern.slf4j.Slf4j;
import org.mabr.messengerservice.dto.ForwardMessageDto;
import org.mabr.messengerservice.dto.MessageDto;
import org.mabr.messengerservice.dto.ReplyMessageDto;
import org.mabr.messengerservice.dto.UpdateMessageDto;
import org.mabr.messengerservice.entity.Message;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CachedMessageService implements MessageService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, List<Message>> hashOperations;
    private final String KEY_PREFIX = "messages";
    private final MessageService messageService;

    public CachedMessageService(RedisTemplate<String, Object> redisTemplate,
                                @Qualifier("messageServiceImpl") MessageService messageService) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
        this.messageService = messageService;
    }

    @Override
    public void sendMessage(MessageDto messageDto) {
        messageService.sendMessage(messageDto);

        var messages = messageService.getMessages(messageDto.chatId(), 0, 10);

        saveCache(messageDto.chatId(), messages, 0, 10);
    }

    @Override
    public List<Message> getMessages(String chatId, int page, int size) {
        var key = makeKey(chatId);
        var hashKey = page + "-" + size;

        var messages = hashOperations.get(key, hashKey);

        if (messages != null) {
            log.debug("Getting message from cache for chat {}", chatId);
            return messages;
        }

        messages = messageService.getMessages(chatId, page, size);

        saveCache(chatId, messages, page, size);

        return messages;
    }

    @Override
    public void updateMessage(UpdateMessageDto messageDto) {
        messageService.updateMessage(messageDto);

        var message = getMessageById(messageDto.messageId());

        updateInCache(message);
    }

    @Override
    public Message getMessageById(int id) {
        return messageService.getMessageById(id);
    }

    @Override
    public Message replyToMessage(ReplyMessageDto dto) {
        return messageService.replyToMessage(dto);
    }

    @Override
    public Message forwardMessage(ForwardMessageDto dto) {
        return messageService.forwardMessage(dto);
    }

    @Override
    public void deleteMessage(int messageId) {
        var message = getMessageById(messageId);
        clearCache(message.getChatId());

        messageService.deleteMessage(messageId);
    }

    private void saveCache(String chatId, List<Message> messages, int page, int size) {
        var key = makeKey(chatId);
        var hashKey = page + "-" + size;

        clearCache(key);

        hashOperations.putAll(key, Map.of(hashKey, messages));
        log.info("Cache saved for chat {} with key {}-{}", chatId, page, size);
    }

    private void updateInCache(Message message) {
        var key = makeKey(message.getChatId());

        Map<String, List<Message>> entries = hashOperations.entries(key);

        if (entries.isEmpty()) {
            hashOperations.put(key, "0-1", List.of(message));

            return;
        }

        for (var entry : entries.entrySet()) {
            var messages = entry.getValue();
            for (var entryMessage : messages) {
                if (entryMessage.getId() == message.getId()) {
                    var index = messages.indexOf(entryMessage);
                    messages.set(index, message);

                    hashOperations.put(key, entry.getKey(), messages);
                    log.info("Update cache for chat {}", message.getChatId());

                    return;
                }
            }
        }
    }

    private void clearCache(String key) {
        redisTemplate.delete(key);
        log.info("Cache cleared for key {}", key);
    }

    private String makeKey(String name) {
        return KEY_PREFIX + ":" + name;
    }
}
