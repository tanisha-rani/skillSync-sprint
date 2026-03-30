package com.skillsync.sessionservice.client;

import org.springframework.stereotype.Component;

@Component
public class NotificationFeignClientFallback implements NotificationFeignClient {

    @Override
    public void sendNotification(NotificationRequestDto request) {
        // notification failed to send — log and move on, don't fail the main flow
        System.err.println("Notification service unavailable. Skipped notification for userId=" + request.getUserId());
    }
}