package org.mabr.notificationservice.repository;

import org.mabr.notificationservice.entity.MessageNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageNotificationRepository extends JpaRepository<MessageNotification, Integer> {
    List<MessageNotification> findAllByRecipientUsername(String username);
}
