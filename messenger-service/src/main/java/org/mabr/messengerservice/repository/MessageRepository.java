package org.mabr.messengerservice.repository;

import org.mabr.messengerservice.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    Optional<List<Message>> findByChatId(String chatId);
}
