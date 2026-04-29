package com.skillsync.skillService.service;

import com.skillsync.skillService.dto.SkillRequestDto;
import com.skillsync.skillService.dto.SkillResponseDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service contract for Skill business logic.
 * Programming to interface — implementation can be swapped without affecting controller.
 */
public interface SkillService {

    // Create a new skill — admin only
    SkillResponseDto createSkill(SkillRequestDto requestDto);

    // Get skill by id
    SkillResponseDto getSkillById(Long id);

    // Get all skills with pagination
    Page<SkillResponseDto> getAllSkills(int page, int size, String sortBy);

    // Search skills by name
    List<SkillResponseDto> searchSkillsByName(String name);

    // Delete skill by id
    void deleteSkill(Long id);

    SkillResponseDto updateSkill(Long id, @Valid SkillRequestDto requestDto);
}