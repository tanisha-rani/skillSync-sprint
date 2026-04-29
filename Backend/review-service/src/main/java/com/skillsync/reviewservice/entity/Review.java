package com.skillsync.reviewservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Represents the reviews table in the database.
 * Stores mentor ratings and reviews submitted by learners after sessions.
 * Each review is linked to a mentor and a learner.
 */
@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID of the mentor being reviewed
    @Column(name = "mentor_id", nullable = false)
    private Long mentorId;

    // ID of the learner who submitted the review
    @Column(name = "learner_id", nullable = false)
    private Long learnerId;

    // ID of the session this review is for
    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    // Rating between 1 and 5
    @Column(nullable = false)
    private int rating;

    // Optional written review comment
    @Column(length = 1000)
    private String comment;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
