package com.skillsync.sessionservice.dto;

import com.skillsync.sessionservice.entity.SessionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SessionRequestDto {
    @NotNull(message = "MentorId is required")
    private Long mentorId;
    @NotNull(message = "LearnerId is required")
    private Long learnerId;

    private LocalDate sessionDate;
    @Positive(message = "Duration must be greater than 0")
    private int duration;
    @NotBlank(message = "Topic of Session is required")
    private String topic;
    @NotBlank(message = "Required skill is required")
    private String requiredSkill;
}
