package com.skillsync.skillService.controller;

import com.skillsync.skillService.dto.SkillRequestDto;
import com.skillsync.skillService.dto.SkillResponseDto;
import com.skillsync.skillService.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/skills")
@Tag(name = "Skill Service", description = "APIs for managing skills")
public class SkillController {

    private final SkillService skillService;

    @Operation(summary = "Create a new skill")
    @PostMapping
    public ResponseEntity<SkillResponseDto> createSkill(
            @Valid @RequestBody SkillRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(skillService.createSkill(requestDto));
    }

    @Operation(summary = "Get skill by Id")
    @GetMapping("/{id}")
    public ResponseEntity<SkillResponseDto> getSkillById(@PathVariable Long id) {
        return ResponseEntity.ok(skillService.getSkillById(id));
    }

    @Operation(summary = "Get all skills with pagination")
    @GetMapping
    public ResponseEntity<Page<SkillResponseDto>> getAllSkills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(skillService.getAllSkills(page, size, sortBy));
    }

    @Operation(summary = "Search skills by name")
    @GetMapping("/search")
    public ResponseEntity<List<SkillResponseDto>> searchSkillsByName(
            @RequestParam String name) {
        return ResponseEntity.ok(skillService.searchSkillsByName(name));
    }

    @Operation(summary = "Update skill by Id")
    @PutMapping("/{id}")
    public ResponseEntity<SkillResponseDto> updateSkill(
            @PathVariable Long id,
            @Valid @RequestBody SkillRequestDto requestDto) {
        return ResponseEntity.ok(skillService.updateSkill(id, requestDto));
    }

    @Operation(summary = "Delete skill by Id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }
}