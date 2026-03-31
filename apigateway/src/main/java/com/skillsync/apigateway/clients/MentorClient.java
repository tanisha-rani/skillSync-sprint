package com.skillsync.apigateway.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "MENTOR-SERVICE")
public interface MentorClient {

    @GetMapping("/mentors/admin/stats")
    Map<String, Long> getMentorStats(
            @RequestHeader("Authorization") String token
    );
}