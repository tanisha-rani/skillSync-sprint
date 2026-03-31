package com.skillsync.sessionservice.service.impl;

import com.skillsync.sessionservice.client.MentorDto;
import com.skillsync.sessionservice.client.MentorFeignClient;
import com.skillsync.sessionservice.client.NotificationFeignClient;
import com.skillsync.sessionservice.dto.SessionRequestDto;
import com.skillsync.sessionservice.entity.Session;
import com.skillsync.sessionservice.entity.SessionStatus;
import com.skillsync.sessionservice.repository.SessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private NotificationFeignClient notificationFeignClient;

    @Mock
    private MentorFeignClient mentorFeignClient;

    @InjectMocks
    private SessionServiceImpl sessionService;

    @Test
    void bookSession_setsRequestedStatus() {
        SessionRequestDto request = new SessionRequestDto(1L, 2L, LocalDate.now(), 60, "Java");
        Session mapped = new Session();
        MentorDto mentor = new MentorDto();
        mentor.setAvailable(true);
        mentor.setUserId(99L);

        when(mentorFeignClient.getMentorById(2L)).thenReturn(mentor);
        when(modelMapper.map(request, Session.class)).thenReturn(mapped);
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(Session.class), any(Class.class))).thenReturn(new com.skillsync.sessionservice.dto.SessionResponseDto());
        doNothing().when(notificationFeignClient).sendNotification(any());

        sessionService.bookSession(request);

        ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
        org.mockito.Mockito.verify(sessionRepository).save(captor.capture());
        assertEquals(SessionStatus.REQUESTED, captor.getValue().getStatus());
    }

    @Test
    void bookSession_whenMentorNotApproved_throwsHelpfulException() {
        SessionRequestDto request = new SessionRequestDto(1L, 2L, LocalDate.now(), 60, "Java");
        MentorDto mentor = new MentorDto();
        mentor.setAvailable(false);
        mentor.setStatus("PENDING");
        when(mentorFeignClient.getMentorById(2L)).thenReturn(mentor);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> sessionService.bookSession(request));

        assertEquals("Mentor 2 cannot be booked because they are not approved yet (status: PENDING)", ex.getMessage());
    }

    @Test
    void acceptSession_whenNotRequested_throwsException() {
        Session session = new Session();
        session.setStatus(SessionStatus.CANCELLED);
        when(sessionRepository.findById(5L)).thenReturn(Optional.of(session));

        assertThrows(IllegalStateException.class, () -> sessionService.acceptSession(5L));
    }
}
