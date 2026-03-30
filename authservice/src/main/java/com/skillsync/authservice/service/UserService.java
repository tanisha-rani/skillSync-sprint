package com.skillsync.authservice.service;

import com.skillsync.authservice.dto.AuthResponse;
import com.skillsync.authservice.dto.LoginRequest;
import com.skillsync.authservice.dto.UserRequest;
import com.skillsync.authservice.entity.User;

import java.util.Map;

public interface UserService {
    AuthResponse register(UserRequest userRequest);
    AuthResponse login(LoginRequest loginRequest);
    User getCurrentUser();
}
