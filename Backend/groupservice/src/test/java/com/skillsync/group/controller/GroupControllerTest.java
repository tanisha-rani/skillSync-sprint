package com.skillsync.group.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.group.dto.GroupRequestDto;
import com.skillsync.group.dto.GroupResponseDto;
import com.skillsync.group.service.GroupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupController.class)
@AutoConfigureMockMvc(addFilters = false)
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GroupService groupService;

    @Test
    void createGroup_returnsCreated() throws Exception {
        GroupRequestDto request = new GroupRequestDto(
                "Spring Study Group",
                "Group for Spring learners",
                List.of("Spring"),
                10L,
                20
        );
        GroupResponseDto response = GroupResponseDto.builder()
                .id(1L)
                .name("Spring Study Group")
                .build();

        when(groupService.createGroup(any(GroupRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getGroupById_returnsOk() throws Exception {
        GroupResponseDto response = GroupResponseDto.builder()
                .id(2L)
                .name("Group Two")
                .build();
        when(groupService.getGroupById(2L)).thenReturn(response);

        mockMvc.perform(get("/groups/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Group Two"));
    }
}
