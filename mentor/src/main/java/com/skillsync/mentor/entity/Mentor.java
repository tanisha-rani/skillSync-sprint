package com.skillsync.mentor.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "mentors")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Mentor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bio;

    @Column(nullable = false)
    private int experienceYears;

    @ElementCollection
    @CollectionTable(name = "mentor_skills", joinColumns = @JoinColumn(name = "mentor_id"))
    @Column(name = "skill")
    private List<String> skills;

    @Column(name = "average_rating")
    private double averageRating;

    @Column(name = "total_reviews")
    private int totalReviews;

    @Column(nullable = false)
    private double hourlyRate;

    @Column(name = "is_available",columnDefinition = "TINYINT(1)")
    private boolean isAvailable=true;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MentorStatus status = MentorStatus.PENDING;

    @Column(name = "user_id", nullable = false)
    private Long userId;

}