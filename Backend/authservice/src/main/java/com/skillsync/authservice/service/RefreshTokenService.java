package com.skillsync.authservice.service;

import com.skillsync.authservice.entity.RefreshToken;
import com.skillsync.authservice.entity.User;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    RefreshToken verifyToken(String token);

    void deleteByToken(String token);
}
