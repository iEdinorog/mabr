package org.mabr.messengerservice.serivce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mabr.messengerservice.dto.AttachmentDto;
import org.mabr.messengerservice.dto.UpdateMessageDto;
import org.mabr.messengerservice.entity.Attachment;
import org.mabr.messengerservice.entity.AttachmentType;
import org.mabr.messengerservice.entity.Message;
import org.mabr.messengerservice.repository.AttachmentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    public List<Attachment> getAttachments(String chatId, AttachmentType type, int page, int size) {
        return attachmentRepository.findByChatIdAndType(chatId, type,
                PageRequest.of(page, size, Sort.by("addedAt").descending()));
    }

    public List<Attachment> updateAttachments(Message message, UpdateMessageDto messageDto) {
        var attachmentDtoSet = new HashSet<>(messageDto.attachments());
        var attachmentToRemove = new ArrayList<Attachment>();

        for (var attachment : message.getAttachments()) {
            if (!attachmentDtoSet.removeIf(dto -> attachment.getContent().equals(dto.content()) &&
                    attachment.getType().equals(dto.type()))) {

                attachmentToRemove.add(attachment);
            }
        }

        message.getAttachments().removeAll(attachmentToRemove);

        return buildAttachments(message, new ArrayList<>(attachmentDtoSet));
    }

    public Attachment buildAttachment(Message message, AttachmentDto attachmentDto) {
        return Attachment.builder()
                .addedAt(Instant.now())
                .chatId(message.getChatId())
                .content(attachmentDto.content())
                .type(attachmentDto.type())
                .message(message)
                .build();
    }

    public List<Attachment> buildAttachments(Message message, List<AttachmentDto> attachmentDtos) {
        return attachmentDtos.stream()
                .map(dto -> buildAttachment(message, dto))
                .toList();
    }
}
