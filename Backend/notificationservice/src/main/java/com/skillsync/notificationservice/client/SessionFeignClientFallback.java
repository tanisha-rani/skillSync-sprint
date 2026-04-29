package com.skillsync.notificationservice.client;

import org.springframework.stereotype.Component;

@Component
public class SessionFeignClientFallback implements SessionFeignClient {

    @Override
    public SessionDto getSessionById(Long id) {
        SessionDto fallback = new SessionDto();
        fallback.setId(id);
        fallback.setStatus("UNKNOWN");
        return fallback;
    }
}
