package org.mabr.messengerservice.repository;

import org.mabr.messengerservice.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Integer> {

    Optional<Chat> findByChatIdAndSenderUsername(String chatId, String senderUsername);

    List<Chat> findBySenderUsername(String senderUsername);
}
