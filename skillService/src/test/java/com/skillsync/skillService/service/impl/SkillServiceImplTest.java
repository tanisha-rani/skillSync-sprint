package com.skillsync.skillService.service.impl;

import com.skillsync.skillService.dto.SkillRequestDto;
import com.skillsync.skillService.entity.Skills;
import com.skillsync.skillService.exception.SkillAlreadyExistsException;
import com.skillsync.skillService.repository.SkillRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillServiceImpl skillService;

    @Test
    void createSkill_whenExists_throwsException() {
        SkillRequestDto request = new SkillRequestDto("Java", "Backend", "Desc");
        when(skillRepository.existsByName("Java")).thenReturn(true);

        assertThrows(SkillAlreadyExistsException.class, () -> skillService.createSkill(request));
    }

    @Test
    void updateSkill_updatesFields() {
        Skills skill = new Skills();
        skill.setId(1L);
        skill.setName("Old");
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(skillRepository.save(any(Skills.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(Skills.class), any(Class.class))).thenReturn(new com.skillsync.skillService.dto.SkillResponseDto());

        skillService.updateSkill(1L, new SkillRequestDto("New", "Cat", "Desc"));

        assertEquals("New", skill.getName());
        assertEquals("Cat", skill.getCategory());
    }
}
