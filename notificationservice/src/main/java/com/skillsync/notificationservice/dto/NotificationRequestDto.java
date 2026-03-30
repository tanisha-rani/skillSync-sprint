package com.skillsync.notificationservice.dto;

import com.skillsync.notificationservice.entity.NotificationType;
import com.skillsync.notificationservice.entity.ReferenceType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestDto {

    private Long userId;
    private String recipientEmail;
    private NotificationType type;
    private String subject;
    private String message;
    private Long referenceId;
    private ReferenceType referenceType;
}