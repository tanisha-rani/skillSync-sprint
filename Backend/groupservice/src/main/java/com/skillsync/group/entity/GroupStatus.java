package com.skillsync.group.entity;

/**
 * Represents the lifecycle status of a learning group.
 * OPEN     — accepting new members
 * CLOSED   — no new members allowed (but group is active)
 * ARCHIVED — group is inactive / ended
 */
public enum GroupStatus {
    OPEN,
    CLOSED,
    ARCHIVED
}
