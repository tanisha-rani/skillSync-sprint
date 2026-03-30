package com.skillsync.notificationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.notificationservice.dto.NotificationRequestDto;
import com.skillsync.notificationservice.entity.NotificationType;
import com.skillsync.notificationservice.entity.ReferenceType;
import com.skillsync.notificationservice.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private NotificationService notificationService;

    @Test
    void sendNotification_sendsToQueue() throws Exception {
        NotificationRequestDto request = NotificationRequestDto.builder()
                .userId(1L)
                .recipientEmail("user@example.com")
                .type(NotificationType.WELCOME)
                .referenceId(10L)
                .referenceType(ReferenceType.USER)
                .build();

        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(rabbitTemplate).convertAndSend(eq("notificationQueue"), any(NotificationRequestDto.class));
    }

    @Test
    void getUnreadCount_returnsCount() throws Exception {
        when(notificationService.getUnreadCount(1L)).thenReturn(3L);

        mockMvc.perform(get("/notifications/user/1/unread-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount").value(3));
    }
}
