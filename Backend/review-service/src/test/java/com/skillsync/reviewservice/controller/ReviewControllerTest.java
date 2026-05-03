package com.skillsync.reviewservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.reviewservice.dto.MentorRatingSummaryDto;
import com.skillsync.reviewservice.dto.ReviewRequestDto;
import com.skillsync.reviewservice.dto.ReviewResponseDto;
import com.skillsync.reviewservice.service.JwtService;
import com.skillsync.reviewservice.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private JwtService jwtService;

    @Test
    void submitReview_returnsCreated() throws Exception {
        ReviewRequestDto request = new ReviewRequestDto(1L, 2L, 3L, 5, "Great");
        ReviewResponseDto response = new ReviewResponseDto();
        response.setId(10L);
        response.setRating(5);

        when(reviewService.submitReview(any(ReviewRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void getReviewById_returnsOk() throws Exception {
        ReviewResponseDto response = response(11L, 4);
        when(reviewService.getReviewById(11L)).thenReturn(response);

        mockMvc.perform(get("/reviews/11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(4));
    }

    @Test
    void getReviewsByMentorId_returnsPage() throws Exception {
        when(reviewService.getReviewsByMentorId(2L, 0, 10, "id"))
                .thenReturn(new PageImpl<>(List.of(response(12L, 5))));

        mockMvc.perform(get("/reviews/mentor/2")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(12));
    }

    @Test
    void getReviewsByLearnerId_returnsList() throws Exception {
        when(reviewService.getReviewsByLearnerId(3L)).thenReturn(List.of(response(13L, 3)));

        mockMvc.perform(get("/reviews/learner/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rating").value(3));
    }

    @Test
    void getMentorRatingSummary_returnsSummary() throws Exception {
        MentorRatingSummaryDto summary = MentorRatingSummaryDto.builder()
                .mentorId(2L)
                .averageRating(4.5)
                .totalReviews(6)
                .build();

        when(reviewService.getMentorRatingSummary(2L)).thenReturn(summary);

        mockMvc.perform(get("/reviews/mentor/2/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(4.5))
                .andExpect(jsonPath("$.totalReviews").value(6));
    }

    @Test
    void deleteReview_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/reviews/14"))
                .andExpect(status().isNoContent());

        verify(reviewService).deleteReview(14L);
    }

    private ReviewResponseDto response(Long id, int rating) {
        ReviewResponseDto response = new ReviewResponseDto();
        response.setId(id);
        response.setRating(rating);
        return response;
    }
}
