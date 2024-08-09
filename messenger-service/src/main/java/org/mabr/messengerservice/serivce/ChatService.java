package org.mabr.messengerservice.serivce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mabr.messengerservice.dto.ChatDto;
import org.mabr.messengerservice.entity.Chat;
import org.mabr.messengerservice.repository.ChatRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;

    public void createChat(ChatDto chatDto) {
        var chatId = UUID.randomUUID().toString();

        var chatForOwner = Chat.builder()
                .chatId(chatId)
                .createdAt(chatDto.createdAt())
                .senderUsername(chatDto.ownerUsername())
                .recipientUsername(chatDto.recipientUsername())
                .build();

        var chatForRecipient = Chat.builder()
                .chatId(chatId)
                .createdAt(chatDto.createdAt())
                .senderUsername(chatDto.recipientUsername())
                .recipientUsername(chatDto.ownerUsername())
                .build();

        log.info("Chats was created with id {}", chatId);
        chatRepository.saveAll(List.of(chatForOwner, chatForRecipient));
    }

    public List<Chat> getChatList(String username) {
        return chatRepository.findBySenderUsername(username);
    }

    public Chat getChatById(String chatId, String senderUsername) {
        //TODO: выбрасывать обрабатываемое исключение
        return chatRepository.findByChatIdAndSenderUsername(chatId, senderUsername).orElseThrow();
    }
}
