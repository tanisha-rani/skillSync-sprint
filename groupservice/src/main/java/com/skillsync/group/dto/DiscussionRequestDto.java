package com.skillsync.group.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Request DTO for posting a discussion message in a group.
 * Extra feature — enables async group communication.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscussionRequestDto {

    /**
     * UserId of the member posting the message.
     * Must be a current member of the group.
     */
    @NotNull(message = "Author userId is required")
    private Long authorUserId;

    /**
     * Message content to post in the group discussion.
     * Cannot be blank and has a 2000 character limit.
     */
    @NotBlank(message = "Message cannot be blank")
    @Size(max = 2000, message = "Message must not exceed 2000 characters")
    private String message;

}
