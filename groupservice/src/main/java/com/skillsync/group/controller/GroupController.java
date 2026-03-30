package com.skillsync.group.controller;

import com.skillsync.group.dto.*;
import com.skillsync.group.entity.GroupStatus;
import com.skillsync.group.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Group Service.
 * Exposes all peer learning group endpoints.
 * Thin controller — delegates all business logic to GroupService.
 */
@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
@Tag(name = "Group Service", description = "APIs for managing peer learning groups")
public class GroupController {

    private final GroupService groupService;

    // ─────────────────────────────────────────────────────────────────────────
    // Core CRUD
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Creates a new peer learning group.
     * Creator is automatically enrolled as first member.
     * Returns 201 CREATED with the created group details.
     */
    @Operation(summary = "Create a new peer learning group")
    @PostMapping
    public ResponseEntity<GroupResponseDto> createGroup(@Valid @RequestBody GroupRequestDto groupRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.createGroup(groupRequestDto));
    }

    /**
     * Fetches a single group by its unique ID.
     * Returns 200 OK with group details, or 404 if not found.
     */
    @Operation(summary = "Get group by ID")
    @GetMapping("/{id}")
    public ResponseEntity<GroupResponseDto> getGroupById(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

    /**
     * Returns paginated list of all groups.
     * Supports custom page, size and sorting parameters.
     */
    @Operation(summary = "Get all groups with pagination")
    @GetMapping
    public ResponseEntity<Page<GroupResponseDto>> getAllGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(groupService.getAllGroups(page, size, sortBy));
    }

    /**
     * Updates an existing group's details.
     * Only the group creator (identified by requestingUserId) can update.
     * Returns 200 OK with updated group details.
     */
    @Operation(summary = "Update group details by ID (creator only)")
    @PutMapping("/{id}")
    public ResponseEntity<GroupResponseDto> updateGroup(
            @PathVariable Long id,
            @RequestParam Long requestingUserId,
            @Valid @RequestBody GroupRequestDto groupRequestDto) {
        return ResponseEntity.ok(groupService.updateGroup(id, requestingUserId, groupRequestDto));
    }

    /**
     * Soft deletes a group by archiving it (status = ARCHIVED).
     * Only the group creator can delete the group.
     * Returns 204 NO CONTENT on success.
     */
    @Operation(summary = "Soft delete (archive) a group by ID (creator only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable Long id,
            @RequestParam Long requestingUserId) {
        groupService.deleteGroup(id, requestingUserId);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Membership
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Adds a user to an existing group.
     * Validates: group is OPEN, not full, user not already a member.
     * Returns 200 OK with updated group details including new member.
     */
    @Operation(summary = "Join a group")
    @PostMapping("/{id}/join")
    public ResponseEntity<GroupResponseDto> joinGroup(
            @PathVariable Long id,
            @RequestParam Long userId) {
        return ResponseEntity.ok(groupService.joinGroup(id, userId));
    }

    /**
     * Removes a user from a group.
     * Group creator cannot leave their own group — they must archive it.
     * Returns 200 OK with updated group details.
     */
    @Operation(summary = "Leave a group")
    @PostMapping("/{id}/leave")
    public ResponseEntity<GroupResponseDto> leaveGroup(
            @PathVariable Long id,
            @RequestParam Long userId) {
        return ResponseEntity.ok(groupService.leaveGroup(id, userId));
    }

    /**
     * Returns all groups that a specific user has joined.
     * Used for "My Groups" screen in the learner dashboard.
     */
    @Operation(summary = "Get all groups a user is a member of")
    @GetMapping("/my-groups/{userId}")
    public ResponseEntity<Page<GroupResponseDto>> getMyGroups(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(groupService.getGroupsByMember(userId, page, size, sortBy));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Filter & Search
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns paginated groups filtered by their current status.
     * e.g. GET /groups/status/OPEN to get all open groups.
     */
    @Operation(summary = "Get groups by status (OPEN / CLOSED / ARCHIVED) with pagination")
    @GetMapping("/status/{groupStatus}")
    public ResponseEntity<Page<GroupResponseDto>> getGroupsByStatus(
            @PathVariable GroupStatus groupStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(groupService.getGroupsByStatus(groupStatus, page, size, sortBy));
    }

    /**
     * Returns paginated groups containing a specific topic tag.
     * e.g. GET /groups/topic/Java to find all Java-related groups.
     */
    @Operation(summary = "Get groups by topic tag with pagination")
    @GetMapping("/topic/{topic}")
    public ResponseEntity<Page<GroupResponseDto>> getGroupsByTopic(
            @PathVariable String topic,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(groupService.getGroupsByTopic(topic, page, size, sortBy));
    }

    /**
     * Returns all groups created by a specific user.
     * Used for "Groups I Created" section in the profile page.
     */
    @Operation(summary = "Get all groups created by a specific user")
    @GetMapping("/creator/{creatorUserId}")
    public ResponseEntity<Page<GroupResponseDto>> getGroupsByCreator(
            @PathVariable Long creatorUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(groupService.getGroupsByCreator(creatorUserId, page, size, sortBy));
    }

    /**
     * Case-insensitive keyword search by group name.
     * Extra feature — e.g. GET /groups/search?keyword=spring finds
     * "Spring Boot Beginners", "Spring Security Deep Dive", etc.
     */
    @Operation(summary = "Search groups by name keyword (case-insensitive)")
    @GetMapping("/search")
    public ResponseEntity<Page<GroupResponseDto>> searchGroupsByName(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(groupService.searchGroupsByName(keyword, page, size, sortBy));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Admin Actions
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Allows group creator to change the status of their group.
     * e.g. OPEN → CLOSED (stop new joins), CLOSED → OPEN (re-open),
     * or any → ARCHIVED (end the group permanently).
     */
    @Operation(summary = "Change group status (creator only) — OPEN / CLOSED / ARCHIVED")
    @PutMapping("/{id}/status")
    public ResponseEntity<GroupResponseDto> changeGroupStatus(
            @PathVariable Long id,
            @RequestParam Long requestingUserId,
            @RequestParam GroupStatus newStatus) {
        return ResponseEntity.ok(groupService.changeGroupStatus(id, requestingUserId, newStatus));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Discussions (Extra Feature)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Posts a new discussion message in a group.
     * Only members of the group can post.
     * Returns 201 CREATED with the saved discussion post.
     */
    @Operation(summary = "Post a discussion message in a group (members only)")
    @PostMapping("/{id}/discussions")
    public ResponseEntity<DiscussionResponseDto> postDiscussion(
            @PathVariable Long id,
            @Valid @RequestBody DiscussionRequestDto discussionRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(groupService.postDiscussion(id, discussionRequestDto));
    }

    /**
     * Returns paginated discussion posts for a given group.
     * Latest messages appear first (sorted by postedAt DESC).
     * Throws 404 if group does not exist.
     */
    @Operation(summary = "Get all discussion posts for a group (latest first)")
    @GetMapping("/{id}/discussions")
    public ResponseEntity<Page<DiscussionResponseDto>> getDiscussionsByGroup(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(groupService.getDiscussionsByGroup(id, page, size));
    }

}
