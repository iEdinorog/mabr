package org.mabr.messengerservice.dto;

import org.mabr.messengerservice.entity.MessageType;

import java.time.Instant;

public record MessageDto(

        String chatId,

        Instant sentAt,

        String senderUsername,

        String content,

        MessageType type
) {
}
