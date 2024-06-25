package org.mabr.messengerservice.dto;

import java.util.List;

public record ForwardMessageDto(

        List<Integer> forwardedMessagesIds,

        MessageDto message
) {
}
