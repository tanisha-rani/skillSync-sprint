package com.skillsync.authservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")

public class RoleController {
    @GetMapping("/admin/data")
    public String admin(){
        return "Admin Access";
    }

    @GetMapping("/user/data")
    public String user(){
        return "User Access";
    }
}
