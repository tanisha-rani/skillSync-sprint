package com.skillsync.mentor.exception;

public class MentorAlreadyExistsException extends RuntimeException{
    public MentorAlreadyExistsException(Long id) {
        super("Mentor Already Exist with id : "+id);
    }
}
