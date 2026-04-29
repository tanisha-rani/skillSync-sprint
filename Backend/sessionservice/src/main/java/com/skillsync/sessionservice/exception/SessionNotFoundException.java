package com.skillsync.sessionservice.exception;

public class SessionNotFoundException extends RuntimeException{
    public SessionNotFoundException(Long id) {
        super("Session not found by id : "+id);
    }
}
