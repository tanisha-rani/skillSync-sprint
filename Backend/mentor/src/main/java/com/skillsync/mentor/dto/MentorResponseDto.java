package com.skillsync.mentor.dto;

import com.skillsync.mentor.entity.MentorStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for Mentor operations.
 * Returned to client — never expose entity directly.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorResponseDto {

    private Long id;
    private String bio;
    private int experienceYears;
    private List<String> skills;
    private double averageRating;
    private int totalReviews;
    private double hourlyRate;
    private boolean isAvailable;
    private MentorStatus status;
    private String rejectionReason;
    private String applicantName;
    private String applicantEmail;
    private Long userId;
    private LocalDateTime createdAt;
}
