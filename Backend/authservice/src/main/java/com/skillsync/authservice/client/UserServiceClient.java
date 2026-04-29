package com.skillsync.authservice.client;

import com.skillsync.authservice.dto.UserServiceUserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {

    @PostMapping("/users")
    void createUser(
            @RequestHeader("Authorization") String authorization,
            @RequestBody UserServiceUserRequest request
    );
}
