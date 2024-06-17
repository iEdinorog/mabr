package org.mabr.messengerservice.dto;

import org.mabr.messengerservice.entity.AttachmentType;

public record AttachmentDto(

        String content,

        AttachmentType type
) {
}
