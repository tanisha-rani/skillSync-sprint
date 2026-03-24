package com.skillsync.mentor.exception;

public class MentorNotFoundException extends RuntimeException {
    public MentorNotFoundException(Long id) {
        super("Mentor not found with id: " + id);
    }
}
