package org.mabr.messengerservice.dto;

import org.mabr.messengerservice.entity.MessageType;

import java.util.List;

public record MessageDto(

        String chatId,

        String senderUsername,

        String content,

        MessageType type,

        List<AttachmentDto> attachments
) {
}
