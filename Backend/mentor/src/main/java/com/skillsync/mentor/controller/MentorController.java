package com.skillsync.mentor.controller;

import com.skillsync.mentor.dto.MentorRequestDto;
import com.skillsync.mentor.dto.MentorResponseDto;
import com.skillsync.mentor.entity.MentorStatus;
import com.skillsync.mentor.repository.MentorRepository;
import com.skillsync.mentor.service.MentorService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/mentors")
@RequiredArgsConstructor
public class MentorController {
    private final MentorRepository mentorRepository;
     private final MentorService mentorService;

     /*
        Takes Mentor Details from MentorRequestDto
        And then Apply as Mentor
      */
     @Operation(summary = "Apply as mentor")
     @PostMapping("/apply")

     public ResponseEntity<MentorResponseDto> applyAsMentor(@RequestBody MentorRequestDto mentorRequestDto){
         return ResponseEntity.status(HttpStatus.CREATED).body(mentorService.applyAsMentor(mentorRequestDto));
     }

    @Operation(summary = "Get Mentor By User Id")
    @GetMapping("/user/{userId}")
    public ResponseEntity<MentorResponseDto> getMentorByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(mentorService.getMentorByUserId(userId));
    }

    @Operation(summary = "Get Mentor By Id")
    @GetMapping("/{id}")
    public ResponseEntity<MentorResponseDto> getMentorById(@PathVariable Long id) {
        return ResponseEntity.ok(mentorService.getMentorById(id));
    }

    @Operation(summary = "Get All Mentors")
    @GetMapping
    public ResponseEntity<Page<MentorResponseDto>> getAllAMentors(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size,
                                                                  @RequestParam(defaultValue = "id") String sortBy){
         return ResponseEntity.ok(mentorService.getAllMentors(page, size, sortBy));
    }

    @Operation(summary = "Update Mentor Details By Id")
    @PutMapping("/{id}")
    public ResponseEntity<MentorResponseDto> updateMentorById(@PathVariable Long id, @Valid @RequestBody MentorRequestDto mentorRequestDto) {
        return ResponseEntity.ok(mentorService.updateMentor(id, mentorRequestDto));

    }

    @Operation(summary = "Soft Delete Mentor by Id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMentor(@PathVariable Long id) {
        mentorService.deleteMentor(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Approve Mentor by Id")
    @PutMapping("/{id}/approve")
    public ResponseEntity<MentorResponseDto> approveMentor(@PathVariable Long id) {
        return ResponseEntity.ok(mentorService.approveMentor(id));
    }
    @Operation(summary = "Reject  Mentor by Id")
    @PutMapping("/{id}/reject")
    public ResponseEntity<MentorResponseDto> rejectMentor(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(mentorService.rejectMentor(id, reason));
    }

    @Operation(summary = "Reapply mentor application by Id")
    @PutMapping("/{id}/reapply")
    public ResponseEntity<MentorResponseDto> reapplyMentor(@PathVariable Long id) {
        return ResponseEntity.ok(mentorService.reapplyMentor(id));
    }

    @Operation(summary = "Get mentors  by status with pagination")
    @GetMapping("/status/{mentorStatus}")
    public ResponseEntity<Page<MentorResponseDto>> getMentorsByStatus(
            @PathVariable MentorStatus mentorStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(mentorService.getMentorsByStatus(mentorStatus, page, size, sortBy));
    }


    @Operation(summary = "Get mentors  by skill with pagination")
    @GetMapping("/skill/{skill}")
    public ResponseEntity<Page<MentorResponseDto>> getMentorsBySkill(
            @PathVariable String skill,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(mentorService.getMentorsBySkill(skill, page, size, sortBy));
    }

    @Operation(summary = "Get all active mentors")
    @GetMapping("/active")
    public ResponseEntity<Page<MentorResponseDto>> getAvailableMentors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(mentorService.getAvailableMentors(page, size, sortBy));
    }

    @Operation(summary = "Toggle Mentor Availability")
    @PutMapping("/toggle/{id}")
    public ResponseEntity<MentorResponseDto> toggleAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(mentorService.toggleAvailability(id));
    }

    // Add to MentorController.java in mentor service
    @PutMapping("/{id}/rating")
    public ResponseEntity<Void> updateRating(
            @PathVariable Long id,
            @RequestParam Double averageRating,
            @RequestParam Integer totalReviews) {
        mentorService.updateRating(id, averageRating, totalReviews);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/stats")
    public Map<String, Long> getStats() {
        long approvedMentors = mentorRepository.countByStatus(MentorStatus.APPROVED);
        return Map.of("totalMentors", approvedMentors);
    }
}
