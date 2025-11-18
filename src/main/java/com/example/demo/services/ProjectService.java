package com.example.demo.services;

import com.example.demo.dtos.ProjectDto;
import com.example.demo.entities.AuditLog;
import com.example.demo.entities.Project;
import com.example.demo.entities.User;
import com.example.demo.exceptions.ProjectNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.repositories.AuditLogRepository;
import com.example.demo.repositories.ProjectRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository, AuditLogRepository auditLogRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }


    public Page<ProjectDto> getAllProjects(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return projectRepository.findAll(pageable)
                .map(this::convertToDto);
    }


    public ProjectDto getProjectById(Long id) {
        Project project = projectRepository.findById(id).orElse(null);

        if (project == null) {
            throw new ProjectNotFoundException("Project not found with id: " + id);
        }

        return convertToDto(project);
    }


    public ProjectDto createProject(ProjectDto dto) {

        User owner = userRepository.findById(dto.getOwnerId()).orElse(null);

        if (owner == null) {
            auditLogRepository.save(createLog(null, AuditLog.Action.CREATE, AuditLog.Status.FAILURE));
            throw new UserNotFoundException("Owner not found with id: " + dto.getOwnerId());
        }

        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setOwner(owner);

        projectRepository.save(project);

        auditLogRepository.save(createLog(project.getId(), AuditLog.Action.CREATE, AuditLog.Status.SUCCESS));

        return convertToDto(project);
    }


    public ProjectDto updateProject(Long id, ProjectDto dto) {

        Project existing = projectRepository.findById(id).orElse(null);

        if (existing == null) {
            auditLogRepository.save(createLog(id, AuditLog.Action.UPDATE, AuditLog.Status.FAILURE));
            throw new ProjectNotFoundException("Project not found with id: " + id);
        }

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        Project updated = projectRepository.save(existing);

        auditLogRepository.save(createLog(id, AuditLog.Action.UPDATE, AuditLog.Status.SUCCESS));

        return convertToDto(updated);
    }


    public void deleteProject(Long id) {

        if (!projectRepository.existsById(id)) {
            auditLogRepository.save(createLog(id, AuditLog.Action.DELETE, AuditLog.Status.FAILURE));
            throw new ProjectNotFoundException("Project not found with id: " + id);
        }

        projectRepository.deleteById(id);

        auditLogRepository.save(createLog(id, AuditLog.Action.DELETE, AuditLog.Status.SUCCESS));
    }


    private ProjectDto convertToDto(Project project) {
        Long ownerId = project.getOwner() != null ? project.getOwner().getId() : null;

        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setOwnerId(ownerId);

        return dto;
    }


    private AuditLog createLog(Long entityId, AuditLog.Action action, AuditLog.Status status){
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityId(entityId);
        auditLog.setEntityType(AuditLog.EntityType.PROJECT);
        auditLog.setAction(action);
        auditLog.setStatus(status);
        return auditLog;
    }

}
