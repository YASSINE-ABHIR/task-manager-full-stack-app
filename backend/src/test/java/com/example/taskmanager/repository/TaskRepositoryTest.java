package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TaskRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private User testUser;
    private Task testTask1;
    private Task testTask2;
    
    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser = entityManager.persistAndFlush(testUser);
        
        // Create test tasks
        testTask1 = new Task();
        testTask1.setTitle("Task 1");
        testTask1.setDescription("Description 1");
        testTask1.setStatus(TaskStatus.PENDING);
        testTask1.setPriority(TaskPriority.HIGH);
        testTask1.setDueDate(LocalDate.now().plusDays(5));
        testTask1.setUser(testUser);
        testTask1 = entityManager.persistAndFlush(testTask1);
        
        testTask2 = new Task();
        testTask2.setTitle("Task 2");
        testTask2.setDescription("Description 2");
        testTask2.setStatus(TaskStatus.COMPLETED);
        testTask2.setPriority(TaskPriority.MEDIUM);
        testTask2.setDueDate(LocalDate.now().minusDays(2));
        testTask2.setUser(testUser);
        testTask2 = entityManager.persistAndFlush(testTask2);
        
        entityManager.clear();
    }
    
    @Test
    void findByUserId_Success() {
        // When
        List<Task> tasks = taskRepository.findByUserId(testUser.getId());
        
        // Then
        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().allMatch(task -> task.getUser().getId().equals(testUser.getId())));
    }
    
    @Test
    void findByUserId_WithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 1);
        
        // When
        Page<Task> taskPage = taskRepository.findByUserId(testUser.getId(), pageable);
        
        // Then
        assertNotNull(taskPage);
        assertEquals(1, taskPage.getContent().size());
        assertEquals(2, taskPage.getTotalElements());
        assertEquals(2, taskPage.getTotalPages());
    }
    
    @Test
    void findByIdAndUserId_Success() {
        // When
        Optional<Task> task = taskRepository.findByIdAndUserId(testTask1.getId(), testUser.getId());
        
        // Then
        assertTrue(task.isPresent());
        assertEquals("Task 1", task.get().getTitle());
        assertEquals(testUser.getId(), task.get().getUser().getId());
    }
    
    @Test
    void findByIdAndUserId_NotFound() {
        // When
        Optional<Task> task = taskRepository.findByIdAndUserId(999L, testUser.getId());
        
        // Then
        assertFalse(task.isPresent());
    }
    
    @Test
    void findByStatusAndUserId_Success() {
        // When
        List<Task> pendingTasks = taskRepository.findByStatusAndUserId(TaskStatus.PENDING, testUser.getId());
        List<Task> completedTasks = taskRepository.findByStatusAndUserId(TaskStatus.COMPLETED, testUser.getId());
        
        // Then
        assertEquals(1, pendingTasks.size());
        assertEquals("Task 1", pendingTasks.get(0).getTitle());
        assertEquals(TaskStatus.PENDING, pendingTasks.get(0).getStatus());
        
        assertEquals(1, completedTasks.size());
        assertEquals("Task 2", completedTasks.get(0).getTitle());
        assertEquals(TaskStatus.COMPLETED, completedTasks.get(0).getStatus());
    }
    
    @Test
    void findByPriorityAndUserId_Success() {
        // When
        List<Task> highPriorityTasks = taskRepository.findByPriorityAndUserId(TaskPriority.HIGH, testUser.getId());
        List<Task> mediumPriorityTasks = taskRepository.findByPriorityAndUserId(TaskPriority.MEDIUM, testUser.getId());
        
        // Then
        assertEquals(1, highPriorityTasks.size());
        assertEquals("Task 1", highPriorityTasks.get(0).getTitle());
        assertEquals(TaskPriority.HIGH, highPriorityTasks.get(0).getPriority());
        
        assertEquals(1, mediumPriorityTasks.size());
        assertEquals("Task 2", mediumPriorityTasks.get(0).getTitle());
        assertEquals(TaskPriority.MEDIUM, mediumPriorityTasks.get(0).getPriority());
    }
    
    @Test
    void findByDueDateBeforeAndUserId_Success() {
        // When
        List<Task> overdueTasks = taskRepository.findByDueDateBeforeAndUserId(LocalDate.now(), testUser.getId());
        
        // Then
        assertEquals(1, overdueTasks.size());
        assertEquals("Task 2", overdueTasks.get(0).getTitle());
        assertTrue(overdueTasks.get(0).getDueDate().isBefore(LocalDate.now()));
    }
    
    @Test
    void findByDueDateAndUserId_Success() {
        // Given
        LocalDate specificDate = testTask1.getDueDate();
        
        // When
        List<Task> tasksOnDate = taskRepository.findByDueDateAndUserId(specificDate, testUser.getId());
        
        // Then
        assertEquals(1, tasksOnDate.size());
        assertEquals("Task 1", tasksOnDate.get(0).getTitle());
        assertEquals(specificDate, tasksOnDate.get(0).getDueDate());
    }
    
    @Test
    void searchByKeywordAndUserId_Success() {
        // When
        List<Task> tasksWithKeyword = taskRepository.searchByKeywordAndUserId("Task 1", testUser.getId());
        List<Task> tasksWithDescription = taskRepository.searchByKeywordAndUserId("Description", testUser.getId());
        
        // Then
        assertEquals(1, tasksWithKeyword.size());
        assertEquals("Task 1", tasksWithKeyword.get(0).getTitle());
        
        assertEquals(2, tasksWithDescription.size());
    }
    
    @Test
    void countByStatusAndUserId_Success() {
        // When
        long pendingCount = taskRepository.countByStatusAndUserId(TaskStatus.PENDING, testUser.getId());
        long completedCount = taskRepository.countByStatusAndUserId(TaskStatus.COMPLETED, testUser.getId());
        long inProgressCount = taskRepository.countByStatusAndUserId(TaskStatus.IN_PROGRESS, testUser.getId());
        
        // Then
        assertEquals(1, pendingCount);
        assertEquals(1, completedCount);
        assertEquals(0, inProgressCount);
    }
    
    @Test
    void countOverdueTasksByUserId_Success() {
        // When
        long overdueCount = taskRepository.countOverdueTasksByUserId(testUser.getId(), LocalDate.now());
        
        // Then
        assertEquals(0, overdueCount); // Task 2 is completed, so not counted as overdue
    }
    
    @Test
    void findTasksWithFilters_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When - Filter by status
        Page<Task> pendingTasks = taskRepository.findTasksWithFilters(
            testUser.getId(), TaskStatus.PENDING, null, null, null, pageable);
        
        // When - Filter by priority
        Page<Task> highPriorityTasks = taskRepository.findTasksWithFilters(
            testUser.getId(), null, TaskPriority.HIGH, null, null, pageable);
        
        // When - No filters
        Page<Task> allTasks = taskRepository.findTasksWithFilters(
            testUser.getId(), null, null, null, null, pageable);
        
        // Then
        assertEquals(1, pendingTasks.getContent().size());
        assertEquals(TaskStatus.PENDING, pendingTasks.getContent().get(0).getStatus());
        
        assertEquals(1, highPriorityTasks.getContent().size());
        assertEquals(TaskPriority.HIGH, highPriorityTasks.getContent().get(0).getPriority());
        
        assertEquals(2, allTasks.getContent().size());
    }
}

