# Task Manager - Full-Stack CRUD Application

A comprehensive task management application built with Java Spring Boot backend and React.js frontend, featuring JWT authentication, unit tests, and modern web development practices.

## 🚀 Features

- **Full CRUD Operations**: Create, read, update, and delete tasks
- **JWT Authentication**: Secure user registration and login
- **Responsive Design**: Modern UI that works on desktop and mobile
- **Real-time Statistics**: Task completion tracking and analytics
- **Search & Filter**: Find tasks by title, status, and priority
- **Input Validation**: Comprehensive form validation on both frontend and backend
- **Unit Testing**: Extensive test coverage for both frontend and backend
- **RESTful API**: Well-designed API endpoints following REST principles

## 🏗️ Architecture

### Backend (Java Spring Boot)

- **Framework**: Spring Boot 3.2.0 with Java 21
- **Database**: PostgreSQL with JPA/Hibernate
- **Security**: Spring Security with JWT tokens
- **Testing**: JUnit 5 with MockMvc for integration tests
- **Build Tool**: Maven

### Frontend (React.js)

- **Framework**: React 19 with functional components and hooks
- **Routing**: React Router DOM for navigation
- **Styling**: Tailwind CSS with shadcn/ui components
- **Icons**: Lucide React icons
- **Testing**: Vitest with React Testing Library
- **Build Tool**: Vite

## 📋 Prerequisites

Before running this application, make sure you have the following installed:

- **Java 21** or higher
- **Maven 3.6+**
- **Node.js 18+** and **pnpm**
- **PostgreSQL 12+**

## 🛠️ Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd crud-app
```

### 2. Database Setup

Create a PostgreSQL database and run the schema script:

```sql
-- Connect to PostgreSQL and create database
CREATE DATABASE taskmanager;

-- Run the schema script
\i database/schema.sql
```

### 3. Backend Setup

```bash
cd backend

# Configure database connection in src/main/resources/application.properties
# Update the following properties:
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanager
spring.datasource.username=your_username
spring.datasource.password=your_password

# Compile and run tests
mvn clean compile
mvn test

# Run the application
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 4. Frontend Setup

```bash
cd frontend/task-manager-frontend

# Install dependencies with (npm)
pnpm install

# Run tests
pnpm test

# Start development server
pnpm run dev
```

The frontend will start on `http://localhost:5173`

## 🔧 Configuration

### Backend Configuration

Key configuration files:

- `src/main/resources/application.properties` - Main application configuration
- `src/main/resources/application-test.properties` - Test environment configuration

Important settings:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanager
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT Configuration
app.jwt.secret=your-secret-key
app.jwt.expiration=86400000

# Server Configuration
server.port=8080
```

### Frontend Configuration

The frontend is configured to connect to the backend at `http://localhost:8080`. To change this, update the `API_BASE_URL` in `src/lib/api.js`.

## 📚 API Documentation

### Authentication Endpoints

#### Register User

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "yassineabhir",
  "email": "yassine@example.com",
  "password": "password123",
  "firstName": "yassine",
  "lastName": "Abhir"
}
```

#### Login User

```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "yassineabhir",
  "password": "password123"
}
```

#### Get Current User Profile

```http
GET /api/auth/profile
Authorization: Bearer <jwt-token>
```

### Task Endpoints

#### Get All Tasks

```http
GET /api/tasks
Authorization: Bearer <jwt-token>

# Optional query parameters:
# ?status=PENDING&priority=HIGH&search=keyword
```

#### Get Task by ID

```http
GET /api/tasks/{id}
Authorization: Bearer <jwt-token>
```

#### Create Task

```http
POST /api/tasks
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "title": "Complete project documentation",
  "description": "Write comprehensive README and API docs",
  "status": "PENDING",
  "priority": "HIGH",
  "dueDate": "2025-12-31"
}
```

#### Update Task

```http
PUT /api/tasks/{id}
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "title": "Updated task title",
  "description": "Updated description",
  "status": "IN_PROGRESS",
  "priority": "MEDIUM",
  "dueDate": "2025-12-31"
}
```

#### Delete Task

```http
DELETE /api/tasks/{id}
Authorization: Bearer <jwt-token>
```

#### Get Task Statistics

```http
GET /api/tasks/statistics
Authorization: Bearer <jwt-token>
```

#### Get Overdue Tasks

```http
GET /api/tasks/overdue
Authorization: Bearer <jwt-token>
```

### Data Models

#### Task Model

```json
{
  "id": 1,
  "title": "Task title",
  "description": "Task description",
  "status": "PENDING|IN_PROGRESS|COMPLETED|CANCELLED",
  "priority": "LOW|MEDIUM|HIGH|URGENT",
  "dueDate": "2025-12-31",
  "createdAt": "2025-06-19T12:00:00Z",
  "updatedAt": "2025-06-19T12:00:00Z",
  "userId": 1
}
```

#### User Model

```json
{
  "id": 1,
  "username": "yassineabhir",
  "email": "yassine@example.com",
  "firstName": "yassine",
  "lastName": "Abhir",
  "createdAt": "2025-06-19T12:00:00Z"
}
```

## 🧪 Testing

### Backend Tests

Run all backend tests:

```bash
cd backend
mvn test
```

Test categories:

- **Unit Tests**: Service layer business logic
- **Integration Tests**: Controller endpoints with MockMvc
- **Repository Tests**: Data access layer with test database

### Frontend Tests

Run all frontend tests:

```bash
cd frontend/task-manager-frontend
pnpm test
```

Test categories:

- **Component Tests**: React component rendering and interactions
- **Hook Tests**: Custom React hooks functionality
- **API Tests**: Service layer API calls and error handling

## 🚀 Deployment

### Backend Deployment

1. Build the application:

```bash
mvn clean package
```

2. Run the JAR file:

```bash
java -jar target/task-manager-0.0.1-SNAPSHOT.jar
```

### Frontend Deployment

1. Build for production:

```bash
pnpm run build
```

2. Serve the built files from the `dist` directory using any static file server.

## 🔒 Security Features

- **JWT Authentication**: Stateless authentication with secure token generation
- **Password Hashing**: BCrypt encryption for user passwords
- **CORS Configuration**: Proper cross-origin resource sharing setup
- **Input Validation**: Server-side validation for all API endpoints
- **SQL Injection Prevention**: JPA/Hibernate parameterized queries
- **XSS Protection**: React's built-in XSS protection

## 🎨 UI/UX Features

- **Responsive Design**: Mobile-first approach with Tailwind CSS
- **Modern Components**: shadcn/ui component library
- **Loading States**: Proper loading indicators for async operations
- **Error Handling**: User-friendly error messages and validation
- **Accessibility**: ARIA labels and keyboard navigation support
- **Dark Mode Ready**: CSS variables for easy theme switching

## Docker Setup

### Requirements:

- Docker 20.10+
- Docker Compose 2.0+

### Run Application:

```bash
docker-compose up --build -d
```

## 🐳 Docker Deployment

### Prerequisites

- Docker installed on your system

### Quick Start

```bash
# Clone the repository
git clone https://github.com/YASSINE-ABHIR/task-manager-full-stack-app.git
cd task-manager-full-stack-app

# Build and start containers
docker-compose up --build -d
```

## 📁 Project Structure

```
crud-app/
├── backend/                    # Spring Boot backend
│   ├── src/main/java/
│   │   └── com/example/taskmanager/
│   │       ├── config/         # Configuration classes
│   │       ├── controller/     # REST controllers
│   │       ├── dto/           # Data transfer objects
│   │       ├── entity/        # JPA entities
│   │       ├── repository/    # Data repositories
│   │       ├── security/      # Security configuration
│   │       └── service/       # Business logic
│   ├── src/test/java/         # Test classes
│   ├── Dockerfile             # Backend Dockerfile
│   └── pom.xml               # Maven configuration
├── frontend/task-manager-frontend/  # React frontend
│   ├── src/
│   │   ├── components/        # React components
│   │   ├── hooks/            # Custom React hooks
│   │   ├── lib/              # Utility functions
│   │   └── __tests__/        # Test files
│   ├── Dockerfile             # Frontend Dockerfile
│   ├── nginx.conf             # Nginx config
│   └── package.json          # npm configuration
├── database/
│   └── schema.sql            # Database schema
├── README.md                  # This file
├── API_DOCUMENTATION.md       # API reference
├── DEPLOYMENT.md              # Deployment guide
├── docker-compose.yml         # Multi-container setup
└── DEVELOPMENT.md             # Developer guide

```

## 🎥 Application Demo

[Download Demo Video](docs/demo.mp4)
(click in View raw)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- React team for the powerful frontend library
- shadcn/ui for the beautiful component library
- Tailwind CSS for the utility-first CSS framework

## 📞 Support

If you have any questions or need help with setup, please open an issue in the repository.

---

**Built with ❤️ using Java Spring Boot and React.js**
