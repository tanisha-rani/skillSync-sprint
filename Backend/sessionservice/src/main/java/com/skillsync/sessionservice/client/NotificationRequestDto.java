package com.skillsync.sessionservice.client;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRequestDto {
    private Long userId;
    private String recipientEmail;
    private String subject;
    private String message;
    private String type;       // SESSION_BOOKED, SESSION_ACCEPTED, REVIEW_SUBMITTED etc.
    private Long referenceId;  // sessionId or reviewId
}