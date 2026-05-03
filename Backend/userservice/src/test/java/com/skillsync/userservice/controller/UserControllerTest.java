package com.skillsync.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.userservice.dto.UserRequestDto;
import com.skillsync.userservice.dto.UserResponseDto;
import com.skillsync.userservice.entity.Role;
import com.skillsync.userservice.repository.UserRepository;
import com.skillsync.userservice.service.JwtService;
import com.skillsync.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @Test
    void createUser_returnsCreated() throws Exception {
        UserRequestDto request = new UserRequestDto("Test User", "user@example.com", Role.LEARNER);
        UserResponseDto response = new UserResponseDto();
        response.setId(1L);
        response.setEmail("user@example.com");

        when(userService.createUser(any(UserRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getUserById_returnsOk() throws Exception {
        UserResponseDto response = new UserResponseDto();
        response.setId(2L);
        response.setEmail("learner@example.com");

        when(userService.getUserById(2L)).thenReturn(response);

        mockMvc.perform(get("/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("learner@example.com"));
    }

    @Test
    void getAllUsers_returnsPage() throws Exception {
        UserResponseDto response = new UserResponseDto();
        response.setId(3L);
        response.setName("Page User");

        when(userService.getAllUsers(0, 10, "id")).thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Page User"));
    }

    @Test
    void updateUser_returnsOk() throws Exception {
        UserRequestDto request = new UserRequestDto("Updated User", "updated@example.com", Role.MENTOR);
        UserResponseDto response = new UserResponseDto();
        response.setId(4L);
        response.setName("Updated User");

        when(userService.updateUser(any(Long.class), any(UserRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/users/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated User"));
    }

    @Test
    void deleteUser_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/users/5"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(5L);
    }

    @Test
    void getUsersByRole_returnsPage() throws Exception {
        UserResponseDto response = new UserResponseDto();
        response.setId(6L);
        response.setRole(Role.LEARNER);

        when(userService.getUsersByRole(Role.LEARNER, 0, 10, "id"))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/users/role/LEARNER")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].role").value("LEARNER"));
    }

    @Test
    void getStats_returnsTotalUsers() throws Exception {
        when(userRepository.count()).thenReturn(7L);

        mockMvc.perform(get("/users/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(7));
    }
}
