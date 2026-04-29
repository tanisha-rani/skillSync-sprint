package com.skillsync.reviewservice.controller;

import com.skillsync.reviewservice.dto.MentorRatingSummaryDto;
import com.skillsync.reviewservice.dto.ReviewRequestDto;
import com.skillsync.reviewservice.dto.ReviewResponseDto;
import com.skillsync.reviewservice.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ReviewController — REST API layer for Review Service.
 * Handles all HTTP requests related to mentor reviews and ratings.
 * Follows RESTful conventions with proper HTTP status codes.
 */
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Tag(name = "Review Service", description = "APIs for managing mentor reviews and ratings")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Submits a new review for a mentor after a completed session.
     * Returns 409 if learner already reviewed the same session.
     */
    @Operation(summary = "Submit a review", description = "Learner submits rating and review for a mentor after session.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Review submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "Review already submitted for this session")
    })
    @PostMapping
    public ResponseEntity<ReviewResponseDto> submitReview(
            @Valid @RequestBody ReviewRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.submitReview(requestDto));
    }

    /**
     * Fetches a single review by its unique ID.
     * Returns 404 if review does not exist.
     */
    @Operation(summary = "Get review by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review found"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> getReviewById(
            @Parameter(description = "Unique ID of the review") @PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    /**
     * Returns paginated list of all reviews for a specific mentor.
     * Used by learners to read mentor reviews before booking.
     */
    @Operation(summary = "Get reviews by mentor ID", description = "Returns paginated reviews for a mentor.")
    @ApiResponse(responseCode = "200", description = "Reviews fetched successfully")
    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<Page<ReviewResponseDto>> getReviewsByMentorId(
            @Parameter(description = "Unique ID of the mentor") @PathVariable Long mentorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(reviewService.getReviewsByMentorId(mentorId, page, size, sortBy));
    }

    /**
     * Returns all reviews submitted by a specific learner.
     */
    @Operation(summary = "Get reviews by learner ID")
    @GetMapping("/learner/{learnerId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByLearnerId(
            @Parameter(description = "Unique ID of the learner") @PathVariable Long learnerId) {
        return ResponseEntity.ok(reviewService.getReviewsByLearnerId(learnerId));
    }

    /**
     * Returns paginated list of all reviews — admin use only.
     */
    @Operation(summary = "Get all reviews with pagination")
    @GetMapping
    public ResponseEntity<Page<ReviewResponseDto>> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(reviewService.getAllReviews(page, size, sortBy));
    }

    /**
     * Returns average rating and total review count for a mentor.
     * Core feature — shows rating calculation logic to evaluators.
     */
    @Operation(summary = "Get mentor rating summary",
            description = "Returns average rating and total reviews for a mentor.")
    @GetMapping("/mentor/{mentorId}/summary")
    public ResponseEntity<MentorRatingSummaryDto> getMentorRatingSummary(
            @Parameter(description = "Unique ID of the mentor") @PathVariable Long mentorId) {
        return ResponseEntity.ok(reviewService.getMentorRatingSummary(mentorId));
    }

    /**
     * Deletes a review permanently by ID.
     * Returns 204 NO CONTENT on success.
     */
    @Operation(summary = "Delete review by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "Unique ID of the review") @PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
