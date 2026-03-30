package com.skillsync.authservice.controller;

import com.skillsync.authservice.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TokenValidationController.class)
@AutoConfigureMockMvc(addFilters = false)
class TokenValidationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @Test
    void validateToken_returnsDetails() throws Exception {
        when(jwtService.extractEmail("token")).thenReturn("user@example.com");
        when(jwtService.extractRole("token")).thenReturn("ROLE_LEARNER");

        mockMvc.perform(get("/auth/validate")
                        .param("token", "token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.role").value("ROLE_LEARNER"))
                .andExpect(jsonPath("$.valid").value("true"));
    }
}
