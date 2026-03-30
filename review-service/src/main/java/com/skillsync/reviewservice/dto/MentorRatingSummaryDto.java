package com.skillsync.reviewservice.dto;

import lombok.*;

/**
 * DTO for returning mentor average rating summary.
 * Calculated from all reviews for a mentor.
 * Used by evaluators to see rating calculation logic.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorRatingSummaryDto {
    private Long mentorId;
    private double averageRating;
    private int totalReviews;
}
