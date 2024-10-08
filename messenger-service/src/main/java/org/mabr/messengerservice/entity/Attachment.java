package org.mabr.messengerservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Instant addedAt;

    @Column(nullable = false)
    private String chatId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private AttachmentType type;

    @ManyToOne()
    @JoinColumn(name = "message_id")
    @JsonIgnore
    private Message message;
}
