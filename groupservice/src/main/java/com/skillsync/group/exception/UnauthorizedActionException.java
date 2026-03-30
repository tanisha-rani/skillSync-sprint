package com.skillsync.group.exception;

public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException(String action) {
        super("Unauthorized: only the group creator can " + action);
    }
}
