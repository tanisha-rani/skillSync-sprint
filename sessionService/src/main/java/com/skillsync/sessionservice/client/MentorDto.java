package com.skillsync.sessionservice.client;

import lombok.Data;
import java.util.List;

@Data
public class MentorDto {
    private Long id;
    private Long userId;
    private String bio;
    private List<String> skills;
    private Double averageRating;
    private Integer totalReviews;
    private boolean available;
    private String status;
}