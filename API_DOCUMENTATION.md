# API Documentation

## Overview

The Task Manager API is a RESTful web service that provides endpoints for managing tasks and user authentication. All endpoints return JSON responses and use standard HTTP status codes.

## Base URL

```
http://localhost:8080/api
```

## Authentication

The API uses JWT (JSON Web Token) for authentication. After successful login or registration, you'll receive a JWT token that must be included in the Authorization header for protected endpoints.

### Header Format

```
Authorization: Bearer <your-jwt-token>
```

## Response Format

### Success Response

```json
{
  "success": true,
  "data": { ... },
  "message": "Operation completed successfully"
}
```

### Error Response

```json
{
  "success": false,
  "message": "Error description",
  "errors": ["Detailed error messages"]
}
```

## Authentication Endpoints

### POST /auth/register

Register a new user account.

**Request Body:**

```json
{
  "username": "yassineabhir",
  "email": "yassine@example.com",
  "password": "password123",
  "firstName": "yassine",
  "lastName": "Abhir"
}
```

**Validation Rules:**

- `username`: Required, 3-50 characters, alphanumeric and underscore only
- `email`: Required, valid email format
- `password`: Required, minimum 6 characters
- `firstName`: Required, 1-50 characters
- `lastName`: Required, 1-50 characters

**Success Response (201):**

```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "yassineabhir",
      "email": "yassine@example.com",
      "firstName": "yassine",
      "lastName": "Abhir"
    }
  },
  "message": "User registered successfully"
}
```

**Error Response (400):**

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": ["Username already exists", "Email is already in use"]
}
```

### POST /auth/login

Authenticate user and receive JWT token.

**Request Body:**

```json
{
  "usernameOrEmail": "yassineabhir",
  "password": "password123"
}
```

**Success Response (200):**

```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "yassineabhir",
      "email": "yassine@example.com",
      "firstName": "yassine",
      "lastName": "Abhir"
    }
  },
  "message": "Login successful"
}
```

**Error Response (401):**

```json
{
  "success": false,
  "message": "Invalid credentials"
}
```

### GET /auth/profile

Get current user profile information.

**Headers:**

```
Authorization: Bearer <jwt-token>
```

**Success Response (200):**

```json
{
  "success": true,
  "data": {
    "id": 1,
    "username": "yassineabhir",
    "email": "yassine@example.com",
    "firstName": "yassine",
    "lastName": "Abhir",
    "createdAt": "2025-06-19T12:00:00Z"
  }
}
```

### POST /auth/logout

Logout user (invalidate token on server side).

**Headers:**

```
Authorization: Bearer <jwt-token>
```

**Success Response (200):**

```json
{
  "success": true,
  "message": "Logout successful"
}
```

## Task Endpoints

### GET /tasks

Retrieve all tasks for the authenticated user.

**Headers:**

```
Authorization: Bearer <jwt-token>
```

**Query Parameters:**

- `status` (optional): Filter by task status (PENDING, IN_PROGRESS, COMPLETED, CANCELLED)
- `priority` (optional): Filter by priority (LOW, MEDIUM, HIGH, URGENT)
- `search` (optional): Search in title and description
- `page` (optional): Page number for pagination (default: 0)
- `size` (optional): Page size (default: 20)
- `sort` (optional): Sort field and direction (e.g., "createdAt,desc")

**Example Request:**

```
GET /api/tasks?status=PENDING&priority=HIGH&search=project&page=0&size=10
```

**Success Response (200):**

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "title": "Complete project documentation",
        "description": "Write comprehensive README and API docs",
        "status": "PENDING",
        "priority": "HIGH",
        "dueDate": "2025-12-31",
        "createdAt": "2025-06-19T12:00:00Z",
        "updatedAt": "2025-06-19T12:00:00Z",
        "userId": 1
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0
  }
}
```

### GET /tasks/{id}

Retrieve a specific task by ID.

**Headers:**

```
Authorization: Bearer <jwt-token>
```

**Path Parameters:**

- `id`: Task ID (integer)

**Success Response (200):**

```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "Complete project documentation",
    "description": "Write comprehensive README and API docs",
    "status": "PENDING",
    "priority": "HIGH",
    "dueDate": "2025-12-31",
    "createdAt": "2025-06-19T12:00:00Z",
    "updatedAt": "2025-06-19T12:00:00Z",
    "userId": 1
  }
}
```

**Error Response (404):**

```json
{
  "success": false,
  "message": "Task not found"
}
```

### POST /tasks

Create a new task.

**Headers:**

```
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

**Request Body:**

```json
{
  "title": "Complete project documentation",
  "description": "Write comprehensive README and API docs",
  "status": "PENDING",
  "priority": "HIGH",
  "dueDate": "2025-12-31"
}
```

**Validation Rules:**

- `title`: Required, 1-200 characters
- `description`: Optional, max 1000 characters
- `status`: Required, must be one of: PENDING, IN_PROGRESS, COMPLETED, CANCELLED
- `priority`: Required, must be one of: LOW, MEDIUM, HIGH, URGENT
- `dueDate`: Optional, must be a valid date in YYYY-MM-DD format

**Success Response (201):**

```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "Complete project documentation",
    "description": "Write comprehensive README and API docs",
    "status": "PENDING",
    "priority": "HIGH",
    "dueDate": "2025-12-31",
    "createdAt": "2025-06-19T12:00:00Z",
    "updatedAt": "2025-06-19T12:00:00Z",
    "userId": 1
  },
  "message": "Task created successfully"
}
```

### PUT /tasks/{id}

Update an existing task.

**Headers:**

```
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

**Path Parameters:**

- `id`: Task ID (integer)

**Request Body:**

```json
{
  "title": "Updated task title",
  "description": "Updated description",
  "status": "IN_PROGRESS",
  "priority": "MEDIUM",
  "dueDate": "2025-12-31"
}
```

**Success Response (200):**

```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "Updated task title",
    "description": "Updated description",
    "status": "IN_PROGRESS",
    "priority": "MEDIUM",
    "dueDate": "2025-12-31",
    "createdAt": "2025-06-19T12:00:00Z",
    "updatedAt": "2025-06-19T13:00:00Z",
    "userId": 1
  },
  "message": "Task updated successfully"
}
```

### DELETE /tasks/{id}

Delete a task.

**Headers:**

```
Authorization: Bearer <jwt-token>
```

**Path Parameters:**

- `id`: Task ID (integer)

**Success Response (200):**

```json
{
  "success": true,
  "message": "Task deleted successfully"
}
```

### GET /tasks/statistics

Get task statistics for the authenticated user.

**Headers:**

```
Authorization: Bearer <jwt-token>
```

**Success Response (200):**

```json
{
  "success": true,
  "data": {
    "totalTasks": 25,
    "pendingTasks": 8,
    "inProgressTasks": 5,
    "completedTasks": 10,
    "cancelledTasks": 2,
    "overdueTasks": 3,
    "completionRate": 40.0,
    "tasksByPriority": {
      "LOW": 5,
      "MEDIUM": 10,
      "HIGH": 8,
      "URGENT": 2
    }
  }
}
```

### GET /tasks/overdue

Get all overdue tasks for the authenticated user.

**Headers:**

```
Authorization: Bearer <jwt-token>
```

**Success Response (200):**

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "title": "Overdue task",
      "description": "This task is past its due date",
      "status": "PENDING",
      "priority": "HIGH",
      "dueDate": "2025-06-15",
      "createdAt": "2025-06-10T12:00:00Z",
      "updatedAt": "2025-06-10T12:00:00Z",
      "userId": 1
    }
  ]
}
```

## Error Codes

| Status Code | Description                                      |
| ----------- | ------------------------------------------------ |
| 200         | OK - Request successful                          |
| 201         | Created - Resource created successfully          |
| 400         | Bad Request - Invalid request data               |
| 401         | Unauthorized - Invalid or missing authentication |
| 403         | Forbidden - Access denied                        |
| 404         | Not Found - Resource not found                   |
| 409         | Conflict - Resource already exists               |
| 422         | Unprocessable Entity - Validation failed         |
| 500         | Internal Server Error - Server error             |

## Rate Limiting

The API implements rate limiting to prevent abuse:

- 100 requests per minute per IP address
- 1000 requests per hour per authenticated user

When rate limit is exceeded, the API returns a 429 status code with retry information.

## Pagination

List endpoints support pagination using the following parameters:

- `page`: Page number (0-based, default: 0)
- `size`: Number of items per page (default: 20, max: 100)
- `sort`: Sort criteria in the format `property,direction` (e.g., "createdAt,desc")

## Date Formats

All dates in the API use ISO 8601 format:

- Date only: `YYYY-MM-DD` (e.g., "2025-12-31")
- Date and time: `YYYY-MM-DDTHH:mm:ssZ` (e.g., "2025-06-19T12:00:00Z")

## CORS

The API supports Cross-Origin Resource Sharing (CORS) for web applications. The following origins are allowed:

- `http://localhost:3000` (React development server)
- `http://localhost:5173` (Vite development server)

## Security

- All passwords are hashed using BCrypt
- JWT tokens expire after 24 hours
- API endpoints are protected against SQL injection
- Input validation is performed on all endpoints
- HTTPS is recommended for production deployments
