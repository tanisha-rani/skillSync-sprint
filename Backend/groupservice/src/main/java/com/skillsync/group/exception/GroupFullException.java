package com.skillsync.group.exception;

public class GroupFullException extends RuntimeException {
    public GroupFullException(Long groupId, int maxMembers) {
        super("Group " + groupId + " is full. Maximum allowed members: " + maxMembers);
    }
}
