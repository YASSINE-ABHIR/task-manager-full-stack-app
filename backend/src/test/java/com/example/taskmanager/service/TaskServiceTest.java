package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskCreateRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    
    @Mock
    private TaskRepository taskRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private TaskService taskService;
    
    private User testUser;
    private Task testTask;
    private TaskCreateRequest taskCreateRequest;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(TaskStatus.PENDING);
        testTask.setPriority(TaskPriority.MEDIUM);
        testTask.setDueDate(LocalDate.now().plusDays(7));
        testTask.setUser(testUser);
        testTask.setCreatedAt(LocalDateTime.now());
        testTask.setUpdatedAt(LocalDateTime.now());
        
        taskCreateRequest = new TaskCreateRequest();
        taskCreateRequest.setTitle("Test Task");
        taskCreateRequest.setDescription("Test Description");
        taskCreateRequest.setStatus(TaskStatus.PENDING);
        taskCreateRequest.setPriority(TaskPriority.MEDIUM);
        taskCreateRequest.setDueDate(LocalDate.now().plusDays(7));
    }
    
    @Test
    void createTask_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        // When
        TaskResponse result = taskService.createTask(taskCreateRequest, 1L);
        
        // Then
        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        assertEquals("Test Description", result.getDescription());
        assertEquals(TaskStatus.PENDING, result.getStatus());
        assertEquals(TaskPriority.MEDIUM, result.getPriority());
        assertEquals(1L, result.getUserId());
        
        verify(userRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
    }
    
    @Test
    void createTask_UserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> taskService.createTask(taskCreateRequest, 1L));
        
        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(taskRepository, never()).save(any(Task.class));
    }
    
    @Test
    void getAllTasksByUser_Success() {
        // Given
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.findByUserId(1L)).thenReturn(tasks);
        
        // When
        List<TaskResponse> result = taskService.getAllTasksByUser(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getTitle());
        
        verify(taskRepository).findByUserId(1L);
    }
    
    @Test
    void getTasksByUserWithPagination_Success() {
        // Given
        List<Task> tasks = Arrays.asList(testTask);
        Page<Task> taskPage = new PageImpl<>(tasks);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(taskRepository.findByUserId(1L, pageable)).thenReturn(taskPage);
        
        // When
        Page<TaskResponse> result = taskService.getTasksByUserWithPagination(1L, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Task", result.getContent().get(0).getTitle());
        
        verify(taskRepository).findByUserId(1L, pageable);
    }
    
    @Test
    void getTaskById_Success() {
        // Given
        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTask));
        
        // When
        TaskResponse result = taskService.getTaskById(1L, 1L);
        
        // Then
        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        assertEquals(1L, result.getId());
        
        verify(taskRepository).findByIdAndUserId(1L, 1L);
    }
    
    @Test
    void getTaskById_TaskNotFound() {
        // Given
        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> taskService.getTaskById(1L, 1L));
        
        assertEquals("Task not found with ID: 1", exception.getMessage());
        verify(taskRepository).findByIdAndUserId(1L, 1L);
    }
    
    @Test
    void updateTask_Success() {
        // Given
        TaskCreateRequest updateRequest = new TaskCreateRequest();
        updateRequest.setTitle("Updated Task");
        updateRequest.setDescription("Updated Description");
        updateRequest.setStatus(TaskStatus.IN_PROGRESS);
        updateRequest.setPriority(TaskPriority.HIGH);
        
        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("Updated Description");
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);
        updatedTask.setPriority(TaskPriority.HIGH);
        updatedTask.setUser(testUser);
        
        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        
        // When
        TaskResponse result = taskService.updateTask(1L, updateRequest, 1L);
        
        // Then
        assertNotNull(result);
        assertEquals("Updated Task", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        assertEquals(TaskPriority.HIGH, result.getPriority());
        
        verify(taskRepository).findByIdAndUserId(1L, 1L);
        verify(taskRepository).save(any(Task.class));
    }
    
    @Test
    void deleteTask_Success() {
        // Given
        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTask));
        
        // When
        taskService.deleteTask(1L, 1L);
        
        // Then
        verify(taskRepository).findByIdAndUserId(1L, 1L);
        verify(taskRepository).delete(testTask);
    }
    
    @Test
    void deleteTask_TaskNotFound() {
        // Given
        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> taskService.deleteTask(1L, 1L));
        
        assertEquals("Task not found with ID: 1", exception.getMessage());
        verify(taskRepository).findByIdAndUserId(1L, 1L);
        verify(taskRepository, never()).delete(any(Task.class));
    }
    
    @Test
    void getTasksByStatus_Success() {
        // Given
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.findByStatusAndUserId(TaskStatus.PENDING, 1L)).thenReturn(tasks);
        
        // When
        List<TaskResponse> result = taskService.getTasksByStatus(TaskStatus.PENDING, 1L);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TaskStatus.PENDING, result.get(0).getStatus());
        
        verify(taskRepository).findByStatusAndUserId(TaskStatus.PENDING, 1L);
    }
    
    @Test
    void getTasksByPriority_Success() {
        // Given
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.findByPriorityAndUserId(TaskPriority.MEDIUM, 1L)).thenReturn(tasks);
        
        // When
        List<TaskResponse> result = taskService.getTasksByPriority(TaskPriority.MEDIUM, 1L);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TaskPriority.MEDIUM, result.get(0).getPriority());
        
        verify(taskRepository).findByPriorityAndUserId(TaskPriority.MEDIUM, 1L);
    }
    
    @Test
    void searchTasks_Success() {
        // Given
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.searchByKeywordAndUserId("test", 1L)).thenReturn(tasks);
        
        // When
        List<TaskResponse> result = taskService.searchTasks("test", 1L);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getTitle().toLowerCase().contains("test"));
        
        verify(taskRepository).searchByKeywordAndUserId("test", 1L);
    }
    
    @Test
    void getOverdueTasks_Success() {
        // Given
        Task overdueTask = new Task();
        overdueTask.setId(2L);
        overdueTask.setTitle("Overdue Task");
        overdueTask.setStatus(TaskStatus.PENDING);
        overdueTask.setDueDate(LocalDate.now().minusDays(1));
        overdueTask.setUser(testUser);
        
        List<Task> overdueTasks = Arrays.asList(overdueTask);
        when(taskRepository.findByDueDateBeforeAndUserId(any(LocalDate.class), eq(1L)))
            .thenReturn(overdueTasks);
        
        // When
        List<TaskResponse> result = taskService.getOverdueTasks(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getDueDate().isBefore(LocalDate.now()));
        
        verify(taskRepository).findByDueDateBeforeAndUserId(any(LocalDate.class), eq(1L));
    }
    
    @Test
    void getTaskStatistics_Success() {
        // Given
        when(taskRepository.countByStatusAndUserId(null, 1L)).thenReturn(10L);
        when(taskRepository.countByStatusAndUserId(TaskStatus.PENDING, 1L)).thenReturn(3L);
        when(taskRepository.countByStatusAndUserId(TaskStatus.IN_PROGRESS, 1L)).thenReturn(2L);
        when(taskRepository.countByStatusAndUserId(TaskStatus.COMPLETED, 1L)).thenReturn(5L);
        when(taskRepository.countOverdueTasksByUserId(eq(1L), any(LocalDate.class))).thenReturn(1L);
        
        // When
        TaskService.TaskStatistics result = taskService.getTaskStatistics(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(10L, result.getTotalTasks());
        assertEquals(3L, result.getPendingTasks());
        assertEquals(2L, result.getInProgressTasks());
        assertEquals(5L, result.getCompletedTasks());
        assertEquals(1L, result.getOverdueTasks());
    }
}

