package com.skillsync.skillService.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SkillRequestDto {
    @NotBlank(message = "Skill name is required")
    private String name;
    @NotBlank(message = "Mention Category from which the skill belong")
    private String category;

    @Size(max=200 , message = "Description must not exceed 200 characters")
    private String description;


}
