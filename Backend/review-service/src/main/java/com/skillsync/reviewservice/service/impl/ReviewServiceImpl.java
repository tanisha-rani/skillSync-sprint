package com.skillsync.reviewservice.service.impl;

import com.skillsync.reviewservice.dto.MentorRatingSummaryDto;
import com.skillsync.reviewservice.dto.ReviewRequestDto;
import com.skillsync.reviewservice.dto.ReviewResponseDto;
import com.skillsync.reviewservice.entity.Review;
import com.skillsync.reviewservice.exception.ReviewAlreadyExistsException;
import com.skillsync.reviewservice.exception.ReviewNotFoundException;
import com.skillsync.reviewservice.repository.ReviewRepository;
import com.skillsync.reviewservice.service.ReviewService;
import com.skillsync.reviewservice.client.MentorFeignClient;
import com.skillsync.reviewservice.client.NotificationFeignClient;
import com.skillsync.reviewservice.client.NotificationRequestDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of ReviewService interface.
 * Contains all business logic for review and rating operations.
 * ModelMapper used for entity-DTO conversion.
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;

    private final MentorFeignClient mentorFeignClient;
    private final NotificationFeignClient notificationFeignClient;

    /**
     * Submits a new review for a mentor.
     * Checks if learner already reviewed the same session.
     * Throws ReviewAlreadyExistsException if duplicate review found.
     */
    @Override
    public ReviewResponseDto submitReview(ReviewRequestDto requestDto) {
        if (reviewRepository.existsByLearnerIdAndSessionId(
                requestDto.getLearnerId(), requestDto.getSessionId())) {
            throw new ReviewAlreadyExistsException(requestDto.getSessionId());
        }

        Review review = new Review();
        review.setMentorId(requestDto.getMentorId());
        review.setLearnerId(requestDto.getLearnerId());
        review.setSessionId(requestDto.getSessionId());
        review.setRating(requestDto.getRating());
        review.setComment(requestDto.getComment());
        Review saved = reviewRepository.save(review);

        // Sync updated rating back to mentor-service
        MentorRatingSummaryDto summary = getMentorRatingSummary(requestDto.getMentorId());
        mentorFeignClient.updateMentorRating(
                requestDto.getMentorId(),
                summary.getAverageRating(),
                summary.getTotalReviews()
        );

        // Notify mentor they received a new review
        notificationFeignClient.sendNotification(
                NotificationRequestDto.builder()
                        .userId(requestDto.getMentorId())
                        .subject("You received a new review")
                        .message("A learner rated you " + requestDto.getRating() + " stars.")
                        .type("REVIEW_RECEIVED")
                        .referenceId(saved.getId())
                        .build()
        );

        return modelMapper.map(saved, ReviewResponseDto.class);
    }


    /**
     * Fetches a single review by its unique ID.
     * Throws ReviewNotFoundException if review does not exist.
     */
    @Override
    public ReviewResponseDto getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
        return modelMapper.map(review, ReviewResponseDto.class);
    }

    /**
     * Returns paginated list of reviews for a specific mentor.
     * Used by learners to read mentor reviews before booking.
     */
    @Override
    public Page<ReviewResponseDto> getReviewsByMentorId(Long mentorId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return reviewRepository.findByMentorId(mentorId, pageable)
                .map(review -> modelMapper.map(review, ReviewResponseDto.class));
    }

    /**
     * Returns all reviews submitted by a specific learner.
     * Used by learner to view their own review history.
     */
    @Override
    public List<ReviewResponseDto> getReviewsByLearnerId(Long learnerId) {
        return reviewRepository.findByLearnerId(learnerId)
                .stream()
                .map(review -> modelMapper.map(review, ReviewResponseDto.class))
                .toList();
    }

    /**
     * Returns paginated list of all reviews — admin use only.
     * Supports dynamic sorting by any review field.
     */
    @Override
    public Page<ReviewResponseDto> getAllReviews(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return reviewRepository.findAll(pageable)
                .map(review -> modelMapper.map(review, ReviewResponseDto.class));
    }

    /**
     * Calculates average rating and total reviews for a mentor.
     * Returns MentorRatingSummaryDto with averageRating and totalReviews.
     * Returns 0.0 average if no reviews exist yet.
     */
    @Override
    public MentorRatingSummaryDto getMentorRatingSummary(Long mentorId) {
        Double avg = reviewRepository.calculateAverageRating(mentorId);
        long total = reviewRepository.countByMentorId(mentorId);
        return MentorRatingSummaryDto.builder()
                .mentorId(mentorId)
                .averageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0)
                .totalReviews((int) total)
                .build();
    }

    /**
     * Permanently deletes a review by ID.
     * Throws ReviewNotFoundException if review does not exist.
     */
    @Override
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
        reviewRepository.delete(review);
    }
}
