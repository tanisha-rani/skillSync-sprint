package com.skillsync.reviewservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Request DTO for submitting a review.
 * Separates API contract from internal entity structure.
 * Validation ensures data integrity before reaching service layer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {

    @NotNull(message = "Mentor ID is required")
    private Long mentorId;

    @NotNull(message = "Learner ID is required")
    private Long learnerId;

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    // Rating must be between 1 and 5
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    private int rating;

    // Optional comment — max 1000 characters
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String comment;
}
