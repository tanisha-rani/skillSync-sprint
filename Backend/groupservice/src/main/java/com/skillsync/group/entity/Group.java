package com.skillsync.group.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Core entity representing a peer learning group.
 * Members list stored in a separate join table (group_members).
 * Topics stored in a separate join table (group_topics) — extra feature.
 * creatorUserId is the user who created the group (group admin).
 */
@Entity
@Table(name = "user_groups")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Display name of the group.
     * e.g. "Spring Boot Beginners", "DSA Interview Preparation"
     */
    @Column(nullable = false)
    private String name;

    /**
     * Short description of what the group focuses on.
     */
    @Column(length = 1000)
    private String description;

    /**
     * Topics/tags associated with this group for filtering.
     * Extra feature — helps learners discover groups by topic.
     */
    @ElementCollection
    @CollectionTable(name = "group_topics", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "topic")
    private List<String> topics = new ArrayList<>();

    /**
     * userId of the person who created the group.
     * This user acts as the group admin.
     */
    @Column(name = "creator_user_id", nullable = false)
    private Long creatorUserId;

    /**
     * List of userIds who have joined this group.
     * Stored in a separate group_members table in DB.
     */
    @ElementCollection
    @CollectionTable(name = "group_members", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "user_id")
    private List<Long> memberUserIds = new ArrayList<>();

    /**
     * Maximum number of members allowed in the group.
     * Extra feature — prevents overcrowding in study groups.
     * Default is 50.
     */
    @Column(name = "max_members", nullable = false)
    private Integer maxMembers = 50;

    /**
     * Current lifecycle status of the group.
     * Default is OPEN — group accepts new members on creation.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupStatus status = GroupStatus.OPEN;

    /**
     * Timestamp when the group was created.
     * Auto-set by Hibernate, never updated.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of the last update to this group record.
     * Auto-managed by Hibernate.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
