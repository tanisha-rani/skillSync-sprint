package com.skillsync.notificationservice.dto;

import com.skillsync.notificationservice.entity.NotificationStatus;
import com.skillsync.notificationservice.entity.NotificationType;
import com.skillsync.notificationservice.entity.ReferenceType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDto {

    private Long id;
    private Long userId;
    private String recipientEmail;
    private NotificationType type;
    private String subject;
    private String message;
    private NotificationStatus status;
    private Long referenceId;
    private ReferenceType referenceType;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private boolean isRead;
}