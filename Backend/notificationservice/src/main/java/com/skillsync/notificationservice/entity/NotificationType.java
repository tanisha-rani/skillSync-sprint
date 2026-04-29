package com.skillsync.notificationservice.entity;

public enum NotificationType {
    // Session-related
    SESSION_BOOKED,
    SESSION_ACCEPTED,
    SESSION_REJECTED,
    SESSION_CANCELLED,
    SESSION_COMPLETED,
    SESSION_REMINDER,

    // Review-related
    REVIEW_RECEIVED,

    // Group-related
    GROUP_JOINED,
    GROUP_INVITATION,

    // General
    WELCOME,
    ACCOUNT_UPDATE
}