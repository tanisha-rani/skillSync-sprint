package com.skillsync.notificationservice.client;

import lombok.Data;

@Data
public class SessionDto {
    private Long id;
    private Long mentorId;
    private Long learnerId;
    private String topic;
    private String status;
}