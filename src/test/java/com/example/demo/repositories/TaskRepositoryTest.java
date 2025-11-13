package com.example.demo.repositories;

import com.example.demo.entities.Project;
import com.example.demo.entities.Task;
import com.example.demo.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Project project;

    @BeforeEach
    void setUp() {


        // Create test data
        user = new User();
        user.setUsername("test");
        user.setEmail("test@example.com");
        user.setPassword("12345678");
        userRepository.save(user);

        project = new Project();
        project.setName("Demo Project");
        project.setOwner(user);
        projectRepository.save(project);

        Task t1 = new Task();
        t1.setTitle("Task 1");
        t1.setStatus(Task.Status.TODO);
        t1.setPriority(Task.Priority.MEDIUM);
        t1.setAssignee(user);
        t1.setProject(project);
        t1.setDueDate(LocalDate.now());

        System.out.println("Status: " + t1.getStatus());
        System.out.println("Priority: " + t1.getPriority());

        taskRepository.save(t1);

        Task t2 = new Task();
        t2.setTitle("Task 2");
        t2.setPriority(Task.Priority.MEDIUM);
        t2.setAssignee(user);
        t2.setProject(project);
        t2.setDueDate(LocalDate.now().plusDays(1));
        t2.setStatus(Task.Status.COMPLETED);
        taskRepository.save(t2);
    }


    @Test
    void testFindByProjectAndStatus() {
        var page = taskRepository.findByProjectAndStatus(project.getId(), Task.Status.TODO, Pageable.unpaged());

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("Task 1");
    }

    @Test
    void testFindByAssignee() {
        List<Task> tasks = taskRepository.findByAssignee(user);

        assertThat(tasks).hasSize(2);
        assertThat(tasks).allMatch(t -> t.getAssignee().equals(user));
    }

    @Test
    void testFindTasksDueToday() {
        List<Task> dueToday = taskRepository.findTasksDueToday();

        assertThat(dueToday).hasSize(1);
        assertThat(dueToday.get(0).getTitle()).isEqualTo("Task 1");
    }

    @Test
    void testSaveAndFindById() {
        Task newTask = new Task();
        newTask.setTitle("Extra");
        newTask.setStatus(Task.Status.COMPLETED);
        newTask.setPriority(Task.Priority.MEDIUM);
        newTask.setProject(project);
        newTask.setAssignee(user);
        newTask.setDueDate(LocalDate.now());

        Task saved = taskRepository.save(newTask);
        assertThat(taskRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void testDelete() {
        Task t = taskRepository.findAll().get(0);
        taskRepository.deleteById(t.getId());

        assertThat(taskRepository.findById(t.getId())).isEmpty();
    }
}
