package com.skillsync.notificationservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EmailServiceTest {

    @Test
    void sendEmail_setsConfiguredFromAddress() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        EmailService emailService = new EmailService(mailSender);
        ReflectionTestUtils.setField(emailService, "fromAddress", "no-reply@skillsync.local");

        emailService.sendEmail("user@example.com", "Subject", "Body");

        org.mockito.ArgumentCaptor<SimpleMailMessage> captor = forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        assertEquals("no-reply@skillsync.local", captor.getValue().getFrom());
    }
}
