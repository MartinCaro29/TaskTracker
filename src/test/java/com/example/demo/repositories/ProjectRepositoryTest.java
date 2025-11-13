package com.example.demo.repositories;

import com.example.demo.entities.Project;
import com.example.demo.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest

class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;

    @BeforeEach
    void setup() {

        owner = new User();
        owner.setUsername("Test");
        owner.setEmail("test@example.com");
        owner.setPassword("12345678");
        userRepository.save(owner);
    }

    @BeforeEach
    void cleanDatabase() {
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testSaveProject() {
        Project project = new Project();
        project.setName("Test");
        project.setDescription("Test");
        project.setOwner(owner);

        Project saved = projectRepository.save(project);

        assertNotNull(saved.getId());
        assertEquals("Test", saved.getName());
        assertEquals(owner.getId(), saved.getOwner().getId());
    }

    @Test
    void testFindById() {
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Description");
        project.setOwner(owner);
        projectRepository.save(project);

        Optional<Project> found = projectRepository.findById(project.getId());

        assertTrue(found.isPresent());
        assertEquals("Test Project", found.get().getName());
    }

    @Test
    void testFindAll() {
        Project p1 = new Project();
        p1.setName("Project1");
        p1.setOwner(owner);

        Project p2 = new Project();
        p2.setName("Project2");
        p2.setOwner(owner);

        projectRepository.saveAll(List.of(p1, p2));

        List<Project> all = projectRepository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void testDeleteById() {
        Project project = new Project();
        project.setName("Delete Me");
        project.setOwner(owner);
        projectRepository.save(project);

        projectRepository.deleteById(project.getId());
        Optional<Project> found = projectRepository.findById(project.getId());

        assertTrue(found.isEmpty());
    }
}
