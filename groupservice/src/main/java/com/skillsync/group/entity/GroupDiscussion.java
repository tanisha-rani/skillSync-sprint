package com.skillsync.group.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Represents a discussion post inside a learning group.
 * Extra feature — enables async communication between group members.
 * Each post belongs to one group and is authored by one user.
 */
@Entity
@Table(name = "group_discussions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class GroupDiscussion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The group this discussion belongs to.
     * ManyToOne — many discussions can belong to one group.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    /**
     * userId of the member who posted this message.
     */
    @Column(name = "author_user_id", nullable = false)
    private Long authorUserId;

    /**
     * The actual discussion message content.
     */
    @Column(nullable = false, length = 2000)
    private String message;

    /**
     * Timestamp when the discussion post was created.
     * Auto-set by Hibernate on insert.
     */
    @CreationTimestamp
    @Column(name = "posted_at", nullable = false, updatable = false)
    private LocalDateTime postedAt;

}
