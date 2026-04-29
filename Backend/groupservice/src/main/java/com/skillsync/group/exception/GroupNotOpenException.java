package com.skillsync.group.exception;

public class GroupNotOpenException extends RuntimeException {
    public GroupNotOpenException(Long groupId) {
        super("Group " + groupId + " is not open for new members");
    }
}
