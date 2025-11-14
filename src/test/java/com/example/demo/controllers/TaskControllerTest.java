package com.example.demo.controllers;

import com.example.demo.dtos.ProjectDto;
import com.example.demo.dtos.TaskDto;
import com.example.demo.entities.Project;
import com.example.demo.entities.Task;
import com.example.demo.entities.User;
import com.example.demo.repositories.ProjectRepository;
import com.example.demo.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TaskControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ObjectMapper mapper;

    private Long assigneeId;
    private Long projectId;

    private String url(String path) {
        return "http://localhost:" + port + "/api" + path;
    }

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        User u = new User();
        u.setUsername("john");
        u.setEmail("john@example.com");
        u.setPassword("12345678");
        assigneeId = userRepository.save(u).getId();

        Project p = new Project();
        p.setName("Project 1");
        p.setDescription("desc");
        p.setOwner(u);
        projectId = projectRepository.save(p).getId();
    }

    @Test
    void testCreateAndGetTask() throws Exception {

        TaskDto dto = new TaskDto();
        dto.setTitle("Task 1");
        dto.setDescription("desc");
        dto.setStatus(Task.Status.IN_PROGRESS);
        dto.setPriority(Task.Priority.LOW);
        dto.setDueDate(LocalDate.now().plusDays(1));
        dto.setProjectId(projectId);
        dto.setAssigneeId(assigneeId);

        // CREATE
        String json = mockMvc.perform(
                        post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(dto))
                )
                .andExpect(status().isCreated())               // 201 correct
                .andExpect(jsonPath("$.title").value("Task 1"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        TaskDto created = mapper.readValue(json, TaskDto.class);

        // GET
        mockMvc.perform(get("/api/tasks/" + created.getId()))   // correct endpoint
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.title").value("Task 1"));
    }

}




