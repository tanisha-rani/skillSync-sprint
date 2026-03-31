package com.skillsync.mentor.service.impl;

import com.skillsync.mentor.dto.MentorRequestDto;
import com.skillsync.mentor.dto.MentorResponseDto;
import com.skillsync.mentor.entity.Mentor;
import com.skillsync.mentor.entity.MentorStatus;
import com.skillsync.mentor.exception.MentorAlreadyExistsException;
import com.skillsync.mentor.exception.MentorNotFoundException;
import com.skillsync.mentor.repository.MentorRepository;
import com.skillsync.mentor.service.MentorService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Implementation of MentorService interface.
 * Contains all business logic for mentor management operations.
 * ModelMapper used to avoid manual entity-to-DTO conversion.
 */
@Service
@RequiredArgsConstructor
public class MentorServiceImpl implements MentorService {

    private final MentorRepository mentorRepository;
    private final ModelMapper modelMapper;

    /**
     * Handles mentor application from a learner.
     * Checks if mentor profile already exists for given userId.
     * Default status is PENDING — admin must approve before activation.
     */
    @Override
    public MentorResponseDto applyAsMentor(MentorRequestDto requestDto) {
        if (mentorRepository.existsByUserId(requestDto.getUserId())) {
            throw new MentorAlreadyExistsException(requestDto.getUserId());
        }
        Mentor mentor = modelMapper.map(requestDto, Mentor.class);
        mentor.setUserId(requestDto.getUserId());
        mentor.setStatus(MentorStatus.PENDING);
        mentor.setAverageRating(0.0);
        mentor.setTotalReviews(0);
        mentor.setAvailable(false);
        Mentor savedMentor = mentorRepository.save(mentor);
        return modelMapper.map(savedMentor, MentorResponseDto.class);
    }

    /**
     * Fetches a single mentor by their unique ID.
     * Throws MentorNotFoundException if no mentor exists with given ID.
     */
    @Override
    public MentorResponseDto getMentorById(Long id) {
        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new MentorNotFoundException(id));
        return modelMapper.map(mentor, MentorResponseDto.class);
    }

    /**
     * Returns paginated list of all mentors regardless of status.
     * Supports dynamic sorting by any mentor field.
     */
    @Override
    public Page<MentorResponseDto> getAllMentors(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return mentorRepository.findAll(pageable)
                .map(mentor -> modelMapper.map(mentor, MentorResponseDto.class));
    }

    /**
     * Updates an existing mentor profile by ID.
     * Fetches existing mentor, updates fields, saves and returns updated data.
     * Throws MentorNotFoundException if mentor does not exist.
     */
    @Override
    public MentorResponseDto updateMentor(Long id, MentorRequestDto requestDto) {
        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new MentorNotFoundException(id));

        mentor.setBio(requestDto.getBio());
        mentor.setExperienceYears(requestDto.getExperienceYears());
        mentor.setSkills(requestDto.getSkills());
        mentor.setHourlyRate(requestDto.getHourlyRate());

        Mentor updatedMentor = mentorRepository.save(mentor);
        return modelMapper.map(updatedMentor, MentorResponseDto.class);
    }

    /**
     * Soft deletes a mentor by setting status = REJECTED.
     * Data is retained in DB for audit purposes.
     * Throws MentorNotFoundException if mentor does not exist.
     */
    @Override
    public void deleteMentor(Long id) {
        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new MentorNotFoundException(id));
        mentor.setStatus(MentorStatus.REJECTED);
        mentorRepository.save(mentor);
    }

    /**
     * Admin approves a mentor application.
     * Sets status to APPROVED and makes mentor available.
     * Throws MentorNotFoundException if mentor does not exist.
     */
    @Override
    public MentorResponseDto approveMentor(Long id) {
        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new MentorNotFoundException(id));
        mentor.setStatus(MentorStatus.APPROVED);
        mentor.setAvailable(true);
        Mentor updatedMentor = mentorRepository.save(mentor);
        return modelMapper.map(updatedMentor, MentorResponseDto.class);
    }

    /**
     * Admin rejects a mentor application.
     * Sets status to REJECTED and makes mentor unavailable.
     * Throws MentorNotFoundException if mentor does not exist.
     */
    @Override
    public MentorResponseDto rejectMentor(Long id) {
        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new MentorNotFoundException(id));
        mentor.setStatus(MentorStatus.REJECTED);
        mentor.setAvailable(false);
        Mentor updatedMentor = mentorRepository.save(mentor);
        return modelMapper.map(updatedMentor, MentorResponseDto.class);
    }

    /**
     * Returns paginated list of mentors filtered by status.
     * Useful for admin to view PENDING applications separately.
     */
    @Override
    public Page<MentorResponseDto> getMentorsByStatus(MentorStatus status, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return mentorRepository.findByStatus(status, pageable)
                .map(mentor -> modelMapper.map(mentor, MentorResponseDto.class));
    }

    /**
     * Returns paginated list of mentors filtered by skill.
     * Learners use this to find mentors for a specific skill.
     */
    @Override
    public Page<MentorResponseDto> getMentorsBySkill(String skill, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return mentorRepository.findBySkillsContaining(skill, pageable)
                .map(mentor -> modelMapper.map(mentor, MentorResponseDto.class));
    }

    /**
     * Returns paginated list of only available and approved mentors.
     * Used by learners to browse mentors ready for booking.
     */
    @Override
    public Page<MentorResponseDto> getAvailableMentors(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return mentorRepository.findByIsAvailableAndStatus(true, MentorStatus.APPROVED, pageable)
                .map(mentor -> modelMapper.map(mentor, MentorResponseDto.class));
    }

    /**
     * Toggles mentor availability between true and false.
     * Only approved mentors should be able to toggle availability.
     * Throws MentorNotFoundException if mentor does not exist.
     */
    @Override
    public MentorResponseDto toggleAvailability(Long id) {
        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new MentorNotFoundException(id));
        mentor.setAvailable(!mentor.isAvailable());
        Mentor updatedMentor = mentorRepository.save(mentor);
        return modelMapper.map(updatedMentor, MentorResponseDto.class);
    }

    @Override
    public void updateRating(Long mentorId, Double averageRating, Integer totalReviews) {
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new MentorNotFoundException(mentorId));
        mentor.setAverageRating(averageRating);
        mentor.setTotalReviews(totalReviews);
        mentorRepository.save(mentor);
    }
}
