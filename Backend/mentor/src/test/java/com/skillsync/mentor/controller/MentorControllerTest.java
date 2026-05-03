package com.skillsync.mentor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.mentor.dto.MentorRequestDto;
import com.skillsync.mentor.dto.MentorResponseDto;
import com.skillsync.mentor.repository.MentorRepository;
import com.skillsync.mentor.service.JwtService;
import com.skillsync.mentor.service.MentorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MentorController.class)
@AutoConfigureMockMvc(addFilters = false)
class MentorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MentorService mentorService;

    @MockBean
    private MentorRepository mentorRepository;

    @MockBean
    private JwtService jwtService;

    @Test
    void applyAsMentor_returnsCreated() throws Exception {
        MentorRequestDto request = new MentorRequestDto(
                "Bio",
                5,
                List.of("Java"),
                200.0,
                "Test User",
                "user@example.com",
                3L
        );
        MentorResponseDto response = new MentorResponseDto();
        response.setId(1L);

        when(mentorService.applyAsMentor(any(MentorRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/mentors/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }
}
