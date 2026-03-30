package com.skillsync.notificationservice.service;

import com.skillsync.notificationservice.dto.NotificationRequestDto;
import com.skillsync.notificationservice.dto.NotificationResponseDto;
import com.skillsync.notificationservice.entity.Notification;
import com.skillsync.notificationservice.entity.NotificationStatus;
import com.skillsync.notificationservice.entity.NotificationType;
import com.skillsync.notificationservice.entity.ReferenceType;
import com.skillsync.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationTemplateService templateService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void sendNotification_setsSentStatus() {
        NotificationRequestDto request = NotificationRequestDto.builder()
                .userId(1L)
                .recipientEmail("user@example.com")
                .type(NotificationType.WELCOME)
                .referenceId(10L)
                .referenceType(ReferenceType.USER)
                .build();

        when(templateService.buildSubject(NotificationType.WELCOME, 10L)).thenReturn("Subject");
        when(templateService.buildMessage(NotificationType.WELCOME, 10L)).thenReturn("Message");
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(Notification.class), any(Class.class))).thenReturn(new NotificationResponseDto());

        notificationService.sendNotification(request);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, org.mockito.Mockito.times(2)).save(captor.capture());
        Notification saved = captor.getAllValues().get(1);
        assertEquals(NotificationStatus.SENT, saved.getStatus());
    }

    @Test
    void markAsRead_updatesEntity() {
        Notification notification = new Notification();
        notification.setRead(false);
        when(notificationRepository.findById(2L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(Notification.class), any(Class.class))).thenReturn(new NotificationResponseDto());

        notificationService.markAsRead(2L);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertEquals(true, captor.getValue().isRead());
    }
}
