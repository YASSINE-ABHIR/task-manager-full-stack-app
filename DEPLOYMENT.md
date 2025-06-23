# Deployment Guide

This guide provides detailed instructions for deploying the Task Manager application in various environments.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Local Development](#local-development)
3. [Production Deployment](#production-deployment)
4. [Docker Deployment](#docker-deployment)
5. [Cloud Deployment](#cloud-deployment)
6. [Environment Configuration](#environment-configuration)
7. [Monitoring and Logging](#monitoring-and-logging)
8. [Troubleshooting](#troubleshooting)

## Prerequisites

### System Requirements

- **Java 21** or higher
- **Maven 3.6+**
- **Node.js 18+** and **pnpm**
- **PostgreSQL 12+**
- **Git** for version control

### Hardware Requirements

**Minimum (Development):**
- 4 GB RAM
- 2 CPU cores
- 10 GB disk space

**Recommended (Production):**
- 8 GB RAM
- 4 CPU cores
- 50 GB disk space
- SSD storage

## Local Development

### 1. Environment Setup

```bash
# Clone the repository
git clone <repository-url>
cd crud-app

# Set up environment variables
cp .env.example .env
# Edit .env with your local configuration
```

### 2. Database Setup

```bash
# Install PostgreSQL (Ubuntu/Debian)
sudo apt update
sudo apt install postgresql postgresql-contrib

# Create database and user
sudo -u postgres psql
CREATE DATABASE taskmanager;
CREATE USER taskmanager_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE taskmanager TO taskmanager_user;
\q

# Run database migrations
psql -U taskmanager_user -d taskmanager -f database/schema.sql
```

### 3. Backend Development

```bash
cd backend

# Configure application.properties
cat > src/main/resources/application-dev.properties << EOF
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanager
spring.datasource.username=postgres_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=validate
spring.profiles.active=dev
app.jwt.secret=dev-secret-key-change-in-production
app.jwt.expiration=86400000
logging.level.com.example.taskmanager=DEBUG
EOF

# Run in development mode
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 4. Frontend Development

```bash
cd frontend/task-manager-frontend

# Install dependencies
pnpm install

# Start development server
pnpm run dev
```

## Production Deployment

### 1. Server Preparation

```bash
# Update system packages
sudo apt update && sudo apt upgrade -y

# Install Java 21
sudo apt install openjdk-21-jdk

# Install PostgreSQL
sudo apt install postgresql postgresql-contrib

# Install Nginx (for reverse proxy)
sudo apt install nginx

# Install Node.js and pnpm
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install nodejs
npm install -g pnpm
```

### 2. Database Configuration

```bash
# Secure PostgreSQL installation
sudo postgresql-setup --initdb
sudo systemctl enable postgresql
sudo systemctl start postgresql

# Create production database
sudo -u postgres psql
CREATE DATABASE taskmanager_prod;
CREATE USER taskmanager_prod WITH PASSWORD 'strong_production_password';
GRANT ALL PRIVILEGES ON DATABASE taskmanager_prod TO taskmanager_prod;
\q

# Run migrations
psql -U taskmanager_prod -d taskmanager_prod -f database/schema.sql
```

### 3. Backend Production Build

```bash
cd backend

# Create production configuration
cat > src/main/resources/application-prod.properties << EOF
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanager_prod
spring.datasource.username=postgres_prod
spring.datasource.password=strong_production_password
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
server.port=8080
app.jwt.secret=very-long-and-secure-jwt-secret-key-for-production
app.jwt.expiration=86400000
logging.level.com.example.taskmanager=INFO
logging.file.name=/var/log/taskmanager/application.log
EOF

# Build production JAR
mvn clean package -Pprod

# Create application directory
sudo mkdir -p /opt/taskmanager
sudo cp target/task-manager-*.jar /opt/taskmanager/app.jar

# Create systemd service
sudo tee /etc/systemd/system/taskmanager.service << EOF
[Unit]
Description=Task Manager Application
After=network.target

[Service]
Type=simple
User=taskmanager
ExecStart=/usr/bin/java -jar /opt/taskmanager/app.jar --spring.profiles.active=prod
Restart=always
RestartSec=10
StandardOutput=syslog
StandardError=syslog
SyslogIdentifier=taskmanager

[Install]
WantedBy=multi-user.target
EOF

# Create application user
sudo useradd -r -s /bin/false taskmanager
sudo chown -R taskmanager:taskmanager /opt/taskmanager

# Start service
sudo systemctl daemon-reload
sudo systemctl enable taskmanager
sudo systemctl start taskmanager
```

### 4. Frontend Production Build

```bash
cd frontend/task-manager-frontend

# Build for production
pnpm run build

# Copy build files to web server
sudo mkdir -p /var/www/taskmanager
sudo cp -r dist/* /var/www/taskmanager/
sudo chown -R www-data:www-data /var/www/taskmanager
```

### 5. Nginx Configuration

```bash
# Create Nginx configuration
sudo tee /etc/nginx/sites-available/taskmanager << EOF
server {
    listen 80;
    server_name your-domain.com;

    # Frontend
    location / {
        root /var/www/taskmanager;
        index index.html;
        try_files \$uri \$uri/ /index.html;
    }

    # Backend API
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;
    add_header Content-Security-Policy "default-src 'self' http: https: data: blob: 'unsafe-inline'" always;
}
EOF

# Enable site
sudo ln -s /etc/nginx/sites-available/taskmanager /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

### 6. SSL Certificate (Let's Encrypt)

```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx

# Obtain SSL certificate
sudo certbot --nginx -d your-domain.com

# Auto-renewal
sudo crontab -e
# Add: 0 12 * * * /usr/bin/certbot renew --quiet
```

## Docker Deployment

### 1. Backend Dockerfile

```dockerfile
# backend/Dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY src ./src

# Install Maven
RUN apt-get update && apt-get install -y maven

# Build application
RUN mvn clean package -DskipTests

# Run application
EXPOSE 8080
CMD ["java", "-jar", "target/task-manager-0.0.1-SNAPSHOT.jar"]
```

### 2. Frontend Dockerfile

```dockerfile
# frontend/task-manager-frontend/Dockerfile
FROM node:18-alpine AS builder

WORKDIR /app
COPY package*.json ./
RUN npm install -g pnpm
RUN pnpm install

COPY . .
RUN pnpm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### 3. Docker Compose

```yaml
# docker-compose.yml
version: '3.8'

services:
  database:
    image: postgres:15
    environment:
      POSTGRES_DB: taskmanager
      POSTGRES_USER: taskmanager
      POSTGRES_PASSWORD: password
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./database/schema.sql:/docker-entrypoint-initdb.d/schema.sql
    ports:
      - "5432:5432"

  backend:
    build: ./backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://database:5432/taskmanager
      SPRING_DATASOURCE_USERNAME: taskmanager
      SPRING_DATASOURCE_PASSWORD: password
      SPRING_PROFILES_ACTIVE: docker
    depends_on:
      - database
    ports:
      - "8080:8080"

  frontend:
    build: ./frontend/task-manager-frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  postgres_data:
```

### 4. Deploy with Docker

```bash
# Build and start services
docker-compose up -d

# View logs
docker-compose logs -f

# Scale services
docker-compose up -d --scale backend=2

# Stop services
docker-compose down
```

## Cloud Deployment

### AWS Deployment

#### 1. RDS Database Setup

```bash
# Create RDS PostgreSQL instance
aws rds create-db-instance \
    --db-instance-identifier taskmanager-db \
    --db-instance-class db.t3.micro \
    --engine postgres \
    --master-username taskmanager \
    --master-user-password your-secure-password \
    --allocated-storage 20 \
    --vpc-security-group-ids sg-xxxxxxxxx
```

#### 2. Elastic Beanstalk Deployment

```bash
# Install EB CLI
pip install awsebcli

# Initialize EB application
cd backend
eb init taskmanager-api

# Create environment
eb create production

# Deploy
eb deploy
```

#### 3. S3 + CloudFront for Frontend

```bash
# Build frontend
cd frontend/task-manager-frontend
pnpm run build

# Upload to S3
aws s3 sync dist/ s3://your-bucket-name --delete

# Create CloudFront distribution
aws cloudfront create-distribution --distribution-config file://cloudfront-config.json
```

### Google Cloud Platform

#### 1. Cloud SQL Setup

```bash
# Create Cloud SQL instance
gcloud sql instances create taskmanager-db \
    --database-version=POSTGRES_13 \
    --tier=db-f1-micro \
    --region=us-central1

# Create database
gcloud sql databases create taskmanager --instance=taskmanager-db
```

#### 2. App Engine Deployment

```yaml
# backend/app.yaml
runtime: java21
env: standard

env_variables:
  SPRING_DATASOURCE_URL: jdbc:postgresql://google/taskmanager?cloudSqlInstance=project:region:taskmanager-db&socketFactory=com.google.cloud.sql.postgres.SocketFactory
  SPRING_DATASOURCE_USERNAME: taskmanager
  SPRING_DATASOURCE_PASSWORD: your-password
  SPRING_PROFILES_ACTIVE: gcp
```

```bash
# Deploy backend
cd backend
gcloud app deploy

# Deploy frontend to Firebase Hosting
cd frontend/task-manager-frontend
pnpm run build
firebase deploy
```

## Environment Configuration

### Environment Variables

Create `.env` files for different environments:

#### Development (.env.dev)
```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=taskmanager
DB_USER=taskmanager_user
DB_PASSWORD=dev_password

# JWT
JWT_SECRET=dev-secret-key
JWT_EXPIRATION=86400000

# API
API_BASE_URL=http://localhost:8080/api
```

#### Production (.env.prod)
```bash
# Database
DB_HOST=prod-db-host
DB_PORT=5432
DB_NAME=taskmanager_prod
DB_USER=taskmanager_prod
DB_PASSWORD=secure_prod_password

# JWT
JWT_SECRET=very-long-and-secure-production-jwt-secret
JWT_EXPIRATION=86400000

# API
API_BASE_URL=https://api.yourdomain.com/api
```

### Configuration Management

```bash
# Use environment-specific profiles
java -jar app.jar --spring.profiles.active=prod

# Override specific properties
java -jar app.jar --server.port=9090 --spring.datasource.url=jdbc:postgresql://new-host:5432/db
```

## Monitoring and Logging

### Application Monitoring

#### 1. Health Checks

```bash
# Backend health check
curl http://localhost:8080/actuator/health

# Custom health endpoint
curl http://localhost:8080/api/health
```

#### 2. Metrics Collection

```yaml
# Add to application.properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
management.metrics.export.prometheus.enabled=true
```

#### 3. Log Configuration

```xml
<!-- logback-spring.xml -->
<configuration>
    <springProfile name="prod">
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>/var/log/taskmanager/application.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>/var/log/taskmanager/application.%d{yyyy-MM-dd}.log</fileNamePattern>
                <maxHistory>30</maxHistory>
            </rollingPolicy>
            <encoder>
                <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="INFO">
            <appender-ref ref="FILE" />
        </root>
    </springProfile>
</configuration>
```

### External Monitoring Tools

#### Prometheus + Grafana

```yaml
# docker-compose.monitoring.yml
version: '3.8'

services:
  prometheus:
    image: prom/prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml

  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
```

## Troubleshooting

### Common Issues

#### 1. Database Connection Issues

```bash
# Check database status
sudo systemctl status postgresql

# Test connection
psql -U taskmanager -d taskmanager -h localhost -p 5432

# Check logs
sudo tail -f /var/log/postgresql/postgresql-*.log
```

#### 2. Application Won't Start

```bash
# Check Java version
java -version

# Check application logs
sudo journalctl -u taskmanager -f

# Check port availability
sudo netstat -tlnp | grep :8080
```

#### 3. Frontend Build Issues

```bash
# Clear cache
pnpm cache clean

# Reinstall dependencies
rm -rf node_modules package-lock.json
pnpm install

# Check Node.js version
node --version
```

#### 4. SSL Certificate Issues

```bash
# Check certificate status
sudo certbot certificates

# Renew certificate
sudo certbot renew --dry-run

# Check Nginx configuration
sudo nginx -t
```

### Performance Optimization

#### 1. Database Optimization

```sql
-- Add indexes for frequently queried columns
CREATE INDEX idx_tasks_user_id ON tasks(user_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);

-- Analyze query performance
EXPLAIN ANALYZE SELECT * FROM tasks WHERE user_id = 1 AND status = 'PENDING';
```

#### 2. Application Optimization

```properties
# JVM tuning
JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Connection pool tuning
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
```

#### 3. Frontend Optimization

```bash
# Enable gzip compression in Nginx
gzip on;
gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;

# Set cache headers
location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

### Backup and Recovery

#### 1. Database Backup

```bash
# Create backup
pg_dump -U taskmanager -h localhost taskmanager > backup_$(date +%Y%m%d_%H%M%S).sql

# Automated backup script
#!/bin/bash
BACKUP_DIR="/var/backups/taskmanager"
DATE=$(date +%Y%m%d_%H%M%S)
pg_dump -U taskmanager taskmanager | gzip > $BACKUP_DIR/backup_$DATE.sql.gz
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +7 -delete
```

#### 2. Application Backup

```bash
# Backup application files
tar -czf taskmanager_backup_$(date +%Y%m%d).tar.gz /opt/taskmanager /var/www/taskmanager

# Backup configuration
cp /etc/nginx/sites-available/taskmanager /backup/nginx_config_$(date +%Y%m%d)
```

This deployment guide covers various scenarios from local development to production cloud deployments. Choose the appropriate section based on your deployment needs and environment.

