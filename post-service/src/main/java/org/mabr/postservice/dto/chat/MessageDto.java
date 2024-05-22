package org.mabr.postservice.dto.chat;

import java.time.Instant;

public record MessageDto(

        String username,

        Instant sentAt,

        String content
) {
}
