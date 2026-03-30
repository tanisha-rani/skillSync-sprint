package com.skillsync.group.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

/**
 * Request DTO for Group create and update operations.
 * Separates API contract from internal entity structure.
 * Validation annotations ensure data integrity before reaching service layer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupRequestDto {

    /**
     * Display name of the group.
     * Mandatory — must be between 3 and 100 characters.
     */
    @NotBlank(message = "Group name is required")
    @Size(min = 3, max = 100, message = "Group name must be between 3 and 100 characters")
    private String name;

    /**
     * Short description of what the group focuses on.
     * Optional — can be added or updated later.
     */
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    /**
     * Topics/tags associated with this group.
     * At least one topic is required for discoverability.
     */
    @NotEmpty(message = "At least one topic is required")
    private List<String> topics;

    /**
     * UserId of the person creating the group.
     * This user becomes the group admin/creator.
     */
    @NotNull(message = "Creator userId is required")
    private Long creatorUserId;

    /**
     * Maximum number of members the group can hold.
     * Must be between 2 and 500.
     * Default value handled in service layer if not provided.
     */
    @Min(value = 2, message = "Group must allow at least 2 members")
    @Max(value = 500, message = "Group cannot have more than 500 members")
    private Integer maxMembers ;

}
