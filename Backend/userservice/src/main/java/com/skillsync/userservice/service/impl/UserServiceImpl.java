package com.skillsync.userservice.service.impl;

import com.skillsync.userservice.dto.UserRequestDto;
import com.skillsync.userservice.dto.UserResponseDto;
import com.skillsync.userservice.entity.Role;
import com.skillsync.userservice.entity.User;
import com.skillsync.userservice.exception.UserAlreadyExistsException;
import com.skillsync.userservice.exception.UserNotFoundException;
import com.skillsync.userservice.repository.UserRepository;
import com.skillsync.userservice.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Implementation of UserService interface.
 * Contains all business logic for user management operations.
 * ModelMapper is used to avoid manual entity-to-DTO conversion.
 * All database interactions are handled via UserRepository.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /**
     * Creates a new user after validating email uniqueness.
     * Throws UserAlreadyExistsException if email is already registered.
     * Maps RequestDto to Entity before saving and returns ResponseDto.
     *
     * @param requestDto — contains name, email, role of new user
     * @return UserResponseDto — saved user details
     */

    @Override
    public UserResponseDto createUser(UserRequestDto requestDto) {

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new UserAlreadyExistsException(requestDto.getEmail());
        }
        User user = modelMapper.map(requestDto, User.class);
        User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserResponseDto.class);
    }

    /**
     * Soft deletes a user by setting isActive = false.
     * User data is retained in DB for audit and recovery purposes.
     * Throws UserNotFoundException if user does not exist.
     *
     * @param id — unique ID of the user to be deactivated
     */
    @Transactional
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setIsActive(false);
        userRepository.save(user);
    }

    /**
     * Fetches paginated list of only active users (isActive = true).
     * Excludes soft-deleted users from results.
     * Supports dynamic sorting by any user field.
     *
     * @param page   — page number (0-based)
     * @param size   — number of records per page
     * @param sortBy — field name to sort results by
     * @return Page of UserResponseDto containing active users only
     */
    @Override
    public Page<UserResponseDto> getActiveUsers(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return userRepository.findByIsActive(true, pageable)
                .map(user -> modelMapper.map(user, UserResponseDto.class));
    }

    /**
     * Fetches a single user by their unique ID.
     * Throws UserNotFoundException if no user exists with given ID.
     *
     * @param id — unique ID of the user
     * @return UserResponseDto — user profile details
     */
    @Override
    public UserResponseDto getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return modelMapper.map(user, UserResponseDto.class);
    }

    /**
     * Returns paginated list of all users regardless of active status.
     * Useful for admin to view complete user list.
     * Supports dynamic sorting by any user field.
     *
     * @param page   — page number (0-based)
     * @param size   — number of records per page
     * @param sortBy — field name to sort results by
     * @return Page of UserResponseDto
     */
    @Override
    public Page<UserResponseDto> getAllUsers(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return userRepository.findAll(pageable)
                .map(user -> modelMapper.map(user, UserResponseDto.class));
    }

    /**
     * Updates an existing user profile by ID.
     * Fetches existing user, updates fields, saves and returns updated data.
     * Throws UserNotFoundException if user does not exist.
     *
     * @param id         — unique ID of user to update
     * @param requestDto — contains updated name, email, role
     * @return UserResponseDto — updated user details
     */
    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto requestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setName(requestDto.getName());
        user.setEmail(requestDto.getEmail());
        user.setRole(requestDto.getRole());

        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserResponseDto.class);
    }

    /**
     * Returns paginated list of users filtered by their role.
     * Useful for admin to separately view learners, mentors or admins.
     * Supports dynamic sorting by any user field.
     *
     * @param role   — role to filter by (ROLE_LEARNER, ROLE_MENTOR, ROLE_ADMIN)
     * @param page   — page number (0-based)
     * @param size   — number of records per page
     * @param sortBy — field name to sort results by
     * @return Page of UserResponseDto filtered by role
     */
    @Override
    public Page<UserResponseDto> getUsersByRole(Role role, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return userRepository.findByRole(role, pageable)
                .map(user -> modelMapper.map(user, UserResponseDto.class));
    }

}