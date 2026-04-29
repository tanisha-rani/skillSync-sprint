package com.skillsync.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.authservice.dto.AuthResponse;
import com.skillsync.authservice.dto.LoginRequest;
import com.skillsync.authservice.dto.UserRequest;
import com.skillsync.authservice.entity.RefreshToken;
import com.skillsync.authservice.entity.Role;
import com.skillsync.authservice.entity.User;
import com.skillsync.authservice.service.JwtService;
import com.skillsync.authservice.service.RefreshTokenService;
import com.skillsync.authservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private JwtService jwtService;

    @Test
    void register_returnsCreated() throws Exception {
        UserRequest request = new UserRequest("Test User", "user@example.com", "Test@1234", Role.ROLE_LEARNER);
        AuthResponse response = AuthResponse.builder()
                .userId(1L)
                .name("Test User")
                .email("user@example.com")
                .role(Role.ROLE_LEARNER)
                .accessToken("access")
                .refreshToken("refresh")
                .build();

        when(userService.register(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void login_returnsOk() throws Exception {
        LoginRequest request = new LoginRequest("user@example.com", "Test@1234");
        AuthResponse response = AuthResponse.builder()
                .userId(1L)
                .email("user@example.com")
                .accessToken("access")
                .refreshToken("refresh")
                .build();

        when(userService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access"));
    }

    @Test
    void refresh_returnsNewAccessToken() throws Exception {
        User user = new User();
        user.setEmail("user@example.com");
        user.setRole(Role.ROLE_LEARNER);
        RefreshToken token = RefreshToken.builder().user(user).build();

        when(refreshTokenService.verifyToken("refresh-token")).thenReturn(token);
        when(jwtService.generateToken(eq("user@example.com"), eq("ROLE_LEARNER"))).thenReturn("new-access");

        mockMvc.perform(post("/auth/refresh")
                        .param("refreshToken", "refresh-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access"));
    }

    @Test
    void logout_deletesToken() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .param("refreshToken", "refresh-token"))
                .andExpect(status().isOk());

        verify(refreshTokenService).deleteByToken("refresh-token");
    }
}
