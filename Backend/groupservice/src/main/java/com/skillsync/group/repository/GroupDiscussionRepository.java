package com.skillsync.group.repository;

import com.skillsync.group.entity.GroupDiscussion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository layer for GroupDiscussion entity.
 * Supports paginated fetching of all discussions within a given group.
 */
public interface GroupDiscussionRepository extends JpaRepository<GroupDiscussion, Long> {

    // Fetch all discussion posts for a given group — sorted and paginated
    Page<GroupDiscussion> findByGroupId(Long groupId, Pageable pageable);

    // Count total discussions in a group — useful for analytics/display
    long countByGroupId(Long groupId);

}
