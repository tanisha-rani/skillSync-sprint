package com.skillsync.group.repository;

import com.skillsync.group.entity.Group;
import com.skillsync.group.entity.GroupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository layer for Group entity.
 * Spring Data JPA generates all query implementations at runtime.
 */
public interface GroupRepository extends JpaRepository<Group, Long> {

    // Filter groups by their current status (OPEN / CLOSED / ARCHIVED)
    Page<Group> findByStatus(GroupStatus status, Pageable pageable);

    // Find all groups created by a specific user
    Page<Group> findByCreatorUserId(Long creatorUserId, Pageable pageable);

    // Find groups that contain a specific topic tag
    Page<Group> findByTopicsContaining(String topic, Pageable pageable);

    // Find groups where a specific user is a member
    Page<Group> findByMemberUserIdsContaining(Long userId, Pageable pageable);

    // Find groups by name (case-insensitive partial match) — search feature
    Page<Group> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

}
