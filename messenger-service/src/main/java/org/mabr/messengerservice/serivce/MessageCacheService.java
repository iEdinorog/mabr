package org.mabr.messengerservice.serivce;

import lombok.extern.slf4j.Slf4j;
import org.mabr.messengerservice.entity.Message;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MessageCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, List<Message>> hashOperations;
    private final String KEY_PREFIX = "messages";

    public MessageCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    public List<Message> getMessage(String chatId, int page, int size) {
        var key = makeKey(chatId);
        var hashKey = page + "-" + size;

        var messages = hashOperations.get(key, hashKey);

        if (messages != null) {
            return messages;
        }

        return Collections.emptyList();
    }

    public void saveMessages(String chatId, List<Message> messages, int page, int size) {
        var key = makeKey(chatId);
        var hashKey = page + "-" + size;

        deleteMessage(key);
        log.debug("Cache cleared for chat {}", chatId);

        hashOperations.putAll(key, Map.of(hashKey, messages));
        log.info("Cache saved for chat {} with key {}-{}", chatId, page, size);
    }

    public void updateMessage(Message message) {
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

    private void deleteMessage(String key) {
        redisTemplate.delete(key);
    }

    private String makeKey(String name) {
        return KEY_PREFIX + ":" + name;
    }
}
