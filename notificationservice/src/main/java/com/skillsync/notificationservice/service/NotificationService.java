package com.skillsync.notificationservice.service;

import com.skillsync.notificationservice.dto.NotificationRequestDto;
import com.skillsync.notificationservice.dto.NotificationResponseDto;
import com.skillsync.notificationservice.entity.Notification;
import com.skillsync.notificationservice.entity.NotificationStatus;
import com.skillsync.notificationservice.exception.NotificationNotFoundException;
import com.skillsync.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final NotificationTemplateService templateService;
    private final ModelMapper modelMapper;

    // ✅ PROCESSING METHOD (called by consumer)
    public NotificationResponseDto sendNotification(NotificationRequestDto requestDto) {

        String subject = requestDto.getSubject() != null
                ? requestDto.getSubject()
                : templateService.buildSubject(requestDto.getType(), requestDto.getReferenceId());

        String message = requestDto.getMessage() != null
                ? requestDto.getMessage()
                : templateService.buildMessage(requestDto.getType(), requestDto.getReferenceId());

        Notification notification = Notification.builder()
                .userId(requestDto.getUserId())
                .recipientEmail(requestDto.getRecipientEmail())
                .type(requestDto.getType())
                .subject(subject)
                .message(message)
                .referenceId(requestDto.getReferenceId())
                .referenceType(requestDto.getReferenceType())
                .build();

        notification = notificationRepository.save(notification);

        try {
            emailService.sendEmail(
                    notification.getRecipientEmail(),
                    notification.getSubject(),
                    notification.getMessage()
            );
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());

        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            log.error("Notification FAILED: {}", e.getMessage());
        }

        notification = notificationRepository.save(notification);
        return mapToResponse(notification);
    }

    // ---------------- ALL YOUR METHODS ----------------

    public List<NotificationResponseDto> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<NotificationResponseDto> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public NotificationResponseDto markAsRead(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Not found"));
        n.setRead(true);
        return mapToResponse(notificationRepository.save(n));
    }

    public void markAllAsRead(Long userId) {
        List<Notification> list = notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        list.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(list);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    public NotificationResponseDto getById(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Not found"));
        return mapToResponse(n);
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    private NotificationResponseDto mapToResponse(Notification n) {
        return modelMapper.map(n, NotificationResponseDto.class);
    }
}