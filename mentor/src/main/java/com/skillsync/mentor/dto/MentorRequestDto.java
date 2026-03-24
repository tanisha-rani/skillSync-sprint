package com.skillsync.mentor.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

/**
 * Request DTO for Mentor operations.
 * Separates API contract from internal entity structure.
 * Validation annotations ensure data integrity before reaching service layer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MentorRequestDto {

    /**
     * Short biography of the mentor.
     * Optional field — mentor can update later.
     */
    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;

    /**
     * Total years of professional experience.
     * Cannot be negative.
     */
    @Min(value = 0, message = "Experience cannot be negative")
    private int experienceYears;

    /**
     * List of skills mentor can teach.
     * At least one skill is mandatory.
     * Stored in separate mentor_skills table in DB.
     */
    @NotEmpty(message = "At least one skill is required")
    private List<String> skills;

    /**
     * Per hour charges by the mentor.
     * Must be greater than 0.
     */
    @Positive(message = "Hourly rate must be greater than 0")
    private double hourlyRate;

    /**
     * ID of the user who is applying as mentor.
     * Links mentor profile to existing user account.
     */
    @NotNull(message = "UserId is required")
    private Long userId;
}