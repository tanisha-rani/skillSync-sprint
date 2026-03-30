package com.skillsync.sessionservice.service;

import com.skillsync.sessionservice.dto.SessionRequestDto;
import com.skillsync.sessionservice.dto.SessionResponseDto;
import com.skillsync.sessionservice.entity.SessionStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SessionService {
    // Core
    SessionResponseDto bookSession(SessionRequestDto requestDto);
    SessionResponseDto acceptSession(Long id);
    SessionResponseDto rejectSession(Long id);
    SessionResponseDto cancelSession(Long id);
    SessionResponseDto completeSession(Long id);

    // Get operations
    SessionResponseDto getSessionById(Long id);
    List<SessionResponseDto> getSessionsByUserId(Long userId);
    List<SessionResponseDto> getSessionsByMentorId(Long mentorId);
    Page<SessionResponseDto> getAllSessions(int page, int size, String sortBy);
    Page<SessionResponseDto> getSessionsByStatus(SessionStatus status, int page, int size, String sortBy);
}
