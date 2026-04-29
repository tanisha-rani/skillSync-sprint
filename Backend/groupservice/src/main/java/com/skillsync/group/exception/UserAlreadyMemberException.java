package com.skillsync.group.exception;

public class UserAlreadyMemberException extends RuntimeException {
    public UserAlreadyMemberException(Long userId, Long groupId) {
        super("User with id " + userId + " is already a member of group " + groupId);
    }
}
