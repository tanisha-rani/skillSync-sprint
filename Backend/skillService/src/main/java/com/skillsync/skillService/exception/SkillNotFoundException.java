package com.skillsync.skillService.exception;

public class SkillNotFoundException extends RuntimeException{
    public SkillNotFoundException(Long id) {
        super("Skill Not found by id: "+id);
    }
}
