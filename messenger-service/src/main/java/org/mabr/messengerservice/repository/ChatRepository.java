package org.mabr.messengerservice.repository;

import org.mabr.messengerservice.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Integer> {

    List<Chat> findByOwnerUsername(String ownerUsername);
}
