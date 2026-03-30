package com.skillsync.authservice.service.impl;

import com.skillsync.authservice.dto.AuthResponse;
import com.skillsync.authservice.dto.LoginRequest;
import com.skillsync.authservice.dto.UserRequest;
import com.skillsync.authservice.entity.RefreshToken;
import com.skillsync.authservice.entity.User;
import com.skillsync.authservice.exception.EmailAlreadyExistException;
import com.skillsync.authservice.exception.InvalidCredentialException;
import com.skillsync.authservice.repository.UserRepository;
import com.skillsync.authservice.service.JwtService;
import com.skillsync.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenServiceImpl refreshTokenServiceImpl;

    @Override
    public AuthResponse register(UserRequest userRequest) {
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyExistException("Email already exists");
        }
        User user = modelMapper.map(userRequest, User.class);
        user.setId(null);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setRole(userRequest.getRole());

        User savedUser = userRepository.save(user);

        String accessToken = jwtService.generateToken(savedUser.getEmail(), savedUser.getRole().name());
        RefreshToken refreshToken = refreshTokenServiceImpl.createRefreshToken(savedUser);

        return AuthResponse.builder()
                .userId(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new InvalidCredentialException("Invalid email or password"));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialException("Invalid email or password");
        }
        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());
        RefreshToken refreshToken = refreshTokenServiceImpl.createRefreshToken(user);

        return AuthResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public User getCurrentUser() {
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}