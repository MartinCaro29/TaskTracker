package com.example.demo.repositories;

import com.example.demo.entities.Task;
import com.example.demo.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId " +
            "AND (:status IS NULL OR t.status = :status)")
    Page<Task> findByProjectAndStatus(
            @Param("projectId") Long projectId,
            @Param("status") Task.Status status,
            Pageable pageable);


    @Query("SELECT t FROM Task t WHERE t.assignee = :assignee")
    Page<Task> findByAssignee(@Param("assignee") User assignee, Pageable pageable);


    @Query("SELECT t FROM Task t WHERE t.dueDate = CURRENT_DATE")
    Page<Task> findTasksDueToday(Pageable pageable);
}
