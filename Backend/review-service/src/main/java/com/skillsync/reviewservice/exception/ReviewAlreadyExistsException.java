package com.skillsync.reviewservice.exception;

/**
 * Thrown when learner tries to submit duplicate review for same session.
 * Caught by GlobalExceptionHandler and returned as 409 response.
 */
public class ReviewAlreadyExistsException extends RuntimeException {
    public ReviewAlreadyExistsException(Long sessionId) {
        super("Review already submitted for session id: " + sessionId);
    }
}
