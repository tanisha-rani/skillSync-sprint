package com.skillsync.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

/**
 * Represents the users table in the database.
 * Stores core user profile information.
 * @CreationTimestamp and @UpdateTimestamp handle audit fields automatically.
 */

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_active", columnDefinition = "TINYINT(1)")
    private Boolean isActive = true;
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
