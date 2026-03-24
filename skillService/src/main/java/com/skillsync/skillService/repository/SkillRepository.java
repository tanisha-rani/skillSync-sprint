package com.skillsync.skillService.repository;

import com.skillsync.skillService.entity.Skills;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skills,Long> {
    boolean existsByName(String name);
    List<Skills> findByNameContainingIgnoreCase(String name);

}
