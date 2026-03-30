package com.skillsync.sessionservice.repository;

import com.skillsync.sessionservice.entity.Session;
import com.skillsync.sessionservice.entity.SessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    // Get all sessions for a learner or mentor
    List<Session> findByLearnerId(Long learnerId);
    List<Session> findByMentorId(Long mentorId);

    // Filter by status with pagination
    Page<Session> findByStatus(SessionStatus status, Pageable pageable);

    // Get sessions by userId (both learner and mentor)
    List<Session> findByLearnerIdOrMentorId(Long learnerId, Long mentorId);
}
