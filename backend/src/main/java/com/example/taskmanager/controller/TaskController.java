package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskCreateRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.security.UserPrincipal;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TaskController {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    
    private final TaskService taskService;
    
    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    
    /**
     * Create a new task
     */
    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskCreateRequest request) {
        try {
            Long userId = getCurrentUserId();
            TaskResponse task = taskService.createTask(request, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Task created successfully");
            response.put("data", task);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error creating task: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Failed to create task: " + e.getMessage()));
        }
    }
    
    /**
     * Get all tasks for the current user
     */
    @GetMapping
    public ResponseEntity<?> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String search) {
        try {
            Long userId = getCurrentUserId();
            
            // Create sort object
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            List<TaskResponse> tasks;
            
            if (search != null && !search.trim().isEmpty()) {
                tasks = taskService.searchTasks(search.trim(), userId);
            } else if (status != null) {
                tasks = taskService.getTasksByStatus(status, userId);
            } else if (priority != null) {
                tasks = taskService.getTasksByPriority(priority, userId);
            } else {
                Page<TaskResponse> taskPage = taskService.getTasksByUserWithPagination(userId, pageable);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", taskPage.getContent());
                response.put("currentPage", taskPage.getNumber());
                response.put("totalItems", taskPage.getTotalElements());
                response.put("totalPages", taskPage.getTotalPages());
                
                return ResponseEntity.ok(response);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", tasks);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching tasks: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to fetch tasks: " + e.getMessage()));
        }
    }
    
    /**
     * Get task by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            TaskResponse task = taskService.getTaskById(id, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", task);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching task with ID {}: ", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Task not found: " + e.getMessage()));
        }
    }
    
    /**
     * Update task
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @Valid @RequestBody TaskCreateRequest request) {
        try {
            Long userId = getCurrentUserId();
            TaskResponse task = taskService.updateTask(id, request, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Task updated successfully");
            response.put("data", task);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating task with ID {}: ", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Failed to update task: " + e.getMessage()));
        }
    }
    
    /**
     * Delete task
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            taskService.deleteTask(id, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Task deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error deleting task with ID {}: ", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Failed to delete task: " + e.getMessage()));
        }
    }
    
    /**
     * Get overdue tasks
     */
    @GetMapping("/overdue")
    public ResponseEntity<?> getOverdueTasks() {
        try {
            Long userId = getCurrentUserId();
            List<TaskResponse> tasks = taskService.getOverdueTasks(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", tasks);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching overdue tasks: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to fetch overdue tasks: " + e.getMessage()));
        }
    }
    
    /**
     * Get task statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getTaskStatistics() {
        try {
            Long userId = getCurrentUserId();
            TaskService.TaskStatistics statistics = taskService.getTaskStatistics(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching task statistics: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to fetch task statistics: " + e.getMessage()));
        }
    }
    
    /**
     * Get current user ID from security context
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getId();
    }
    
    /**
     * Create error response
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}

