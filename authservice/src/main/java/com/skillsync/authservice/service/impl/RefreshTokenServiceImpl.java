package com.skillsync.authservice.service.impl;

import com.skillsync.authservice.entity.RefreshToken;
import com.skillsync.authservice.entity.User;
import com.skillsync.authservice.repository.RefreshTokenRepository;
import com.skillsync.authservice.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final long REFRESH_EXPIRY=7;
//    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken createRefreshToken(User user) {
        RefreshToken token= RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusDays(REFRESH_EXPIRY))
                .build();
        return refreshTokenRepository.save(token);
    }

    @Override
    public RefreshToken verifyToken(String token) {
        RefreshToken refreshToken=refreshTokenRepository.findByToken(token)
                .orElseThrow(()->new RuntimeException("Invalid Refresh Token"));
        if(refreshToken.getExpiryDate().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Refresh token expired");
        }
        return refreshToken;
    }
    @Override
    public void deleteByToken(String token){
        refreshTokenRepository.deleteByToken(token);
    }
}
