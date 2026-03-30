package com.skillsync.reviewservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "MENTOR-SERVICE",
        fallback = MentorFeignClientFallback.class
)
public interface MentorFeignClient {

    @GetMapping("/mentors/{id}")
    MentorDto getMentorById(@PathVariable("id") Long id);

    // Called by review-service after a new review is submitted
    @PutMapping("/mentors/{id}/rating")
    void updateMentorRating(
            @PathVariable("id") Long mentorId,
            @RequestParam("averageRating") Double averageRating,
            @RequestParam("totalReviews") Integer totalReviews
    );
}