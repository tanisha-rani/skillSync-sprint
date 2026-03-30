package com.skillsync.group.service.impl;

import com.skillsync.group.dto.GroupRequestDto;
import com.skillsync.group.entity.Group;
import com.skillsync.group.dto.GroupResponseDto;
import com.skillsync.group.exception.UnauthorizedActionException;
import com.skillsync.group.repository.GroupDiscussionRepository;
import com.skillsync.group.repository.GroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupDiscussionRepository groupDiscussionRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private GroupServiceImpl groupService;

    @Test
    void createGroup_setsDefaultMaxMembers() {
        GroupRequestDto request = new GroupRequestDto(
                "Group",
                "Desc",
                List.of("Java"),
                1L,
                null
        );
        Group mapped = new Group();
        when(modelMapper.map(request, Group.class)).thenReturn(mapped);
        when(modelMapper.map(any(Group.class), eq(GroupResponseDto.class))).thenReturn(new GroupResponseDto());
        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> invocation.getArgument(0));

        groupService.createGroup(request);

        ArgumentCaptor<Group> captor = ArgumentCaptor.forClass(Group.class);
        org.mockito.Mockito.verify(groupRepository).save(captor.capture());
        assertEquals(50, captor.getValue().getMaxMembers());
    }

    @Test
    void updateGroup_whenNotCreator_throwsException() {
        Group group = new Group();
        group.setCreatorUserId(1L);
        when(groupRepository.findById(5L)).thenReturn(Optional.of(group));

        assertThrows(UnauthorizedActionException.class,
                () -> groupService.updateGroup(5L, 99L, new GroupRequestDto()));
    }
}
