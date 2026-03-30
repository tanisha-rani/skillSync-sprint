package com.skillsync.reviewservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Standard error response structure returned for all exceptions.
 * Ensures consistent error format across all endpoints.
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String message;
    private int status;
    private String path;
}
