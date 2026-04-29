package com.skillsync.mentor.repository;

import com.skillsync.mentor.entity.Mentor;
import com.skillsync.mentor.entity.MentorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
    // Check if mentor already exists for this user
    boolean existsByUserId(Long userId);
    Optional<Mentor> findByUserId(Long userId);

    // Filter by status with pagination
    Page<Mentor> findByStatus(MentorStatus status, Pageable pageable);

    // Filter by skill with pagination
    Page<Mentor> findBySkillsContaining(String skill, Pageable pageable);

    // Get only available mentors
    Page<Mentor> findByIsAvailableAndStatus(boolean isAvailable, MentorStatus status, Pageable pageable);

    long countByStatus(MentorStatus status);
}
