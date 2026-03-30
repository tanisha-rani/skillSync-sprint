package com.skillsync.authservice.controller;

import com.skillsync.authservice.entity.User;
import com.skillsync.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TestController {
    private final UserService userService;
    @GetMapping("/test")
    public String test() {
        return "You are authenticated!!";
    }

    @GetMapping("/me")
    public User getMe() {
        return userService.getCurrentUser();
    }
}
