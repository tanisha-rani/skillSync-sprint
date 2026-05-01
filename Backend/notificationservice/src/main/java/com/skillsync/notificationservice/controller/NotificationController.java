package com.skillsync.notificationservice.controller;

import com.skillsync.notificationservice.config.RabbitMQConfig;
import com.skillsync.notificationservice.dto.NotificationRequestDto;
import com.skillsync.notificationservice.dto.NotificationResponseDto;
import com.skillsync.notificationservice.entity.NotificationStatus;
import com.skillsync.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final RabbitTemplate rabbitTemplate;
    private final NotificationService notificationService;

    // ✅ SEND TO RABBITMQ
    @PostMapping
    public ResponseEntity<?> sendNotification(
            @RequestBody NotificationRequestDto requestDto) {

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY,
                    requestDto
            );
        } catch (AmqpException e) {
            NotificationResponseDto response = notificationService.sendNotification(requestDto);
            if (response.getStatus() == NotificationStatus.FAILED) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
            }
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok("Message sent to queue");
    }

    @PostMapping("/send-now")
    public ResponseEntity<NotificationResponseDto> sendNotificationNow(
            @RequestBody NotificationRequestDto requestDto) {

        NotificationResponseDto response = notificationService.sendNotification(requestDto);
        HttpStatus status = response.getStatus() == NotificationStatus.FAILED
                ? HttpStatus.BAD_GATEWAY
                : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    // ---------------- NORMAL METHODS ----------------

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDto>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponseDto>> getUnread(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponseDto> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @PatchMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
