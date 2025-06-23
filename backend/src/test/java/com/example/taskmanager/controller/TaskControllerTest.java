package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskCreateRequest;
import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TaskService taskService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private TaskCreateRequest taskCreateRequest;
    
    @BeforeEach
    void setUp() {
        taskCreateRequest = new TaskCreateRequest();
        taskCreateRequest.setTitle("Test Task");
        taskCreateRequest.setDescription("Test Description");
        taskCreateRequest.setStatus(TaskStatus.PENDING);
        taskCreateRequest.setPriority(TaskPriority.MEDIUM);
        taskCreateRequest.setDueDate(LocalDate.now().plusDays(7));
    }
    
    @Test
    @WithMockUser
    void createTask_Success() throws Exception {
        // Given
        when(taskService.createTask(any(TaskCreateRequest.class), anyLong()))
            .thenReturn(null); // We'll mock a proper response in real implementation
        
        // When & Then
        mockMvc.perform(post("/tasks")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Task created successfully"));
        
        verify(taskService).createTask(any(TaskCreateRequest.class), anyLong());
    }
    
    @Test
    @WithMockUser
    void createTask_ValidationError() throws Exception {
        // Given
        TaskCreateRequest invalidRequest = new TaskCreateRequest();
        invalidRequest.setTitle(""); // Invalid: empty title
        
        // When & Then
        mockMvc.perform(post("/tasks")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.title").exists());
        
        verify(taskService, never()).createTask(any(TaskCreateRequest.class), anyLong());
    }
    
    @Test
    @WithMockUser
    void getAllTasks_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/tasks")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "createdAt")
                .param("sortDir", "desc"))
                .andExpected(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        verify(taskService).getTasksByUserWithPagination(anyLong(), any());
    }
    
    @Test
    @WithMockUser
    void getAllTasks_WithSearch() throws Exception {
        // When & Then
        mockMvc.perform(get("/tasks")
                .param("search", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        verify(taskService).searchTasks(eq("test"), anyLong());
    }
    
    @Test
    @WithMockUser
    void getAllTasks_WithStatusFilter() throws Exception {
        // When & Then
        mockMvc.perform(get("/tasks")
                .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        verify(taskService).getTasksByStatus(eq(TaskStatus.PENDING), anyLong());
    }
    
    @Test
    @WithMockUser
    void getTaskById_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        verify(taskService).getTaskById(eq(1L), anyLong());
    }
    
    @Test
    @WithMockUser
    void getTaskById_NotFound() throws Exception {
        // Given
        when(taskService.getTaskById(eq(1L), anyLong()))
            .thenThrow(new RuntimeException("Task not found"));
        
        // When & Then
        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Task not found: Task not found"));
        
        verify(taskService).getTaskById(eq(1L), anyLong());
    }
    
    @Test
    @WithMockUser
    void updateTask_Success() throws Exception {
        // When & Then
        mockMvc.perform(put("/tasks/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskCreateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Task updated successfully"));
        
        verify(taskService).updateTask(eq(1L), any(TaskCreateRequest.class), anyLong());
    }
    
    @Test
    @WithMockUser
    void updateTask_ValidationError() throws Exception {
        // Given
        TaskCreateRequest invalidRequest = new TaskCreateRequest();
        invalidRequest.setTitle(""); // Invalid: empty title
        
        // When & Then
        mockMvc.perform(put("/tasks/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"));
        
        verify(taskService, never()).updateTask(anyLong(), any(TaskCreateRequest.class), anyLong());
    }
    
    @Test
    @WithMockUser
    void deleteTask_Success() throws Exception {
        // When & Then
        mockMvc.perform(delete("/tasks/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Task deleted successfully"));
        
        verify(taskService).deleteTask(eq(1L), anyLong());
    }
    
    @Test
    @WithMockUser
    void deleteTask_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Task not found"))
            .when(taskService).deleteTask(eq(1L), anyLong());
        
        // When & Then
        mockMvc.perform(delete("/tasks/1")
                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to delete task: Task not found"));
        
        verify(taskService).deleteTask(eq(1L), anyLong());
    }
    
    @Test
    @WithMockUser
    void getOverdueTasks_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/tasks/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        verify(taskService).getOverdueTasks(anyLong());
    }
    
    @Test
    @WithMockUser
    void getTaskStatistics_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/tasks/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        verify(taskService).getTaskStatistics(anyLong());
    }
    
    @Test
    void createTask_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskCreateRequest)))
                .andExpect(status().isUnauthorized());
        
        verify(taskService, never()).createTask(any(TaskCreateRequest.class), anyLong());
    }
}

