package com.skillsync.reviewservice.client;

import org.springframework.stereotype.Component;

@Component
public class MentorFeignClientFallback implements MentorFeignClient {

    @Override
    public MentorDto getMentorById(Long id) {
        MentorDto fallback = new MentorDto();
        fallback.setId(id);
        fallback.setBio("Mentor info unavailable");
        return fallback;
    }

    @Override
    public void updateMentorRating(Long mentorId, Double avg, Integer total) {
        // silently fail — rating sync will retry
    }
}
