package org.mabr.messengerservice.repository;

import org.mabr.messengerservice.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {
}
