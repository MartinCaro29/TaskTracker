package com.example.demo.services;

import com.example.demo.dtos.ProjectDto;
import com.example.demo.entities.Project;
import com.example.demo.entities.User;
import com.example.demo.repositories.ProjectRepository;
import com.example.demo.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    private User owner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        owner = new User();
        owner.setId(1L);
        owner.setUsername("john_doe");
        owner.setEmail("john@example.com");
        owner.setPassword("password123");
    }

    @Test
    void testGetAllProjects() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Demo Project");
        project.setDescription("Simple test project");
        project.setOwner(owner);

        Page<Project> projectPage = new PageImpl<>(List.of(project));
        when(projectRepository.findAll(PageRequest.of(0, 10))).thenReturn(projectPage);

        Page<ProjectDto> result = projectService.getAllProjects(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Demo Project", result.getContent().get(0).getName());
        assertEquals(1L, result.getContent().get(0).getOwnerId());
    }

    @Test
    void testGetProjectById_Success() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Project Alpha");
        project.setOwner(owner);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        ProjectDto found = projectService.getProjectById(1L);

        assertNotNull(found);
        assertEquals("Project Alpha", found.getName());
        assertEquals(1L, found.getOwnerId());
    }

    @Test
    void testGetProjectById_NotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> projectService.getProjectById(99L));

        assertEquals("Project not found with id: 99", ex.getMessage());
    }

    @Test
    void testCreateProject() {

        ProjectDto dto = new ProjectDto();
        dto.setName("New Project");
        dto.setDescription("Test description");
        dto.setOwnerId(owner.getId());

        Project projectToSave = new Project();
        projectToSave.setName(dto.getName());
        projectToSave.setDescription(dto.getDescription());
        projectToSave.setOwner(owner);


        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(projectRepository.save(projectToSave)).thenReturn(projectToSave);


        ProjectDto saved = projectService.createProject(dto);


        assertNotNull(saved);
        assertEquals("New Project", saved.getName());
        assertEquals(1L, saved.getOwnerId());
    }


    @Test
    void testUpdateProject() {

        Project existing = new Project();
        existing.setId(1L);
        existing.setName("Old Name");
        existing.setDescription("Old description");
        existing.setOwner(owner);

        ProjectDto updatedDto = new ProjectDto();
        updatedDto.setName("Updated Name");
        updatedDto.setDescription("Updated description");
        updatedDto.setOwnerId(1L);


        when(projectRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(projectRepository.save(existing)).thenReturn(existing);

        ProjectDto result = projectService.updateProject(1L, updatedDto);


        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated description", result.getDescription());
        assertEquals(1L, result.getOwnerId());

    }
}