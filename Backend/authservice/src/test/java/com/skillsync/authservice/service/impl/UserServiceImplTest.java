package com.skillsync.authservice.service.impl;

import com.skillsync.authservice.client.NotificationServiceClient;
import com.skillsync.authservice.client.UserServiceClient;
import com.skillsync.authservice.dto.AuthResponse;
import com.skillsync.authservice.dto.LoginRequest;
import com.skillsync.authservice.dto.UserRequest;
import com.skillsync.authservice.entity.RefreshToken;
import com.skillsync.authservice.entity.Role;
import com.skillsync.authservice.entity.User;
import com.skillsync.authservice.exception.EmailAlreadyExistException;
import com.skillsync.authservice.exception.InvalidCredentialException;
import com.skillsync.authservice.repository.UserRepository;
import com.skillsync.authservice.service.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenServiceImpl refreshTokenServiceImpl;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private NotificationServiceClient notificationServiceClient;

    @InjectMocks
    private UserServiceImpl userService;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void register_whenEmailExists_throwsException() {
        UserRequest request = new UserRequest("Test User", "user@example.com", "Test@1234", Role.ROLE_LEARNER);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(EmailAlreadyExistException.class, () -> userService.register(request));
    }

    @Test
    void register_success_returnsAuthResponse() {
        UserRequest request = new UserRequest("Test User", "user@example.com", "Test@1234", Role.ROLE_LEARNER);
        User mappedUser = new User();
        mappedUser.setEmail("user@example.com");
        mappedUser.setRole(Role.ROLE_LEARNER);
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("user@example.com");
        savedUser.setRole(Role.ROLE_LEARNER);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(modelMapper.map(request, User.class)).thenReturn(mappedUser);
        when(passwordEncoder.encode("Test@1234")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken("user@example.com", "ROLE_LEARNER")).thenReturn("access");
        when(refreshTokenServiceImpl.createRefreshToken(savedUser))
                .thenReturn(RefreshToken.builder().token("refresh").build());

        AuthResponse response = userService.register(request);

        assertEquals("user@example.com", response.getEmail());
        assertEquals("access", response.getAccessToken());
        assertEquals("refresh", response.getRefreshToken());
    }

    @Test
    void login_invalidPassword_throwsException() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("encoded");
        user.setRole(Role.ROLE_LEARNER);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("bad", "encoded")).thenReturn(false);

        assertThrows(InvalidCredentialException.class,
                () -> userService.login(new LoginRequest("user@example.com", "bad")));
    }

    @Test
    void getCurrentUser_readsFromSecurityContext() {
        User user = new User();
        user.setEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user@example.com", null));

        User result = userService.getCurrentUser();

        assertNotNull(result);
        assertEquals("user@example.com", result.getEmail());
    }
}
