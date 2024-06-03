package org.mabr.messengerservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageSentEvent {

    private String senderUsername;

    private String recipientUsername;

    private String message;

    private String messageType;
}
