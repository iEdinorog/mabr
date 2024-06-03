package org.mabr.notificationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String senderUsername;

    @Column(nullable = false)
    private String recipientUsername;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String type;
}
