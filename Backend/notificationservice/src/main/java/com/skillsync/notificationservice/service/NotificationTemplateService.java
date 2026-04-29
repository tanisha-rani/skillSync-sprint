package com.skillsync.notificationservice.service;

import com.skillsync.notificationservice.entity.NotificationType;
import org.springframework.stereotype.Service;

/**
 * Generates human-readable email subjects and message bodies
 * based on the notification type and a reference ID.
 */
@Service
public class NotificationTemplateService {

    public String buildSubject(NotificationType type, Long referenceId) {
        return switch (type) {
            case SESSION_BOOKED    -> "SkillSync: Session Booked (ID: " + referenceId + ")";
            case SESSION_ACCEPTED  -> "SkillSync: Your Session Has Been Accepted!";
            case SESSION_REJECTED  -> "SkillSync: Session Request Rejected";
            case SESSION_CANCELLED -> "SkillSync: Session Cancelled (ID: " + referenceId + ")";
            case SESSION_COMPLETED -> "SkillSync: Session Completed — Leave a Review!";
            case SESSION_REMINDER  -> "SkillSync: Upcoming Session Reminder";
            case REVIEW_RECEIVED   -> "SkillSync: You Received a New Review!";
            case GROUP_JOINED      -> "SkillSync: Welcome to the Group!";
            case GROUP_INVITATION  -> "SkillSync: You're Invited to Join a Group";
            case WELCOME           -> "Welcome to SkillSync!";
            case ACCOUNT_UPDATE    -> "SkillSync: Account Updated";
        };
    }

    public String buildMessage(NotificationType type, Long referenceId) {
        return switch (type) {
            case SESSION_BOOKED ->
                    "Hello,\n\nA new session has been booked (Session ID: " + referenceId + ").\n" +
                            "Please log in to SkillSync to accept or reject the request.\n\nThanks,\nSkillSync Team";

            case SESSION_ACCEPTED ->
                    "Hello,\n\nGreat news! Your session request (Session ID: " + referenceId + ") has been accepted by the mentor.\n" +
                            "Please prepare for your upcoming session.\n\nThanks,\nSkillSync Team";

            case SESSION_REJECTED ->
                    "Hello,\n\nUnfortunately, your session request (Session ID: " + referenceId + ") was not accepted by the mentor.\n" +
                            "You can search for other available mentors on SkillSync.\n\nThanks,\nSkillSync Team";

            case SESSION_CANCELLED ->
                    "Hello,\n\nSession (ID: " + referenceId + ") has been cancelled.\n" +
                            "If you have questions, please contact support.\n\nThanks,\nSkillSync Team";

            case SESSION_COMPLETED ->
                    "Hello,\n\nYour session (ID: " + referenceId + ") is now marked as completed.\n" +
                            "We'd love to hear your feedback! Please leave a review for your mentor on SkillSync.\n\nThanks,\nSkillSync Team";

            case SESSION_REMINDER ->
                    "Hello,\n\nThis is a reminder that your session (ID: " + referenceId + ") is coming up soon.\n" +
                            "Please be ready and log in on time.\n\nThanks,\nSkillSync Team";

            case REVIEW_RECEIVED ->
                    "Hello,\n\nYou have received a new review on SkillSync!\n" +
                            "Log in to your mentor profile to see what your learner said.\n\nThanks,\nSkillSync Team";

            case GROUP_JOINED ->
                    "Hello,\n\nYou have successfully joined a learning group (Group ID: " + referenceId + ") on SkillSync.\n" +
                            "Start collaborating with your group members today!\n\nThanks,\nSkillSync Team";

            case GROUP_INVITATION ->
                    "Hello,\n\nYou have been invited to join a learning group (Group ID: " + referenceId + ") on SkillSync.\n" +
                            "Log in to accept the invitation.\n\nThanks,\nSkillSync Team";

            case WELCOME ->
                    "Hello,\n\nWelcome to SkillSync — your platform for finding mentors and growing your skills!\n" +
                            "Complete your profile to get started.\n\nThanks,\nSkillSync Team";

            case ACCOUNT_UPDATE ->
                    "Hello,\n\nYour SkillSync account details have been updated.\n" +
                            "If you did not make this change, please contact support immediately.\n\nThanks,\nSkillSync Team";
        };
    }
}