package com.example.demo.controllers;

import com.example.demo.dtos.TaskDto;
import com.example.demo.entities.Task;
import com.example.demo.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto taskDto) {
        TaskDto createdTask = taskService.createTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        TaskDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<Page<TaskDto>> getTasksByProject(
            @PathVariable Long projectId,
            @RequestParam(required = false) Task.Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TaskDto> tasks = taskService.getTasksByProject(projectId, status, page, size);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id,
                                              @Valid @RequestBody TaskDto updatedTask) {
        TaskDto task = taskService.updateTask(id, updatedTask);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tasks/due-today")
    public ResponseEntity<Page<TaskDto>> getTasksDueToday(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        Page<TaskDto> tasks = taskService.getTasksDueToday(page, size);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/users/{userId}/tasks")
    public ResponseEntity<Page<TaskDto>> getTasksByUser(@PathVariable Long userId,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size){
        Page<TaskDto> tasks = taskService.getTasksByUser(userId, page, size);
        return ResponseEntity.ok(tasks);
    }
}
