package com.skillsync.reviewservice.exception;

/**
 * Thrown when a review with given ID does not exist.
 * Caught by GlobalExceptionHandler and returned as 404 response.
 */
public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(Long id) {
        super("Review not found with id: " + id);
    }
}
