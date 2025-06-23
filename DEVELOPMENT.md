# Development Guide

This guide provides detailed information for developers who want to contribute to or extend the Task Manager application.

## Table of Contents

1. [Development Environment Setup](#development-environment-setup)
2. [Project Structure](#project-structure)
3. [Coding Standards](#coding-standards)
4. [Testing Guidelines](#testing-guidelines)
5. [Git Workflow](#git-workflow)
6. [Adding New Features](#adding-new-features)
7. [Database Migrations](#database-migrations)
8. [API Development](#api-development)
9. [Frontend Development](#frontend-development)
10. [Performance Considerations](#performance-considerations)
11. [Security Guidelines](#security-guidelines)
12. [Debugging](#debugging)

## Development Environment Setup

### Prerequisites

- **Java 21** (OpenJDK recommended)
- **Maven 3.6+**
- **Node.js 18+** and **pnpm**
- **PostgreSQL 12+**
- **Git**
- **IDE**: IntelliJ IDEA, VS Code, or Eclipse

### IDE Configuration

#### IntelliJ IDEA Setup

1. **Import Project**
   ```
   File → Open → Select crud-app directory
   ```

2. **Configure Java SDK**
   ```
   File → Project Structure → Project → Project SDK → Java 21
   ```

3. **Install Plugins**
   - Spring Boot
   - Lombok
   - SonarLint
   - GitToolBox

4. **Code Style**
   ```
   File → Settings → Editor → Code Style → Java
   Import: google-java-format.xml
   ```

#### VS Code Setup

1. **Install Extensions**
   ```
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - ES7+ React/Redux/React-Native snippets
   - Prettier - Code formatter
   - ESLint
   ```

2. **Configure Settings**
   ```json
   {
     "java.home": "/path/to/java-21",
     "java.configuration.runtimes": [
       {
         "name": "JavaSE-21",
         "path": "/path/to/java-21"
       }
     ],
     "editor.formatOnSave": true,
     "editor.codeActionsOnSave": {
       "source.organizeImports": true
     }
   }
   ```

### Local Development Setup

```bash
# Clone repository
git clone <repository-url>
cd crud-app

# Set up environment
cp .env.example .env.local
# Edit .env.local with your configuration

# Database setup
createdb taskmanager_dev
psql -d taskmanager_dev -f database/schema.sql

# Backend setup
cd backend
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Frontend setup (in new terminal)
cd frontend/task-manager-frontend
pnpm install
pnpm run dev
```

## Project Structure

### Backend Structure

```
backend/
├── src/main/java/com/example/taskmanager/
│   ├── config/                 # Configuration classes
│   │   ├── SecurityConfig.java
│   │   ├── ApplicationConstants.java
│   │   └── DefaultProfileUtil.java
│   ├── controller/             # REST controllers
│   │   ├── AuthController.java
│   │   ├── TaskController.java
│   │   └── GlobalExceptionHandler.java
│   ├── dto/                    # Data Transfer Objects
│   │   ├── request/
│   │   └── response/
│   ├── entity/                 # JPA entities
│   │   ├── User.java
│   │   ├── Task.java
│   │   ├── TaskStatus.java
│   │   └── TaskPriority.java
│   ├── repository/             # Data repositories
│   │   ├── UserRepository.java
│   │   └── TaskRepository.java
│   ├── security/               # Security components
│   │   ├── JwtUtils.java
│   │   ├── UserPrincipal.java
│   │   └── AuthTokenFilter.java
│   ├── service/                # Business logic
│   │   ├── UserService.java
│   │   ├── TaskService.java
│   │   └── UserDetailsServiceImpl.java
│   └── TaskManagerApplication.java
├── src/main/resources/
│   ├── application.properties
│   ├── application-dev.properties
│   ├── application-test.properties
│   └── logback-spring.xml
└── src/test/java/              # Test classes
    ├── controller/
    ├── service/
    └── repository/
```

### Frontend Structure

```
frontend/task-manager-frontend/
├── src/
│   ├── components/             # React components
│   │   ├── ui/                # Reusable UI components
│   │   ├── Login.jsx
│   │   ├── Register.jsx
│   │   ├── Dashboard.jsx
│   │   ├── TaskForm.jsx
│   │   ├── TaskStats.jsx
│   │   └── ProtectedRoute.jsx
│   ├── hooks/                  # Custom React hooks
│   │   └── useAuth.jsx
│   ├── lib/                    # Utility functions
│   │   └── api.js
│   ├── __tests__/             # Test files
│   ├── App.jsx
│   ├── App.css
│   └── main.jsx
├── public/
├── package.json
├── vite.config.js
└── vitest.config.js
```

## Coding Standards

### Java Coding Standards

#### 1. Naming Conventions

```java
// Classes: PascalCase
public class TaskService { }

// Methods and variables: camelCase
public void createTask() { }
private String taskTitle;

// Constants: UPPER_SNAKE_CASE
private static final String DEFAULT_STATUS = "PENDING";

// Packages: lowercase with dots
package com.example.taskmanager.service;
```

#### 2. Code Organization

```java
// Class structure order:
public class TaskService {
    // 1. Static fields
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    
    // 2. Instance fields
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    
    // 3. Constructors
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }
    
    // 4. Public methods
    public TaskResponse createTask(TaskCreateRequest request, Long userId) {
        // Implementation
    }
    
    // 5. Private methods
    private Task mapToEntity(TaskCreateRequest request) {
        // Implementation
    }
}
```

#### 3. Documentation

```java
/**
 * Service class for managing task operations.
 * Provides CRUD operations and business logic for tasks.
 *
 * @author Your Name
 * @since 1.0
 */
@Service
@Transactional
public class TaskService {
    
    /**
     * Creates a new task for the specified user.
     *
     * @param request the task creation request containing task details
     * @param userId the ID of the user creating the task
     * @return TaskResponse containing the created task information
     * @throws UserNotFoundException if the user is not found
     * @throws ValidationException if the request data is invalid
     */
    public TaskResponse createTask(TaskCreateRequest request, Long userId) {
        // Implementation
    }
}
```

### JavaScript/React Coding Standards

#### 1. Component Structure

```jsx
// Functional component with hooks
import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';

/**
 * TaskForm component for creating and editing tasks.
 * 
 * @param {Object} props - Component props
 * @param {Object} props.task - Task object for editing (null for new task)
 * @param {Function} props.onSave - Callback when task is saved
 * @param {Function} props.onClose - Callback when form is closed
 */
const TaskForm = ({ task, onSave, onClose }) => {
  // State declarations
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    status: 'PENDING',
    priority: 'MEDIUM'
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Effects
  useEffect(() => {
    if (task) {
      setFormData({
        title: task.title || '',
        description: task.description || '',
        status: task.status || 'PENDING',
        priority: task.priority || 'MEDIUM'
      });
    }
  }, [task]);

  // Event handlers
  const handleSubmit = async (e) => {
    e.preventDefault();
    // Implementation
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  // Render
  return (
    <form onSubmit={handleSubmit}>
      {/* JSX content */}
    </form>
  );
};

export default TaskForm;
```

#### 2. Naming Conventions

```jsx
// Components: PascalCase
const TaskForm = () => { };

// Functions and variables: camelCase
const handleSubmit = () => { };
const isLoading = true;

// Constants: UPPER_SNAKE_CASE
const API_BASE_URL = 'http://localhost:8080/api';

// Files: kebab-case
// task-form.jsx, api-service.js
```

## Testing Guidelines

### Backend Testing

#### 1. Unit Tests

```java
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    
    @Mock
    private TaskRepository taskRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private TaskService taskService;
    
    @Test
    @DisplayName("Should create task successfully when valid request provided")
    void shouldCreateTaskSuccessfully() {
        // Given
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Test Task");
        request.setDescription("Test Description");
        
        User user = new User();
        user.setId(1L);
        
        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle("Test Task");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        
        // When
        TaskResponse response = taskService.createTask(request, 1L);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Test Task");
        verify(taskRepository).save(any(Task.class));
    }
}
```

#### 2. Integration Tests

```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
class TaskControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void shouldCreateTaskSuccessfully() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Integration Test Task");
        
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Integration Test Task"));
    }
}
```

### Frontend Testing

#### 1. Component Tests

```jsx
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { vi } from 'vitest';
import TaskForm from '../components/TaskForm';

describe('TaskForm', () => {
  const mockOnSave = vi.fn();
  const mockOnClose = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('renders form fields correctly', () => {
    render(<TaskForm onSave={mockOnSave} onClose={mockOnClose} />);
    
    expect(screen.getByLabelText('Title')).toBeInTheDocument();
    expect(screen.getByLabelText('Description')).toBeInTheDocument();
    expect(screen.getByLabelText('Status')).toBeInTheDocument();
    expect(screen.getByLabelText('Priority')).toBeInTheDocument();
  });

  test('calls onSave when form is submitted with valid data', async () => {
    render(<TaskForm onSave={mockOnSave} onClose={mockOnClose} />);
    
    fireEvent.change(screen.getByLabelText('Title'), {
      target: { value: 'Test Task' }
    });
    
    fireEvent.click(screen.getByRole('button', { name: 'Save' }));
    
    await waitFor(() => {
      expect(mockOnSave).toHaveBeenCalledWith({
        title: 'Test Task',
        description: '',
        status: 'PENDING',
        priority: 'MEDIUM'
      });
    });
  });
});
```

#### 2. API Tests

```jsx
import { vi } from 'vitest';
import apiService from '../lib/api';

// Mock fetch
global.fetch = vi.fn();

describe('API Service', () => {
  beforeEach(() => {
    fetch.mockClear();
  });

  test('creates task successfully', async () => {
    const mockTask = { id: 1, title: 'Test Task' };
    
    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({ success: true, data: mockTask })
    });

    const result = await apiService.createTask({
      title: 'Test Task',
      description: 'Test Description'
    });

    expect(result.success).toBe(true);
    expect(result.data).toEqual(mockTask);
    expect(fetch).toHaveBeenCalledWith(
      'http://localhost:8080/api/tasks',
      expect.objectContaining({
        method: 'POST',
        headers: expect.objectContaining({
          'Content-Type': 'application/json'
        }),
        body: JSON.stringify({
          title: 'Test Task',
          description: 'Test Description'
        })
      })
    );
  });
});
```

## Git Workflow

### Branch Naming

```bash
# Feature branches
feature/add-task-filtering
feature/implement-user-roles

# Bug fix branches
bugfix/fix-authentication-issue
bugfix/resolve-task-deletion-error

# Hotfix branches
hotfix/security-vulnerability
hotfix/critical-data-loss

# Release branches
release/v1.0.0
release/v1.1.0
```

### Commit Messages

Follow the Conventional Commits specification:

```bash
# Format: <type>[optional scope]: <description>

# Examples:
feat(auth): add JWT token refresh functionality
fix(tasks): resolve task deletion permission issue
docs(api): update authentication endpoint documentation
test(frontend): add unit tests for TaskForm component
refactor(backend): extract common validation logic
style(frontend): fix code formatting issues
chore(deps): update Spring Boot to version 3.2.1
```

### Pull Request Process

1. **Create Feature Branch**
   ```bash
   git checkout -b feature/add-task-filtering
   ```

2. **Make Changes and Commit**
   ```bash
   git add .
   git commit -m "feat(tasks): add filtering by status and priority"
   ```

3. **Push and Create PR**
   ```bash
   git push origin feature/add-task-filtering
   # Create PR through GitHub/GitLab interface
   ```

4. **PR Template**
   ```markdown
   ## Description
   Brief description of changes made.

   ## Type of Change
   - [ ] Bug fix
   - [ ] New feature
   - [ ] Breaking change
   - [ ] Documentation update

   ## Testing
   - [ ] Unit tests pass
   - [ ] Integration tests pass
   - [ ] Manual testing completed

   ## Checklist
   - [ ] Code follows style guidelines
   - [ ] Self-review completed
   - [ ] Documentation updated
   - [ ] No new warnings introduced
   ```

## Adding New Features

### Backend Feature Development

#### 1. Create Entity (if needed)

```java
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Constructors, getters, setters
}
```

#### 2. Create Repository

```java
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserIdOrderByNameAsc(Long userId);
    
    @Query("SELECT c FROM Category c WHERE c.user.id = :userId AND c.name ILIKE %:name%")
    List<Category> findByUserIdAndNameContainingIgnoreCase(@Param("userId") Long userId, @Param("name") String name);
}
```

#### 3. Create DTOs

```java
// Request DTO
public class CategoryCreateRequest {
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    // Getters and setters
}

// Response DTO
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    
    // Constructors, getters, setters
}
```

#### 4. Create Service

```java
@Service
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    
    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }
    
    public CategoryResponse createCategory(CategoryCreateRequest request, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setUser(user);
        
        Category savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory);
    }
    
    private CategoryResponse mapToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setCreatedAt(category.getCreatedAt());
        return response;
    }
}
```

#### 5. Create Controller

```java
@RestController
@RequestMapping("/api/categories")
@Validated
public class CategoryController {
    private final CategoryService categoryService;
    
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryCreateRequest request,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        CategoryResponse response = categoryService.createCategory(request, userPrincipal.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Category created successfully"));
    }
}
```

### Frontend Feature Development

#### 1. Create Component

```jsx
import React, { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';

const CategoryForm = ({ category, onSave, onCancel }) => {
  const [formData, setFormData] = useState({
    name: category?.name || '',
    description: category?.description || ''
  });
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrors({});

    try {
      await onSave(formData);
    } catch (error) {
      setErrors(error.errors || { general: error.message });
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    
    // Clear error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle>{category ? 'Edit Category' : 'New Category'}</CardTitle>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <Input
              name="name"
              placeholder="Category name"
              value={formData.name}
              onChange={handleChange}
              disabled={loading}
              required
            />
            {errors.name && (
              <p className="text-sm text-red-600 mt-1">{errors.name}</p>
            )}
          </div>
          
          <div>
            <Textarea
              name="description"
              placeholder="Category description (optional)"
              value={formData.description}
              onChange={handleChange}
              disabled={loading}
              rows={3}
            />
            {errors.description && (
              <p className="text-sm text-red-600 mt-1">{errors.description}</p>
            )}
          </div>

          {errors.general && (
            <p className="text-sm text-red-600">{errors.general}</p>
          )}

          <div className="flex gap-2">
            <Button type="submit" disabled={loading}>
              {loading ? 'Saving...' : 'Save'}
            </Button>
            <Button type="button" variant="outline" onClick={onCancel}>
              Cancel
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
};

export default CategoryForm;
```

#### 2. Add API Methods

```javascript
// In lib/api.js
const apiService = {
  // ... existing methods

  // Category methods
  async getCategories() {
    return this.request('/categories');
  },

  async createCategory(categoryData) {
    return this.request('/categories', {
      method: 'POST',
      body: JSON.stringify(categoryData)
    });
  },

  async updateCategory(id, categoryData) {
    return this.request(`/categories/${id}`, {
      method: 'PUT',
      body: JSON.stringify(categoryData)
    });
  },

  async deleteCategory(id) {
    return this.request(`/categories/${id}`, {
      method: 'DELETE'
    });
  }
};
```

#### 3. Add Tests

```jsx
// __tests__/CategoryForm.test.jsx
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { vi } from 'vitest';
import CategoryForm from '../components/CategoryForm';

describe('CategoryForm', () => {
  const mockOnSave = vi.fn();
  const mockOnCancel = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('renders form fields correctly', () => {
    render(<CategoryForm onSave={mockOnSave} onCancel={mockOnCancel} />);
    
    expect(screen.getByPlaceholderText('Category name')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Category description (optional)')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Save' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Cancel' })).toBeInTheDocument();
  });

  test('calls onSave with form data when submitted', async () => {
    render(<CategoryForm onSave={mockOnSave} onCancel={mockOnCancel} />);
    
    fireEvent.change(screen.getByPlaceholderText('Category name'), {
      target: { value: 'Work' }
    });
    
    fireEvent.change(screen.getByPlaceholderText('Category description (optional)'), {
      target: { value: 'Work-related tasks' }
    });
    
    fireEvent.click(screen.getByRole('button', { name: 'Save' }));
    
    await waitFor(() => {
      expect(mockOnSave).toHaveBeenCalledWith({
        name: 'Work',
        description: 'Work-related tasks'
      });
    });
  });
});
```

## Database Migrations

### Creating Migrations

```sql
-- V2__add_categories_table.sql
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_categories_user_id ON categories(user_id);
CREATE INDEX idx_categories_name ON categories(name);
```

### Migration Best Practices

1. **Always use transactions**
2. **Test migrations on copy of production data**
3. **Make migrations reversible when possible**
4. **Use descriptive names with version numbers**
5. **Document breaking changes**

## Performance Considerations

### Backend Optimization

#### 1. Database Queries

```java
// Use pagination for large datasets
@Query("SELECT t FROM Task t WHERE t.user.id = :userId")
Page<Task> findByUserId(@Param("userId") Long userId, Pageable pageable);

// Use projections for specific fields
@Query("SELECT new com.example.taskmanager.dto.TaskSummary(t.id, t.title, t.status) " +
       "FROM Task t WHERE t.user.id = :userId")
List<TaskSummary> findTaskSummariesByUserId(@Param("userId") Long userId);

// Use batch operations
@Modifying
@Query("UPDATE Task t SET t.status = :status WHERE t.id IN :ids")
int updateTaskStatusBatch(@Param("status") TaskStatus status, @Param("ids") List<Long> ids);
```

#### 2. Caching

```java
@Service
public class TaskService {
    
    @Cacheable(value = "taskStats", key = "#userId")
    public TaskStatistics getTaskStatistics(Long userId) {
        // Expensive calculation
        return calculateStatistics(userId);
    }
    
    @CacheEvict(value = "taskStats", key = "#userId")
    public TaskResponse createTask(TaskCreateRequest request, Long userId) {
        // Create task and evict cache
    }
}
```

### Frontend Optimization

#### 1. Component Optimization

```jsx
import React, { memo, useMemo, useCallback } from 'react';

const TaskList = memo(({ tasks, onTaskUpdate, onTaskDelete }) => {
  const sortedTasks = useMemo(() => {
    return tasks.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  }, [tasks]);

  const handleTaskUpdate = useCallback((taskId, updates) => {
    onTaskUpdate(taskId, updates);
  }, [onTaskUpdate]);

  return (
    <div>
      {sortedTasks.map(task => (
        <TaskItem
          key={task.id}
          task={task}
          onUpdate={handleTaskUpdate}
          onDelete={onTaskDelete}
        />
      ))}
    </div>
  );
});
```

#### 2. API Optimization

```javascript
// Implement request debouncing
import { debounce } from 'lodash';

const useTaskSearch = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [results, setResults] = useState([]);

  const debouncedSearch = useMemo(
    () => debounce(async (term) => {
      if (term) {
        const response = await apiService.searchTasks(term);
        setResults(response.data);
      } else {
        setResults([]);
      }
    }, 300),
    []
  );

  useEffect(() => {
    debouncedSearch(searchTerm);
    return () => debouncedSearch.cancel();
  }, [searchTerm, debouncedSearch]);

  return { searchTerm, setSearchTerm, results };
};
```

## Security Guidelines

### Backend Security

#### 1. Input Validation

```java
@PostMapping
public ResponseEntity<ApiResponse<TaskResponse>> createTask(
        @Valid @RequestBody TaskCreateRequest request,
        Authentication authentication) {
    
    // Additional custom validation
    if (request.getDueDate() != null && request.getDueDate().isBefore(LocalDate.now())) {
        throw new ValidationException("Due date cannot be in the past");
    }
    
    // Sanitize input
    String sanitizedTitle = StringEscapeUtils.escapeHtml4(request.getTitle());
    request.setTitle(sanitizedTitle);
    
    // Process request
}
```

#### 2. Authorization

```java
@PreAuthorize("@taskService.isTaskOwner(#taskId, authentication.principal.id)")
@DeleteMapping("/{taskId}")
public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long taskId) {
    taskService.deleteTask(taskId);
    return ResponseEntity.ok(ApiResponse.success(null, "Task deleted successfully"));
}
```

### Frontend Security

#### 1. XSS Prevention

```jsx
import DOMPurify from 'dompurify';

const TaskDescription = ({ description }) => {
  const sanitizedDescription = useMemo(() => {
    return DOMPurify.sanitize(description);
  }, [description]);

  return (
    <div dangerouslySetInnerHTML={{ __html: sanitizedDescription }} />
  );
};
```

#### 2. Secure API Calls

```javascript
const apiService = {
  async request(endpoint, options = {}) {
    const token = this.getAuthToken();
    
    const config = {
      headers: {
        'Content-Type': 'application/json',
        ...(token && { Authorization: `Bearer ${token}` }),
        ...options.headers
      },
      ...options
    };

    // Add CSRF token if available
    const csrfToken = document.querySelector('meta[name="csrf-token"]')?.content;
    if (csrfToken) {
      config.headers['X-CSRF-Token'] = csrfToken;
    }

    const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
    
    if (!response.ok) {
      if (response.status === 401) {
        this.removeAuthToken();
        window.location.href = '/login';
        return;
      }
      throw new Error(`API request failed: ${response.statusText}`);
    }

    return response.json();
  }
};
```

## Debugging

### Backend Debugging

#### 1. Logging Configuration

```xml
<!-- logback-spring.xml -->
<configuration>
    <springProfile name="dev">
        <logger name="com.example.taskmanager" level="DEBUG"/>
        <logger name="org.springframework.security" level="DEBUG"/>
        <logger name="org.hibernate.SQL" level="DEBUG"/>
        <logger name="org.hibernate.type.descriptor.sql.BasicBinder" level="TRACE"/>
    </springProfile>
</configuration>
```

#### 2. Debug Annotations

```java
@RestController
@Slf4j
public class TaskController {
    
    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody TaskCreateRequest request,
            Authentication authentication) {
        
        log.debug("Creating task with title: {}", request.getTitle());
        log.debug("User: {}", authentication.getName());
        
        try {
            TaskResponse response = taskService.createTask(request, getCurrentUserId(authentication));
            log.debug("Task created successfully with ID: {}", response.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Task created successfully"));
        } catch (Exception e) {
            log.error("Error creating task", e);
            throw e;
        }
    }
}
```

### Frontend Debugging

#### 1. Development Tools

```jsx
// Add debug information in development
const TaskForm = ({ task, onSave, onClose }) => {
  const [formData, setFormData] = useState(initialState);

  // Debug logging in development
  useEffect(() => {
    if (process.env.NODE_ENV === 'development') {
      console.log('TaskForm rendered with:', { task, formData });
    }
  }, [task, formData]);

  return (
    <form onSubmit={handleSubmit}>
      {/* Form content */}
      {process.env.NODE_ENV === 'development' && (
        <pre>{JSON.stringify(formData, null, 2)}</pre>
      )}
    </form>
  );
};
```

#### 2. Error Boundaries

```jsx
import React from 'react';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error('Error caught by boundary:', error, errorInfo);
    
    // Send error to monitoring service
    if (process.env.NODE_ENV === 'production') {
      // logErrorToService(error, errorInfo);
    }
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="error-boundary">
          <h2>Something went wrong.</h2>
          {process.env.NODE_ENV === 'development' && (
            <details>
              <summary>Error details</summary>
              <pre>{this.state.error?.toString()}</pre>
            </details>
          )}
        </div>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;
```

This development guide provides comprehensive information for developers working on the Task Manager application. Follow these guidelines to maintain code quality, consistency, and security throughout the development process.

