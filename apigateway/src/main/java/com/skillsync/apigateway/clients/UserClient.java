package com.skillsync.apigateway.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;
@FeignClient(name="USER-SERVICE")
public interface UserClient {

    @GetMapping("/users/admin/stats")
    Map<String, Long> getUserStats(
            @RequestHeader("Authorization") String token
    );

}
