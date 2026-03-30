package com.skillsync.sessionservice.client;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String role;
    private boolean active;
}