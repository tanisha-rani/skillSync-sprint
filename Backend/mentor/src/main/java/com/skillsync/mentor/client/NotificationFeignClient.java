package com.skillsync.mentor.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "NOTIFICATION-SERVICE",
        fallback = NotificationFeignClientFallback.class
)
public interface NotificationFeignClient {

    @PostMapping("/notifications")
    void sendNotification(@RequestBody NotificationRequestDto request);
}
