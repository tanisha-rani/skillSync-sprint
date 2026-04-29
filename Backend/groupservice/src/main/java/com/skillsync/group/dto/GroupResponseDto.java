package com.skillsync.group.dto;

import com.skillsync.group.entity.GroupStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for Group operations.
 * Returned to the client — the entity is never exposed directly.
 * Includes memberCount as a derived field for convenience.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupResponseDto {

    private Long id;
    private String name;
    private String description;
    private List<String> topics;
    private Long creatorUserId;
    private List<Long> memberUserIds;

    /**
     * Derived field — total members currently in the group.
     * Computed in service layer from memberUserIds.size().
     * Saves client from computing it on their side.
     */
    private int memberCount;

    private int maxMembers;
    private GroupStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
