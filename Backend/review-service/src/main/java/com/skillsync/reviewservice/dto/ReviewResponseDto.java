package com.skillsync.reviewservice.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO for review operations.
 * Returned to client — entity is never exposed directly.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDto {
    private Long id;
    private Long mentorId;
    private Long learnerId;
    private Long sessionId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
