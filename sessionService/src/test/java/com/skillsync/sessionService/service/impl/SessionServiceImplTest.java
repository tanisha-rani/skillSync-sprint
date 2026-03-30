package com.skillsync.sessionservice.service.impl;

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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private SessionServiceImpl sessionService;

    @Test
    void bookSession_setsRequestedStatus() {
        SessionRequestDto request = new SessionRequestDto(1L, 2L, LocalDate.now(), 60, "Java");
        Session mapped = new Session();
        when(modelMapper.map(request, Session.class)).thenReturn(mapped);
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(Session.class), any(Class.class))).thenReturn(new com.skillsync.sessionservice.dto.SessionResponseDto());

        sessionService.bookSession(request);

        ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
        org.mockito.Mockito.verify(sessionRepository).save(captor.capture());
        assertEquals(SessionStatus.REQUESTED, captor.getValue().getStatus());
    }

    @Test
    void acceptSession_whenNotRequested_throwsException() {
        Session session = new Session();
        session.setStatus(SessionStatus.CANCELLED);
        when(sessionRepository.findById(5L)).thenReturn(Optional.of(session));

        assertThrows(IllegalStateException.class, () -> sessionService.acceptSession(5L));
    }
}
