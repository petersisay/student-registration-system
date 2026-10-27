# Multi-stage Dockerfile for free cloud deployment (Render, Fly.io, Railway, etc.)
# Stage 1: Build application and package dependencies
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /workspace

# Copy Maven wrapper and POM
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Copy source code and build
COPY src/ src/
RUN ./mvnw clean package -DskipTests

# Stage 2: Minimal production runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy compiled classes, dependencies, and webapp resources
COPY --from=builder /workspace/target/classes /app/target/classes
COPY --from=builder /workspace/target/dependency /app/target/dependency
COPY --from=builder /workspace/src/main/webapp /app/src/main/webapp

# Default environment variables for free cloud hosting
ENV PORT=8080
ENV DB_HOST=localhost
ENV DB_PORT=3306
ENV DB_NAME=student_db
ENV DB_USER=root
ENV DB_PASSWORD=

EXPOSE 8080

CMD ["java", "-cp", "target/classes:target/dependency/*", "com.school.runner.EmbeddedServer"]
