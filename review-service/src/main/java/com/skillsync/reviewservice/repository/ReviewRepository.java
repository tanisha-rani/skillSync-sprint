package com.skillsync.reviewservice.repository;

import com.skillsync.reviewservice.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Data access layer for Review entity.
 * Extends JpaRepository — provides built-in CRUD + pagination.
 * Custom queries for mentor-specific review operations.
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Get all reviews for a specific mentor with pagination
    Page<Review> findByMentorId(Long mentorId, Pageable pageable);

    // Get all reviews by a specific learner
    List<Review> findByLearnerId(Long learnerId);

    // Check if learner already reviewed a session
    boolean existsByLearnerIdAndSessionId(Long learnerId, Long sessionId);

    // Calculate average rating for a mentor
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.mentorId = :mentorId")
    Double calculateAverageRating(@Param("mentorId") Long mentorId);

    // Count total reviews for a mentor
    long countByMentorId(Long mentorId);
}
