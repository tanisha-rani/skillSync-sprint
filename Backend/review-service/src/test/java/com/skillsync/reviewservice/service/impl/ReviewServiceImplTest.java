package com.skillsync.reviewservice.service.impl;

import com.skillsync.reviewservice.dto.MentorRatingSummaryDto;
import com.skillsync.reviewservice.dto.ReviewRequestDto;
import com.skillsync.reviewservice.exception.ReviewAlreadyExistsException;
import com.skillsync.reviewservice.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    void submitReview_whenExists_throwsException() {
        ReviewRequestDto request = new ReviewRequestDto(1L, 2L, 3L, 4, "Ok");
        when(reviewRepository.existsByLearnerIdAndSessionId(2L, 3L)).thenReturn(true);

        assertThrows(ReviewAlreadyExistsException.class, () -> reviewService.submitReview(request));
    }

    @Test
    void getMentorRatingSummary_roundsAverage() {
        when(reviewRepository.calculateAverageRating(1L)).thenReturn(4.66);
        when(reviewRepository.countByMentorId(1L)).thenReturn(3L);

        MentorRatingSummaryDto result = reviewService.getMentorRatingSummary(1L);

        assertEquals(4.7, result.getAverageRating());
        assertEquals(3, result.getTotalReviews());
    }
}
