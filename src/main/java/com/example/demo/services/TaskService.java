package com.example.demo.services;

import com.example.demo.dtos.TaskDto;
import com.example.demo.entities.AuditLog;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final AuditLogRepository auditLogRepository;

    public TaskService(TaskRepository taskRepository,
                       ProjectRepository projectRepository,
                       UserRepository userRepository,
                       EmailService emailService,
                       AuditLogRepository auditLogRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.auditLogRepository = auditLogRepository;
    }

    private void sendTaskEmail(Task task, User assignee){
        emailService.sendEmail(
                assignee.getEmail(),
                "New Task Assigned: " + task.getTitle(),
                "Hello " + assignee.getUsername() + ",\n\n" +
                        "You have been assigned a new task:\n\n" +
                        "• Title: " + task.getTitle() + "\n" +
                        "• Description: " + task.getDescription() + "\n" +
                        "• Status: " + task.getStatus() + "\n" +
                        "• Priority: " + task.getPriority() + "\n" +
                        "• Due Date: " + task.getDueDate() + "\n\n" +
                        "Regards,\nTask Manager System"
        );
    }

    public TaskDto createTask(TaskDto dto) {

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> {

                    auditLogRepository.save(createLog(dto.getProjectId(), AuditLog.Action.CREATE, AuditLog.Status.FAILURE));
                    return new ProjectNotFoundException("Project not found");
                });

        User assignee = userRepository.findById(dto.getAssigneeId())
                .orElseThrow(() -> {
                    auditLogRepository.save(createLog(dto.getAssigneeId(), AuditLog.Action.CREATE, AuditLog.Status.FAILURE));
                    return new UserNotFoundException("Assignee not found");
                });

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setStatus(dto.getStatus());
        task.setDueDate(dto.getDueDate());
        task.setProject(project);
        task.setAssignee(assignee);

        taskRepository.save(task);

        auditLogRepository.save(createLog(task.getId(), AuditLog.Action.CREATE, AuditLog.Status.SUCCESS));

        sendTaskEmail(task, assignee);

        return convertToDto(task);
    }

    public TaskDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        return convertToDto(task);
    }

    public Page<TaskDto> getTasksByProject(Long projectId, Task.Status status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return taskRepository.findByProjectAndStatus(projectId, status, pageable)
                .map(this::convertToDto);
    }

    public Page<TaskDto> getTasksDueToday(int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        return taskRepository.findTasksDueToday(pageable)
                .map(this::convertToDto);
    }

    public TaskDto updateTask(Long id, TaskDto dto) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    auditLogRepository.save(createLog(id, AuditLog.Action.UPDATE, AuditLog.Status.FAILURE));
                    return new TaskNotFoundException("Task not found");
                });

        User assignee = null;

        if (dto.getAssigneeId() != null) {
            assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> {
                        auditLogRepository.save(createLog(id, AuditLog.Action.UPDATE, AuditLog.Status.FAILURE));
                        return new UserNotFoundException("Assignee not found");
                    });
        }

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setStatus(dto.getStatus());
        task.setDueDate(dto.getDueDate());
        if (assignee != null) task.setAssignee(assignee);

        Task updated = taskRepository.save(task);

        auditLogRepository.save(createLog(id, AuditLog.Action.UPDATE, AuditLog.Status.SUCCESS));

        if (assignee != null) sendTaskEmail(task, assignee);

        return convertToDto(updated);
    }


    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            AuditLog auditLog = createLog(id, AuditLog.Action.DELETE, AuditLog.Status.FAILURE);
            auditLogRepository.save(auditLog);
            throw new TaskNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);

        AuditLog auditLog = createLog(id, AuditLog.Action.DELETE, AuditLog.Status.SUCCESS);
        auditLogRepository.save(auditLog);
    }

    public Page<TaskDto> getTasksByUser(Long userId,int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return taskRepository.findByAssignee(user, pageable)
                .map(this::convertToDto);
    }

    private TaskDto convertToDto(Task task) {
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getProject().getId(),
                task.getAssignee() != null ? task.getAssignee().getId() : null
        );
    }

    private AuditLog createLog(Long entityId, AuditLog.Action action, AuditLog.Status status){
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityId(entityId);
        auditLog.setEntityType(AuditLog.EntityType.TASK);
        auditLog.setAction(action);
        auditLog.setStatus(status);

        return auditLog;
    }
}
