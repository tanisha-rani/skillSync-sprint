package com.skillsync.mentor.client;

import org.springframework.stereotype.Component;

@Component
public class UserFeignClientFallback implements UserFeignClient {

    @Override
    public UserDto getUserById(Long id) {
        UserDto fallback = new UserDto();
        fallback.setId(id);
        fallback.setName("Unknown User");
        fallback.setEmail("");
        return fallback;
    }
}