package org.mabr.messengerservice.serivce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mabr.messengerservice.dto.ChatDto;
import org.mabr.messengerservice.entity.Chat;
import org.mabr.messengerservice.repository.ChatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;

    public void createChat(ChatDto chatDto) {
        var chatForOwner = Chat.builder()
                .createdAt(chatDto.createdAt())
                .ownerUsername(chatDto.ownerUsername())
                .recipientUsername(chatDto.recipientUsername())
                .build();

        var chatForRecipient = Chat.builder()
                .createdAt(chatDto.createdAt())
                .ownerUsername(chatDto.recipientUsername())
                .recipientUsername(chatDto.ownerUsername())
                .build();

        log.info("Chats was created for {}, {}", chatForOwner.getOwnerUsername(), chatForRecipient.getOwnerUsername());
        chatRepository.saveAll(List.of(chatForOwner, chatForRecipient));
    }

    public List<Chat> getChatList(String username) {
        return chatRepository.findByOwnerUsername(username);
    }

    public Chat getChatById(int chatId) {
        return chatRepository.findById(chatId).orElseThrow();
    }
}
