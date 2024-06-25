package org.mabr.messengerservice.dto;

public record ReplyMessageDto(

        int originalMessageId,

        MessageDto message
) {
}
