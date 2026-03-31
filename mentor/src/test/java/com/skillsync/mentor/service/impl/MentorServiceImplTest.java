package com.skillsync.mentor.service.impl;

import com.skillsync.mentor.dto.MentorRequestDto;
import com.skillsync.mentor.entity.Mentor;
import com.skillsync.mentor.entity.MentorStatus;
import com.skillsync.mentor.exception.MentorAlreadyExistsException;
import com.skillsync.mentor.repository.MentorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorServiceImplTest {

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MentorServiceImpl mentorService;

    @Test
    void applyAsMentor_whenExists_throwsException() {
        MentorRequestDto request = new MentorRequestDto("Bio", 2, List.of("Java"), 100.0, 10L);
        when(mentorRepository.existsByUserId(10L)).thenReturn(true);

        assertThrows(MentorAlreadyExistsException.class, () -> mentorService.applyAsMentor(request));
    }

    @Test
    void applyAsMentor_setsDefaults() {
        MentorRequestDto request = new MentorRequestDto("Bio", 2, List.of("Java"), 100.0, 10L);
        Mentor mapped = new Mentor();
        when(mentorRepository.existsByUserId(10L)).thenReturn(false);
        when(modelMapper.map(request, Mentor.class)).thenReturn(mapped);
        when(mentorRepository.save(any(Mentor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mentorService.applyAsMentor(request);

        ArgumentCaptor<Mentor> captor = ArgumentCaptor.forClass(Mentor.class);
        org.mockito.Mockito.verify(mentorRepository).save(captor.capture());
        assertEquals(MentorStatus.PENDING, captor.getValue().getStatus());
        assertEquals(false, captor.getValue().isAvailable());
        assertEquals(10L, captor.getValue().getUserId());
    }
}
