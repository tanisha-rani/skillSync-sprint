package com.skillsync.skillService.service.impl;

import com.skillsync.skillService.dto.SkillRequestDto;
import com.skillsync.skillService.dto.SkillResponseDto;
import com.skillsync.skillService.entity.Skills;
import com.skillsync.skillService.exception.SkillAlreadyExistsException;
import com.skillsync.skillService.exception.SkillNotFoundException;
import com.skillsync.skillService.repository.SkillRepository;
import com.skillsync.skillService.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of SkillService interface.
 * Handles all business logic for skill management operations.
 */
@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final ModelMapper modelMapper;
    private final SkillRepository skillRepository;

    /**
     * Creates a new skill after checking for duplicate name.
     * Throws SkillAlreadyExistsException if skill name already exists.
     */
    @Override
    public SkillResponseDto createSkill(SkillRequestDto requestDto) {
        if (skillRepository.existsByName(requestDto.getName())) {
            throw new SkillAlreadyExistsException(requestDto.getName());
        }
        Skills skill = modelMapper.map(requestDto, Skills.class);
        Skills savedSkill = skillRepository.save(skill);
        return modelMapper.map(savedSkill, SkillResponseDto.class);
    }

    /**
     * Fetches a single skill by its unique ID.
     * Throws SkillNotFoundException if skill does not exist.
     */
    @Override
    public SkillResponseDto getSkillById(Long id) {
        Skills skill = skillRepository.findById(id)
                .orElseThrow(() -> new SkillNotFoundException(id));
        return modelMapper.map(skill, SkillResponseDto.class);
    }

    /**
     * Returns paginated list of all skills.
     * Supports dynamic sorting by any skill field.
     */
    @Override
    public Page<SkillResponseDto> getAllSkills(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return skillRepository.findAll(pageable)
                .map(skill -> modelMapper.map(skill, SkillResponseDto.class));
    }

    /**
     * Searches skills by name containing keyword (case insensitive).
     * Useful for learners to find skills by partial name.
     */
    @Override
    public List<SkillResponseDto> searchSkillsByName(String name) {
        return skillRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(skill -> modelMapper.map(skill, SkillResponseDto.class))
                .toList();
    }

    /**
     * Permanently deletes a skill by ID.
     * Throws SkillNotFoundException if skill does not exist.
     */
    @Override
    public void deleteSkill(Long id) {
        Skills skill = skillRepository.findById(id)
                .orElseThrow(() -> new SkillNotFoundException(id));
        skillRepository.delete(skill);
    }
    @Override
    public SkillResponseDto updateSkill(Long id, SkillRequestDto requestDto) {
        Skills skill = skillRepository.findById(id)
                .orElseThrow(() -> new SkillNotFoundException(id));
        skill.setName(requestDto.getName());
        skill.setCategory(requestDto.getCategory());
        skill.setDescription(requestDto.getDescription());
        Skills updatedSkill = skillRepository.save(skill);
        return modelMapper.map(updatedSkill, SkillResponseDto.class);
    }
}