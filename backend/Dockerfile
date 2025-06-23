# Build stage
FROM maven:3.8.6-openjdk-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:21-jdk-slim
COPY --from=build /app/target/*.jar app.jar

# Create properties file
RUN echo "spring.profiles.active=docker" > application-docker.properties
RUN echo "spring.datasource.url=jdbc:postgresql://db:5432/taskmanager" >> application-docker.properties
RUN echo "spring.datasource.username=postgres" >> application-docker.properties
RUN echo "spring.datasource.password=12354" >> application-docker.properties
RUN echo "spring.jpa.hibernate.ddl-auto=update" >> application-docker.properties
RUN echo "server.port=8080" >> application-docker.properties
RUN echo "server.servlet.context-path=/api" >> application-docker.properties
RUN echo "app.jwt.secret=mySecretKey123456789012345678901234567890" >> application-docker.properties
RUN echo "app.jwt.expiration=86400000" >> application-docker.properties

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.config.location=classpath:/application-docker.properties"]