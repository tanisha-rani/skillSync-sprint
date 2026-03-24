package com.skillsync.userservice.repository;

import com.skillsync.userservice.entity.Role;
import com.skillsync.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Data access layer for User entity.
 * Extends JpaRepository — provides built-in CRUD + pagination out of the box.
 * Custom queries follow Spring Data JPA naming conventions.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Page<User> findAll(Pageable pageable);
    Page<User> findByRole(Role role, Pageable pageable);
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);

}
