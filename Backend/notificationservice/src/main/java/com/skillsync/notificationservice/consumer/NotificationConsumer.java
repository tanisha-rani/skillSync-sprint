package com.skillsync.notificationservice.consumer;

import com.skillsync.notificationservice.dto.NotificationRequestDto;
import com.skillsync.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = "notificationQueue")
    public void receive(NotificationRequestDto requestDto) {

        System.out.println("Received from queue: " + requestDto);

        notificationService.sendNotification(requestDto);
    }
}
