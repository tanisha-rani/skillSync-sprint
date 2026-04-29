package com.skillsync.group.exception;

public class UserNotMemberException extends RuntimeException {
    public UserNotMemberException(Long userId, Long groupId) {
        super("User with id " + userId + " is not a member of group " + groupId);
    }
}
