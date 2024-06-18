package org.mabr.messengerservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message extends MessageStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String chatId;

    @Column(nullable = false)
    private Instant sentAt;

    @Column(nullable = false)
    private String senderUsername;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private MessageType type;

    @OneToMany()
    private List<Attachment> attachments;
}
