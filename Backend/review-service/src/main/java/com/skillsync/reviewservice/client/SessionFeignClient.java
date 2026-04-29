package com.skillsync.reviewservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "SESSION-SERVICE",
        fallback = SessionFeignClientFallback.class
)
public interface SessionFeignClient {

    @GetMapping("/sessions/{id}")
    SessionDto getSessionById(@PathVariable("id") Long id);
}