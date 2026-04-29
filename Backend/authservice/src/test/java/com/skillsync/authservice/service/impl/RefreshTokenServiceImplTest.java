package com.skillsync.authservice.service.impl;

import com.skillsync.authservice.entity.RefreshToken;
import com.skillsync.authservice.entity.User;
import com.skillsync.authservice.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @Test
    void createRefreshToken_savesToken() {
        User user = new User();
        RefreshToken saved = RefreshToken.builder().token("token").user(user).build();
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(saved);

        RefreshToken result = refreshTokenService.createRefreshToken(user);

        assertNotNull(result);
        assertEquals("token", result.getToken());
    }

    @Test
    void verifyToken_expired_throwsException() {
        RefreshToken token = RefreshToken.builder()
                .token("token")
                .expiryDate(LocalDateTime.now().minusDays(1))
                .build();
        when(refreshTokenRepository.findByToken("token")).thenReturn(Optional.of(token));

        assertThrows(RuntimeException.class, () -> refreshTokenService.verifyToken("token"));
    }
}
