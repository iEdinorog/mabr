package org.mabr.messengerservice.dto;

import java.time.Instant;

public record ChatDto(

        Instant createdAt,

        String ownerUsername,

        String recipientUsername
) {
}
