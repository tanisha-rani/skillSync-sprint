package com.skillsync.notificationservice.repository;

import com.skillsync.notificationservice.entity.Notification;
import com.skillsync.notificationservice.entity.NotificationStatus;
import com.skillsync.notificationservice.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Get all notifications for a specific user (in-app feed)
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Get unread notifications for a user
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    // Get notifications by status (e.g., all PENDING for retry)
    List<Notification> findByStatus(NotificationStatus status);

    // Get notifications by type for a user
    List<Notification> findByUserIdAndType(Long userId, NotificationType type);

    // Count unread notifications for a user
    long countByUserIdAndIsReadFalse(Long userId);
}