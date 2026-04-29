package com.skillsync.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestDto {
    private Long userId;
    private String recipientEmail;
    private String type;
    private String subject;
    private String message;
    private Long referenceId;
    private String referenceType;
}
