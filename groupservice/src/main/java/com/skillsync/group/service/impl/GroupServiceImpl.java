package com.skillsync.group.service.impl;

import com.skillsync.group.dto.*;
import com.skillsync.group.entity.Group;
import com.skillsync.group.entity.GroupDiscussion;
import com.skillsync.group.entity.GroupStatus;
import com.skillsync.group.exception.*;
import com.skillsync.group.repository.GroupDiscussionRepository;
import com.skillsync.group.repository.GroupRepository;
import com.skillsync.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Implementation of GroupService interface.
 * Contains all business logic for peer learning group management.
 * ModelMapper used to avoid manual entity-to-DTO conversion boilerplate.
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupDiscussionRepository groupDiscussionRepository;
    private final ModelMapper modelMapper;

    // ─────────────────────────────────────────────────────────────────────────
    // Core CRUD
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Creates a new peer learning group.
     * Creator is automatically added as the first member of the group.
     * Default status is OPEN — group is immediately visible and joinable.
     */
    @Override
    public GroupResponseDto createGroup(GroupRequestDto requestDto) {
        Group group = modelMapper.map(requestDto, Group.class);
        group.setId(null);
        if (requestDto.getMaxMembers() == null) {
            group.setMaxMembers(50);
        }

        Group savedGroup = groupRepository.save(group);
        return mapToResponseDto(savedGroup);
    }

    /**
     * Fetches a single group by its unique ID.
     * Throws GroupNotFoundException if no group exists with given ID.
     */
    @Override
    public GroupResponseDto getGroupById(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(id));
        return mapToResponseDto(group);
    }

    /**
     * Returns paginated list of all groups regardless of status.
     * Supports dynamic sorting by any group field.
     */
    @Override
    public Page<GroupResponseDto> getAllGroups(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return groupRepository.findAll(pageable)
                .map(this::mapToResponseDto);
    }

    /**
     * Updates group details — name, description, topics, maxMembers.
     * Only the group creator is allowed to update the group.
     * Throws UnauthorizedActionException if requestingUserId is not the creator.
     * Throws GroupNotFoundException if group does not exist.
     */
    @Override
    public GroupResponseDto updateGroup(Long id, Long requestingUserId, GroupRequestDto requestDto) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(id));

        // Only the creator can update group details
        if (!group.getCreatorUserId().equals(requestingUserId)) {
            throw new UnauthorizedActionException("update this group");
        }

        group.setName(requestDto.getName());
        group.setDescription(requestDto.getDescription());
        group.setTopics(requestDto.getTopics());
        group.setMaxMembers(requestDto.getMaxMembers());

        Group updatedGroup = groupRepository.save(group);
        return mapToResponseDto(updatedGroup);
    }

    /**
     * Soft deletes a group by setting status to ARCHIVED.
     * Data is retained in DB for audit and history purposes.
     * Only the group creator is allowed to archive the group.
     * Throws GroupNotFoundException if group does not exist.
     */
    @Override
    public void deleteGroup(Long id, Long requestingUserId) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(id));

        // Only the creator can delete/archive the group
        if (!group.getCreatorUserId().equals(requestingUserId)) {
            throw new UnauthorizedActionException("delete this group");
        }

        group.setStatus(GroupStatus.ARCHIVED);
        groupRepository.save(group);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Membership
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Adds a user to a group as a new member.
     * Validates three things before joining:
     *   1. Group must exist
     *   2. Group status must be OPEN
     *   3. Group must not have reached maxMembers capacity
     *   4. User must not already be a member
     */
    @Override
    public GroupResponseDto joinGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        // Group must be OPEN to accept new members
        if (group.getStatus() != GroupStatus.OPEN) {
            throw new GroupNotOpenException(groupId);
        }

        // Group must not have exceeded capacity
        if (group.getMemberUserIds().size() >= group.getMaxMembers()) {
            throw new GroupFullException(groupId, group.getMaxMembers());
        }

        // User must not already be a member
        if (group.getMemberUserIds().contains(userId)) {
            throw new UserAlreadyMemberException(userId, groupId);
        }

        group.getMemberUserIds().add(userId);
        Group updatedGroup = groupRepository.save(group);
        return mapToResponseDto(updatedGroup);
    }

    /**
     * Removes a user from a group.
     * Creator cannot leave their own group — they must archive it instead.
     * Throws UserNotMemberException if user is not currently a member.
     * Throws GroupNotFoundException if group does not exist.
     */
    @Override
    public GroupResponseDto leaveGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        // User must be a current member to leave
        if (!group.getMemberUserIds().contains(userId)) {
            throw new UserNotMemberException(userId, groupId);
        }

        // Creator cannot leave — they own the group
        if (group.getCreatorUserId().equals(userId)) {
            throw new UnauthorizedActionException("leave a group you created. Archive the group instead.");
        }

        group.getMemberUserIds().remove(userId);
        Group updatedGroup = groupRepository.save(group);
        return mapToResponseDto(updatedGroup);
    }

    /**
     * Returns all groups that a specific user is a member of.
     * Useful for "My Groups" screen in the frontend.
     */
    @Override
    public Page<GroupResponseDto> getGroupsByMember(Long userId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return groupRepository.findByMemberUserIdsContaining(userId, pageable)
                .map(this::mapToResponseDto);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Filter & Search
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns paginated groups filtered by their current status.
     * e.g. OPEN groups for learners browsing, ARCHIVED for admin audit.
     */
    @Override
    public Page<GroupResponseDto> getGroupsByStatus(GroupStatus status, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return groupRepository.findByStatus(status, pageable)
                .map(this::mapToResponseDto);
    }

    /**
     * Returns paginated groups that contain a given topic tag.
     * Learners use this to discover groups relevant to a skill they want to learn.
     */
    @Override
    public Page<GroupResponseDto> getGroupsByTopic(String topic, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return groupRepository.findByTopicsContaining(topic, pageable)
                .map(this::mapToResponseDto);
    }

    /**
     * Returns all groups created by a specific user.
     * Useful for "Groups I Created" section in the user profile.
     */
    @Override
    public Page<GroupResponseDto> getGroupsByCreator(Long creatorUserId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return groupRepository.findByCreatorUserId(creatorUserId, pageable)
                .map(this::mapToResponseDto);
    }

    /**
     * Case-insensitive partial name search across all groups.
     * Extra feature — allows learners to search groups like "Spring", "DSA", "ML".
     */
    @Override
    public Page<GroupResponseDto> searchGroupsByName(String keyword, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return groupRepository.findByNameContainingIgnoreCase(keyword, pageable)
                .map(this::mapToResponseDto);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Admin Actions
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Allows group creator to change the group status.
     * e.g. OPEN → CLOSED to stop accepting new members,
     *      CLOSED → OPEN to re-open, or either → ARCHIVED to end the group.
     * Only the group creator can perform this action.
     */
    @Override
    public GroupResponseDto changeGroupStatus(Long id, Long requestingUserId, GroupStatus newStatus) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(id));

        // Only the creator can change group status
        if (!group.getCreatorUserId().equals(requestingUserId)) {
            throw new UnauthorizedActionException("change the status of this group");
        }

        group.setStatus(newStatus);
        Group updatedGroup = groupRepository.save(group);
        return mapToResponseDto(updatedGroup);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Discussions (Extra Feature)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Posts a discussion message inside a group.
     * Only members of the group are allowed to post.
     * Throws UserNotMemberException if author is not a member.
     * Throws GroupNotFoundException if group does not exist.
     */
    @Override
    public DiscussionResponseDto postDiscussion(Long groupId, DiscussionRequestDto requestDto) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        // Only group members can post in group discussions
        if (!group.getMemberUserIds().contains(requestDto.getAuthorUserId())) {
            throw new UserNotMemberException(requestDto.getAuthorUserId(), groupId);
        }

        GroupDiscussion discussion = new GroupDiscussion();
        discussion.setGroup(group);
        discussion.setAuthorUserId(requestDto.getAuthorUserId());
        discussion.setMessage(requestDto.getMessage());

        GroupDiscussion savedDiscussion = groupDiscussionRepository.save(discussion);
        return mapToDiscussionResponseDto(savedDiscussion);
    }

    /**
     * Returns paginated discussion posts for a specific group.
     * Sorted by postedAt descending by default (latest messages first).
     * Throws GroupNotFoundException if group does not exist.
     */
    @Override
    public Page<DiscussionResponseDto> getDiscussionsByGroup(Long groupId, int page, int size) {
        // Verify group exists before fetching discussions
        if (!groupRepository.existsById(groupId)) {
            throw new GroupNotFoundException(groupId);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "postedAt"));
        return groupDiscussionRepository.findByGroupId(groupId, pageable)
                .map(this::mapToDiscussionResponseDto);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private Helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Maps a Group entity to GroupResponseDto.
     * Sets memberCount as a derived field from the size of memberUserIds list.
     * Kept private — only used internally within the service layer.
     */
    private GroupResponseDto mapToResponseDto(Group group) {
        GroupResponseDto dto = modelMapper.map(group, GroupResponseDto.class);
        // Derived field — saves client the trouble of computing list size
        dto.setMemberCount(group.getMemberUserIds() != null ? group.getMemberUserIds().size() : 0);
        return dto;
    }

    /**
     * Maps a GroupDiscussion entity to DiscussionResponseDto.
     * Extracts groupId from the nested Group object for a flat response structure.
     */
    private DiscussionResponseDto mapToDiscussionResponseDto(GroupDiscussion discussion) {
        return DiscussionResponseDto.builder()
                .id(discussion.getId())
                .groupId(discussion.getGroup().getId())
                .authorUserId(discussion.getAuthorUserId())
                .message(discussion.getMessage())
                .postedAt(discussion.getPostedAt())
                .build();
    }

}
