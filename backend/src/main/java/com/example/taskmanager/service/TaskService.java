package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskCreateRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * Create a new task
     */
    public TaskResponse createTask(TaskCreateRequest request, Long userId) {
        logger.debug("Creating new task for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus() != null ? request.getStatus() : TaskStatus.PENDING);
        task.setPriority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM);
        task.setDueDate(request.getDueDate());
        task.setUser(user);
        
        Task savedTask = taskRepository.save(task);
        logger.info("Created task with ID: {} for user: {}", savedTask.getId(), user.getUsername());
        
        return new TaskResponse(savedTask);
    }
    
    /**
     * Get all tasks for a user
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasksByUser(Long userId) {
        logger.debug("Fetching all tasks for user ID: {}", userId);
        
        List<Task> tasks = taskRepository.findByUserId(userId);
        return tasks.stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get tasks with pagination
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByUserWithPagination(Long userId, Pageable pageable) {
        logger.debug("Fetching tasks with pagination for user ID: {}", userId);
        
        Page<Task> tasks = taskRepository.findByUserId(userId, pageable);
        return tasks.map(TaskResponse::new);
    }
    
    /**
     * Get task by ID
     */
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long taskId, Long userId) {
        logger.debug("Fetching task ID: {} for user ID: {}", taskId, userId);
        
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
        
        return new TaskResponse(task);
    }
    
    /**
     * Update task
     */
    public TaskResponse updateTask(Long taskId, TaskCreateRequest request, Long userId) {
        logger.debug("Updating task ID: {} for user ID: {}", taskId, userId);
        
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus() != null ? request.getStatus() : task.getStatus());
        task.setPriority(request.getPriority() != null ? request.getPriority() : task.getPriority());
        task.setDueDate(request.getDueDate());
        
        Task updatedTask = taskRepository.save(task);
        logger.info("Updated task with ID: {}", updatedTask.getId());
        
        return new TaskResponse(updatedTask);
    }
    
    /**
     * Delete task
     */
    public void deleteTask(Long taskId, Long userId) {
        logger.debug("Deleting task ID: {} for user ID: {}", taskId, userId);
        
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
        
        taskRepository.delete(task);
        logger.info("Deleted task with ID: {}", taskId);
    }
    
    /**
     * Get tasks by status
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByStatus(TaskStatus status, Long userId) {
        logger.debug("Fetching tasks with status: {} for user ID: {}", status, userId);
        
        List<Task> tasks = taskRepository.findByStatusAndUserId(status, userId);
        return tasks.stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get tasks by priority
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByPriority(TaskPriority priority, Long userId) {
        logger.debug("Fetching tasks with priority: {} for user ID: {}", priority, userId);
        
        List<Task> tasks = taskRepository.findByPriorityAndUserId(priority, userId);
        return tasks.stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Search tasks by keyword
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> searchTasks(String keyword, Long userId) {
        logger.debug("Searching tasks with keyword: '{}' for user ID: {}", keyword, userId);
        
        List<Task> tasks = taskRepository.searchByKeywordAndUserId(keyword, userId);
        return tasks.stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get overdue tasks
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getOverdueTasks(Long userId) {
        logger.debug("Fetching overdue tasks for user ID: {}", userId);
        
        LocalDate today = LocalDate.now();
        List<Task> tasks = taskRepository.findByDueDateBeforeAndUserId(today, userId);
        
        return tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.COMPLETED)
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get task statistics for a user
     */
    @Transactional(readOnly = true)
    public TaskStatistics getTaskStatistics(Long userId) {
        logger.debug("Fetching task statistics for user ID: {}", userId);
        
        long totalTasks = taskRepository.countByStatusAndUserId(null, userId);
        long pendingTasks = taskRepository.countByStatusAndUserId(TaskStatus.PENDING, userId);
        long inProgressTasks = taskRepository.countByStatusAndUserId(TaskStatus.IN_PROGRESS, userId);
        long completedTasks = taskRepository.countByStatusAndUserId(TaskStatus.COMPLETED, userId);
        long overdueTasks = taskRepository.countOverdueTasksByUserId(userId, LocalDate.now());
        
        return new TaskStatistics(totalTasks, pendingTasks, inProgressTasks, completedTasks, overdueTasks);
    }
    
    /**
     * Inner class for task statistics
     */
    public static class TaskStatistics {
        private final long totalTasks;
        private final long pendingTasks;
        private final long inProgressTasks;
        private final long completedTasks;
        private final long overdueTasks;
        
        public TaskStatistics(long totalTasks, long pendingTasks, long inProgressTasks, 
                            long completedTasks, long overdueTasks) {
            this.totalTasks = totalTasks;
            this.pendingTasks = pendingTasks;
            this.inProgressTasks = inProgressTasks;
            this.completedTasks = completedTasks;
            this.overdueTasks = overdueTasks;
        }
        
        // Getters
        public long getTotalTasks() { return totalTasks; }
        public long getPendingTasks() { return pendingTasks; }
        public long getInProgressTasks() { return inProgressTasks; }
        public long getCompletedTasks() { return completedTasks; }
        public long getOverdueTasks() { return overdueTasks; }
    }
}

