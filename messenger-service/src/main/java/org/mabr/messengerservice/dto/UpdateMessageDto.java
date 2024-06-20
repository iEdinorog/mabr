package org.mabr.messengerservice.dto;

import org.mabr.messengerservice.entity.MessageType;

import java.util.List;

public record UpdateMessageDto(

        int messageId,

        String content,

        MessageType type,

        List<AttachmentDto> attachments
) {
}
