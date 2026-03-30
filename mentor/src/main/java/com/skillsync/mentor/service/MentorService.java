package com.skillsync.mentor.service;

import com.skillsync.mentor.dto.MentorRequestDto;
import com.skillsync.mentor.dto.MentorResponseDto;
import com.skillsync.mentor.entity.MentorStatus;
import org.springframework.data.domain.Page;

/**
 * Service contract for Mentor business logic.
 * Programming to interface — implementation can be swapped without affecting controller.
 */
public interface MentorService {

    // Core features
    MentorResponseDto applyAsMentor(MentorRequestDto requestDto);

    MentorResponseDto getMentorById(Long id);

    Page<MentorResponseDto> getAllMentors(int page, int size, String sortBy);
    MentorResponseDto updateMentor(Long id, MentorRequestDto requestDto);
    void deleteMentor(Long id);

    // Admin features
    MentorResponseDto approveMentor(Long id);
    MentorResponseDto rejectMentor(Long id);

    // Extra features
    Page<MentorResponseDto> getMentorsByStatus(MentorStatus status, int page, int size, String sortBy);
    Page<MentorResponseDto> getMentorsBySkill(String skill, int page, int size, String sortBy);
    Page<MentorResponseDto> getAvailableMentors(int page, int size, String sortBy);
    MentorResponseDto toggleAvailability(Long id);

    void updateRating(Long mentorId, Double averageRating, Integer totalReviews);
}