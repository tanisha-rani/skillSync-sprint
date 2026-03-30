package com.skillsync.group.service;

import com.skillsync.group.dto.*;
import com.skillsync.group.entity.GroupStatus;
import org.springframework.data.domain.Page;

/**
 * Service contract for Group business logic.
 * Programming to interface — implementation can be swapped without affecting controller.
 */
public interface GroupService {

    // ─── Core CRUD ────────────────────────────────────────────────────────────

    GroupResponseDto createGroup(GroupRequestDto requestDto);

    GroupResponseDto getGroupById(Long id);

    Page<GroupResponseDto> getAllGroups(int page, int size, String sortBy);

    GroupResponseDto updateGroup(Long id, Long requestingUserId, GroupRequestDto requestDto);

    void deleteGroup(Long id, Long requestingUserId);

    // ─── Membership ───────────────────────────────────────────────────────────

    GroupResponseDto joinGroup(Long groupId, Long userId);

    GroupResponseDto leaveGroup(Long groupId, Long userId);

    Page<GroupResponseDto> getGroupsByMember(Long userId, int page, int size, String sortBy);

    // ─── Filter & Search ──────────────────────────────────────────────────────

    Page<GroupResponseDto> getGroupsByStatus(GroupStatus status, int page, int size, String sortBy);

    Page<GroupResponseDto> getGroupsByTopic(String topic, int page, int size, String sortBy);

    Page<GroupResponseDto> getGroupsByCreator(Long creatorUserId, int page, int size, String sortBy);

    Page<GroupResponseDto> searchGroupsByName(String keyword, int page, int size, String sortBy);

    // ─── Admin Actions ────────────────────────────────────────────────────────

    GroupResponseDto changeGroupStatus(Long id, Long requestingUserId, GroupStatus newStatus);

    // ─── Discussions (Extra Feature) ─────────────────────────────────────────

    DiscussionResponseDto postDiscussion(Long groupId, DiscussionRequestDto requestDto);

    Page<DiscussionResponseDto> getDiscussionsByGroup(Long groupId, int page, int size);

}
