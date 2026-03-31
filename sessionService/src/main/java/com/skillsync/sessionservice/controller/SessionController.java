package com.skillsync.sessionservice.controller;

import com.skillsync.sessionservice.dto.SessionRequestDto;
import com.skillsync.sessionservice.dto.SessionResponseDto;
import com.skillsync.sessionservice.entity.SessionStatus;
import com.skillsync.sessionservice.repository.SessionRepository;
import com.skillsync.sessionservice.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
@Tag(name = "Session Controller", description = "Manage mentoring sessions")
public class SessionController {
    private final SessionRepository sessionRepository;
    private final SessionService sessionService;

    //-----Core Operations-----------------------------------------

    @PostMapping
    @Operation(summary = "Book a new session")
    public ResponseEntity<SessionResponseDto> bookSession(
            @Valid @RequestBody SessionRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessionService.bookSession(requestDto));
    }

    @PutMapping("/{id}/accept")
    @Operation(summary = "Accept a session (mentor action)")
    public ResponseEntity<SessionResponseDto> acceptSession(@PathVariable Long id) {
        return ResponseEntity.ok(sessionService.acceptSession(id));
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject a session (mentor action)")
    public ResponseEntity<SessionResponseDto> rejectSession(@PathVariable Long id) {
        return ResponseEntity.ok(sessionService.rejectSession(id));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel a session (learner or mentor)")
    public ResponseEntity<SessionResponseDto> cancelSession(@PathVariable Long id) {
        return ResponseEntity.ok(sessionService.cancelSession(id));
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Mark a session as completed")
    public ResponseEntity<SessionResponseDto> completeSession(@PathVariable Long id) {
        return ResponseEntity.ok(sessionService.completeSession(id));
    }

    // ----Read Operations----------------------------------------------------

    @GetMapping("/{id}")
    @Operation(summary = "Get session by ID")
    public ResponseEntity<SessionResponseDto> getSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(sessionService.getSessionById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all sessions for a user (as learner or mentor)")
    public ResponseEntity<List<SessionResponseDto>> getSessionsByUserId(
            @PathVariable Long userId) {
        return ResponseEntity.ok(sessionService.getSessionsByUserId(userId));
    }

    @GetMapping("/mentor/{mentorId}")
    @Operation(summary = "Get all sessions for a mentor")
    public ResponseEntity<List<SessionResponseDto>> getSessionsByMentorId(
            @PathVariable Long mentorId) {
        return ResponseEntity.ok(sessionService.getSessionsByMentorId(mentorId));
    }

    @GetMapping
    @Operation(summary = "Get all sessions with pagination")
    public ResponseEntity<Page<SessionResponseDto>> getAllSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        return ResponseEntity.ok(sessionService.getAllSessions(page, size, sortBy));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get sessions filtered by status with pagination")
    public ResponseEntity<Page<SessionResponseDto>> getSessionsByStatus(
            @PathVariable SessionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        return ResponseEntity.ok(sessionService.getSessionsByStatus(status, page, size, sortBy));
    }

    @GetMapping("/admin/stats")
    public Map<String, Long> getStats() {
        long totalSessions = sessionRepository.count();
        return Map.of("totalSessions", totalSessions);
    }
}