package com.skillsync.reviewservice.service;

import com.skillsync.reviewservice.dto.MentorRatingSummaryDto;
import com.skillsync.reviewservice.dto.ReviewRequestDto;
import com.skillsync.reviewservice.dto.ReviewResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service contract for Review business logic.
 * Programming to interface — implementation can be swapped without affecting controller.
 */
public interface ReviewService {

    // Submit a new review for a mentor after session
    ReviewResponseDto submitReview(ReviewRequestDto requestDto);

    // Get review by id
    ReviewResponseDto getReviewById(Long id);

    // Get all reviews for a specific mentor with pagination
    Page<ReviewResponseDto> getReviewsByMentorId(Long mentorId, int page, int size, String sortBy);

    // Get all reviews submitted by a learner
    List<ReviewResponseDto> getReviewsByLearnerId(Long learnerId);

    // Get all reviews with pagination — admin use
    Page<ReviewResponseDto> getAllReviews(int page, int size, String sortBy);

    // Calculate average rating for a mentor
    MentorRatingSummaryDto getMentorRatingSummary(Long mentorId);

    // Delete a review by id
    void deleteReview(Long id);
}
