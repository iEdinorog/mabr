package org.mabr.messengerservice.dto;

import java.time.Instant;

public record MessageDto(

        int chatId,

        Instant sentAt,

        String senderUsername,

        String content
) {
}
