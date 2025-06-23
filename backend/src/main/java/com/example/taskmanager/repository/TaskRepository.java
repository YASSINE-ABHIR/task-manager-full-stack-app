package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    /**
     * Find all tasks by user ID
     */
    List<Task> findByUserId(Long userId);
    
    /**
     * Find all tasks by user ID with pagination
     */
    Page<Task> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Find task by ID and user ID (for security)
     */
    Optional<Task> findByIdAndUserId(Long id, Long userId);
    
    /**
     * Find tasks by status and user ID
     */
    List<Task> findByStatusAndUserId(TaskStatus status, Long userId);
    
    /**
     * Find tasks by priority and user ID
     */
    List<Task> findByPriorityAndUserId(TaskPriority priority, Long userId);
    
    /**
     * Find tasks due before a certain date
     */
    List<Task> findByDueDateBeforeAndUserId(LocalDate date, Long userId);
    
    /**
     * Find tasks due on a specific date
     */
    List<Task> findByDueDateAndUserId(LocalDate date, Long userId);
    
    /**
     * Search tasks by title containing keyword
     */
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Task> searchByKeywordAndUserId(@Param("keyword") String keyword, @Param("userId") Long userId);
    
    /**
     * Find tasks with filters
     */
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId " +
           "AND (:status IS NULL OR t.status = :status) " +
           "AND (:priority IS NULL OR t.priority = :priority) " +
           "AND (:dueDateFrom IS NULL OR t.dueDate >= :dueDateFrom) " +
           "AND (:dueDateTo IS NULL OR t.dueDate <= :dueDateTo)")
    Page<Task> findTasksWithFilters(
            @Param("userId") Long userId,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("dueDateFrom") LocalDate dueDateFrom,
            @Param("dueDateTo") LocalDate dueDateTo,
            Pageable pageable
    );
    
    /**
     * Count tasks by status for a user
     */
    long countByStatusAndUserId(TaskStatus status, Long userId);
    
    /**
     * Count overdue tasks for a user
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.dueDate < :currentDate AND t.status != 'COMPLETED'")
    long countOverdueTasksByUserId(@Param("userId") Long userId, @Param("currentDate") LocalDate currentDate);
}

