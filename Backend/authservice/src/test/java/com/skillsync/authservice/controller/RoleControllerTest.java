package com.skillsync.authservice.controller;

import com.skillsync.authservice.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleController.class)
@AutoConfigureMockMvc(addFilters = false)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @Test
    void admin_returnsString() throws Exception {
        mockMvc.perform(get("/api/admin/data"))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin Access"));
    }

    @Test
    void user_returnsString() throws Exception {
        mockMvc.perform(get("/api/user/data"))
                .andExpect(status().isOk())
                .andExpect(content().string("User Access"));
    }
}
