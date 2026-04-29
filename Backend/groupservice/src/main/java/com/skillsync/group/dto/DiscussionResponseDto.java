package com.skillsync.group.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO for GroupDiscussion operations.
 * Returned to client — never exposes entity or group details directly.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussionResponseDto {

    private Long id;
    private Long groupId;
    private Long authorUserId;
    private String message;
    private LocalDateTime postedAt;

}
