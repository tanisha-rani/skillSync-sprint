package com.skillsync.userservice.service;

import com.skillsync.userservice.dto.UserRequestDto;
import com.skillsync.userservice.dto.UserResponseDto;
import com.skillsync.userservice.entity.Role;
import org.springframework.data.domain.Page;


/**
 * Service contract for user business logic.
 * Programming to interface — implementation can be swapped without affecting controller.
 */
public interface UserService {

    UserResponseDto createUser(UserRequestDto requestDto);

    void deleteUser(Long id);

    UserResponseDto getUserById(Long id);

    UserResponseDto updateUser(Long id, UserRequestDto requestDto);
    Page<UserResponseDto> getAllUsers(int page, int size, String sortBy);
    Page<UserResponseDto> getUsersByRole(Role role, int page, int size, String sortBy);
    Page<UserResponseDto> getActiveUsers(int page, int size, String sortBy);

}
