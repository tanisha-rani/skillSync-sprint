package com.skillsync.skillService.exception;

public class SkillAlreadyExistsException extends RuntimeException{
    public SkillAlreadyExistsException(String name) {
        super("Skill Already exist with id : "+name );
    }
}
