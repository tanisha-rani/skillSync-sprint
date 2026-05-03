package com.skillsync.sessionservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.sessionservice.dto.SessionRequestDto;
import com.skillsync.sessionservice.dto.SessionResponseDto;
import com.skillsync.sessionservice.entity.SessionStatus;
import com.skillsync.sessionservice.repository.SessionRepository;
import com.skillsync.sessionservice.service.JwtService;
import com.skillsync.sessionservice.service.SessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessionController.class)
@AutoConfigureMockMvc(addFilters = false)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private SessionRepository sessionRepository;

    @MockBean
    private JwtService jwtService;

    @Test
    void bookSession_returnsCreated() throws Exception {
        SessionRequestDto request = new SessionRequestDto(1L, 2L, LocalDate.now(), 60, "Java mentoring", "Java");
        SessionResponseDto response = new SessionResponseDto();
        response.setId(10L);
        response.setStatus(SessionStatus.REQUESTED);

        when(sessionService.bookSession(any(SessionRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void acceptSession_returnsOk() throws Exception {
        SessionResponseDto response = response(11L, SessionStatus.ACCEPTED);
        when(sessionService.acceptSession(11L)).thenReturn(response);

        mockMvc.perform(put("/sessions/11/accept"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    void rejectSession_returnsOk() throws Exception {
        when(sessionService.rejectSession(12L)).thenReturn(response(12L, SessionStatus.REJECTED));

        mockMvc.perform(put("/sessions/12/reject"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void getSessionById_returnsOk() throws Exception {
        when(sessionService.getSessionById(13L)).thenReturn(response(13L, SessionStatus.REQUESTED));

        mockMvc.perform(get("/sessions/13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(13));
    }

    @Test
    void getSessionsByUserId_returnsList() throws Exception {
        when(sessionService.getSessionsByUserId(1L)).thenReturn(List.of(response(14L, SessionStatus.COMPLETED)));

        mockMvc.perform(get("/sessions/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
    }

    @Test
    void getSessionsByStatus_returnsPage() throws Exception {
        when(sessionService.getSessionsByStatus(SessionStatus.ACCEPTED, 0, 10, "createdAt"))
                .thenReturn(new PageImpl<>(List.of(response(15L, SessionStatus.ACCEPTED))));

        mockMvc.perform(get("/sessions/status/ACCEPTED")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(15));
    }

    @Test
    void getStats_returnsTotalSessions() throws Exception {
        when(sessionRepository.count()).thenReturn(9L);

        mockMvc.perform(get("/sessions/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSessions").value(9));
    }

    private SessionResponseDto response(Long id, SessionStatus status) {
        SessionResponseDto response = new SessionResponseDto();
        response.setId(id);
        response.setStatus(status);
        return response;
    }
}
