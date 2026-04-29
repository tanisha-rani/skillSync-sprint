package com.skillsync.authservice.client;

import com.skillsync.authservice.dto.NotificationRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationServiceClient {

    @PostMapping("/notifications")
    void sendNotification(
            @RequestHeader("Authorization") String authorization,
            @RequestBody NotificationRequestDto request
    );
}
