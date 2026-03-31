package com.skillsync.apigateway.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "SESSION-SERVICE")
public interface SessionClient {

    @GetMapping("/sessions/admin/stats")
    Map<String, Long> getSessionStats(
            @RequestHeader("Authorization") String token
    );
}