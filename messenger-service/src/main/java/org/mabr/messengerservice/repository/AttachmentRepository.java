package org.mabr.messengerservice.repository;

import org.mabr.messengerservice.entity.Attachment;
import org.mabr.messengerservice.entity.AttachmentType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {

    List<Attachment> findByChatIdAndType(String chatId, AttachmentType type, PageRequest pageRequest);
}
