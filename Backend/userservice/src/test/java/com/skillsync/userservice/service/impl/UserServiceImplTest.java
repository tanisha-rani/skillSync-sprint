package com.skillsync.userservice.service.impl;

import com.skillsync.userservice.dto.UserRequestDto;
import com.skillsync.userservice.entity.Role;
import com.skillsync.userservice.entity.User;
import com.skillsync.userservice.exception.UserAlreadyExistsException;
import com.skillsync.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_whenExists_throwsException() {
        UserRequestDto request = new UserRequestDto("Test User", "user@example.com", Role.ROLE_LEARNER);
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request));
    }

    @Test
    void deleteUser_setsInactive() {
        User user = new User();
        user.setId(1L);
        user.setIsActive(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.deleteUser(1L);

        assertEquals(false, user.getIsActive());
    }
}
