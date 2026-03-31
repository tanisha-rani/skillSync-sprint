package com.skillsync.userservice.controller;

import com.skillsync.userservice.dto.UserRequestDto;
import com.skillsync.userservice.dto.UserResponseDto;
import com.skillsync.userservice.entity.Role;
import com.skillsync.userservice.repository.UserRepository;
import com.skillsync.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Service", description = "APIs for managing users")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    //Create a new User
    //validates data before entering in db
    @Operation(summary = "Create a new user")
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(requestDto));
    }

    //Get users By Id
    //Throw Exception if User exist by id
    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    //Get all Users Details
    //Added Pagination( one page consist of 10 user details)
    @Operation(summary = "Get all users with pagination")
    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(userService.getAllUsers(page, size, sortBy));
    }

    //Update User details by id
    //Find the user by Id then update details
    //If user not found by id then throw exception

    @Operation(summary = "Update user by ID")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto requestDto) {
        return ResponseEntity.ok(userService.updateUser(id, requestDto));
    }

    //Soft deletes a user by setting isActive = false.
    //User data is retained in DB — not permanently deleted.
    @Operation(summary = "Soft delete user by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

     //Fetches paginated list of users filtered by role.
     // Useful for admin to view all mentors or learners separately.
    @Operation(summary = "Get users by role with pagination")
    @GetMapping("/role/{role}")
    public ResponseEntity<Page<UserResponseDto>> getUsersByRole(
            @PathVariable Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(userService.getUsersByRole(role, page, size, sortBy));
    }


    //Returns only active users (isActive = true).
    //Excludes soft-deleted users from results.
    @Operation(summary = "Get all active users")
    @GetMapping("/active")
    public ResponseEntity<Page<UserResponseDto>> getActiveUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(userService.getActiveUsers(page, size, sortBy));
    }

    @GetMapping("/admin/stats")
    public Map<String, Long> getStats() {
        long totalUsers = userRepository.count();
        return Map.of("totalUsers", totalUsers);
    }

}
