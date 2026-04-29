package com.skillsync.mentor.client;

import org.springframework.stereotype.Component;

@Component
public class NotificationFeignClientFallback implements NotificationFeignClient {

    @Override
    public void sendNotification(NotificationRequestDto request) {
        System.err.println("Notification service unavailable. Skipped mentor notification for userId=" + request.getUserId());
    }
}
