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
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private String ownerUsername;

    @Column(nullable = false)
    private String recipientUsername;

    @OneToMany
    private List<Message> messages;
}
