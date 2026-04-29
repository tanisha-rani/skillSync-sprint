package com.skillsync.notificationservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user receiving the notification
    private Long userId;

    // Email address to send to
    private String recipientEmail;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    // Reference to the entity that triggered this notification
    // e.g. sessionId, reviewId, groupId
    private Long referenceId;

    @Enumerated(EnumType.STRING)
    private ReferenceType referenceType;

    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

    // Whether the user has read this in-app notification
    private boolean isRead;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = NotificationStatus.PENDING;
        this.isRead = false;
    }
}