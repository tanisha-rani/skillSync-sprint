package com.skillsync.sessionservice.dto;

import com.skillsync.sessionservice.entity.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SessionResponseDto {
    private Long id;
    private Long mentorId;
    private Long learnerId;
    private LocalDate sessionDate;
    private int duration;
    private String topic;
    private SessionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
