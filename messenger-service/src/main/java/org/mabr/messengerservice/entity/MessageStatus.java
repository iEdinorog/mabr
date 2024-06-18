package org.mabr.messengerservice.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.*;

@Data
@MappedSuperclass
public class MessageStatus {

    private MessageStatusType statusType;

    private boolean isEdited = false;
}
