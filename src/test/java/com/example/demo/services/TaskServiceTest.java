package com.example.demo.services;

import com.example.demo.dtos.TaskDto;
import com.example.demo.entities.Project;
import com.example.demo.entities.Task;
import com.example.demo.entities.User;
import com.example.demo.exceptions.ProjectNotFoundException;
import com.example.demo.exceptions.TaskNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.repositories.AuditLogRepository;
import com.example.demo.repositories.ProjectRepository;
import com.example.demo.repositories.TaskRepository;
import com.example.demo.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private TaskService taskService;

    private User assignee;
    private Project project;
    private Task task;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        assignee = new User();
        assignee.setId(1L);
        assignee.setUsername("John");

        project = new Project();
        project.setId(1L);
        project.setName("Java App");

        task = new Task();
        task.setId(1L);
        task.setTitle("Database implementation");
        task.setDescription("Implement SQL database");
        task.setPriority(Task.Priority.MEDIUM);
        task.setStatus(Task.Status.TODO);
        task.setDueDate(LocalDate.now());
        task.setProject(project);
        task.setAssignee(assignee);
    }


    @Test
    void testCreateTask_Success() {
        TaskDto dto = new TaskDto();
        dto.setTitle("Database implementation");
        dto.setDescription("Implement SQL database");
        dto.setPriority(Task.Priority.MEDIUM);
        dto.setStatus(Task.Status.TODO);
        dto.setDueDate(LocalDate.now());
        dto.setProjectId(1L);
        dto.setAssigneeId(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(1L)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(task)).thenReturn(task);


        Task toSave = new Task();
        toSave.setTitle(dto.getTitle());
        toSave.setDescription(dto.getDescription());
        toSave.setPriority(dto.getPriority());
        toSave.setStatus(dto.getStatus());
        toSave.setDueDate(dto.getDueDate());
        toSave.setProject(project);
        toSave.setAssignee(assignee);

        when(taskRepository.save(toSave)).thenReturn(task);

        TaskDto saved = taskService.createTask(dto);

        assertNotNull(saved);
        assertEquals("Database implementation", saved.getTitle());
        assertEquals(1L, saved.getProjectId());
        assertEquals(1L, saved.getAssigneeId());
    }

    @Test
    void testCreateTask_ProjectNotFound() {
        TaskDto dto = new TaskDto();
        dto.setProjectId(99L);
        dto.setAssigneeId(1L);

        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> taskService.createTask(dto));
    }

    @Test
    void testCreateTask_AssigneeNotFound() {
        TaskDto dto = new TaskDto();
        dto.setProjectId(1L);
        dto.setAssigneeId(99L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> taskService.createTask(dto));
    }

    @Test
    void testGetTaskById_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskDto found = taskService.getTaskById(1L);

        assertNotNull(found);
        assertEquals("Database implementation", found.getTitle());
        assertEquals(1L, found.getProjectId());
        assertEquals(1L, found.getAssigneeId());
    }

    @Test
    void testGetTaskById_NotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(99L));
    }

    @Test
    void testUpdateTask_Success() {
        TaskDto dto = new TaskDto();
        dto.setTitle("Updated Title");
        dto.setDescription("Updated desc");
        dto.setPriority(Task.Priority.HIGH);
        dto.setStatus(Task.Status.IN_PROGRESS);
        dto.setDueDate(LocalDate.now());
        dto.setAssigneeId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(1L)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(task)).thenReturn(task);

        TaskDto updated = taskService.updateTask(1L, dto);

        assertNotNull(updated);
        assertEquals("Updated Title", updated.getTitle());
        assertEquals(Task.Status.IN_PROGRESS, updated.getStatus());
        assertEquals(Task.Priority.HIGH, updated.getPriority());
    }

    @Test
    void testUpdateTask_NotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());
        TaskDto dto = new TaskDto();
        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(99L, dto));
    }


    @Test
    void testDeleteTask_Success() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void testDeleteTask_NotFound() {
        when(taskRepository.existsById(99L)).thenReturn(false);
        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(99L));
    }


    @Test
    void testGetTasksByUser_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findById(1L)).thenReturn(Optional.of(assignee));
        when(taskRepository.findByAssignee(assignee, pageable))
                .thenReturn(new PageImpl<>(List.of(task)));

        Page<TaskDto> result = taskService.getTasksByUser(1L, 0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals("Database implementation", result.getContent().get(0).getTitle());
    }


    @Test
    void testGetTasksByUser_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> taskService.getTasksByUser(99L, 0, 10));
    }

}
