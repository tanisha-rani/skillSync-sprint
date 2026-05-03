package com.skillsync.skillService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.skillService.dto.SkillRequestDto;
import com.skillsync.skillService.dto.SkillResponseDto;
import com.skillsync.skillService.service.JwtService;
import com.skillsync.skillService.service.SkillService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SkillController.class)
@AutoConfigureMockMvc(addFilters = false)
class SkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SkillService skillService;

    @MockBean
    private JwtService jwtService;

    @Test
    void createSkill_returnsCreated() throws Exception {
        SkillRequestDto request = new SkillRequestDto("Java", "Backend", "Desc");
        SkillResponseDto response = new SkillResponseDto();
        response.setId(1L);
        response.setName("Java");

        when(skillService.createSkill(any(SkillRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getSkillById_returnsOk() throws Exception {
        SkillResponseDto response = new SkillResponseDto();
        response.setId(2L);
        response.setName("Spring Boot");

        when(skillService.getSkillById(2L)).thenReturn(response);

        mockMvc.perform(get("/skills/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Spring Boot"));
    }

    @Test
    void getAllSkills_returnsPage() throws Exception {
        SkillResponseDto response = new SkillResponseDto();
        response.setId(3L);
        response.setName("React");

        when(skillService.getAllSkills(0, 10, "id")).thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/skills")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("React"));
    }

    @Test
    void searchSkillsByName_returnsMatches() throws Exception {
        SkillResponseDto response = new SkillResponseDto();
        response.setId(4L);
        response.setName("Java");

        when(skillService.searchSkillsByName("Java")).thenReturn(List.of(response));

        mockMvc.perform(get("/skills/search").param("name", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(4));
    }

    @Test
    void updateSkill_returnsOk() throws Exception {
        SkillRequestDto request = new SkillRequestDto("Java Advanced", "Backend", "Updated");
        SkillResponseDto response = new SkillResponseDto();
        response.setId(5L);
        response.setName("Java Advanced");

        when(skillService.updateSkill(any(Long.class), any(SkillRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/skills/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Java Advanced"));
    }

    @Test
    void deleteSkill_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/skills/6"))
                .andExpect(status().isNoContent());

        verify(skillService).deleteSkill(6L);
    }
}
