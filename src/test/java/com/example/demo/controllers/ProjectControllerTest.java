package com.example.demo.controllers;
import com.example.demo.dtos.ProjectDto;
import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper mapper;

    private Long ownerId;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        User u = new User();
        u.setUsername("john");
        u.setEmail("john@example.com");
        u.setPassword("12345678");
        ownerId = userRepository.save(u).getId();
    }

    @Test
    void testCreateAndGetProject() throws Exception {

        ProjectDto dto = new ProjectDto();
        dto.setName("Project A");
        dto.setDescription("desc");
        dto.setOwnerId(ownerId);

        // CREATE
        String json = mockMvc.perform(
                        post("/api/projects")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Project A"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProjectDto created = mapper.readValue(json, ProjectDto.class);

        // GET
        mockMvc.perform(get("/api/projects/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.name").value("Project A"));
    }
}