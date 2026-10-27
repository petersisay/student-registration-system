# Free Cloud Web Deployment Guide

## 1. Overview
The **Ethiopian Elementary School Management System** is packaged as both a standalone web application runner (`com.school.runner.EmbeddedServer`) and a standard `WAR` file (`target/StudentRegistrationApp.war`). This dual capability allows deployment on conventional Apache Tomcat servers as well as modern containerized platforms.

---

## 2. Recommended Free-Tier Hosting Options

### Option A: Render (Web Service + Managed Cloud MySQL)
- **Application**: Render Web Service (Free Tier)
  - Environment: Docker or Java native.
  - Build Command: `./mvnw clean package`
  - Start Command: `java -Dserver.port=$PORT -cp target/classes:target/dependency/* com.school.runner.EmbeddedServer`
  - Free Limitations: Spins down after 15 minutes of inactivity; spins up automatically upon incoming HTTP request.
- **Database**:
  - Free Cloud MySQL: **Aiven for MySQL** (Free Tier with 5GB storage) or **Clever Cloud MySQL** (Free 10MB starter).
  - Environment variables configured on Render:
    - `DB_URL`: `jdbc:mysql://<host>:<port>/<dbname>?useSSL=true&characterEncoding=UTF-8`
    - `DB_USER`: `<username>`
    - `DB_PASSWORD`: `<password>`

### Option B: Railway.app
- Supports direct GitHub repository integration.
- Automatically provisions a free MySQL instance and attaches `MYSQLHOST`, `MYSQLPORT`, `MYSQLUSER`, `MYSQLPASSWORD`, `MYSQLDATABASE`.
- Zero configuration needed: `DBConnection.java` detects standard environment variables automatically.

### Option C: Fly.io
- Deploy via Dockerfile:
```dockerfile
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/StudentRegistrationApp.war /app/app.war
COPY --from=build /app/target/classes /app/classes
COPY --from=build /app/target/dependency /app/dependency
ENV PORT=8080
EXPOSE 8080
CMD ["java", "-cp", "/app/classes:/app/dependency/*", "com.school.runner.EmbeddedServer"]
```

---

## 3. Environment Variables Configuration

| Variable | Default (Local) | Production Example | Description |
|---|---|---|---|
| `DB_HOST` | `localhost` | `mysql-3a21.aivencloud.com` | Hostname of remote MySQL database |
| `DB_PORT` | `3306` | `24582` | Port of MySQL server |
| `DB_NAME` | `student_db` | `defaultdb` | Schema name |
| `DB_USER` | `root` | `avnadmin` | Database username |
| `DB_PASSWORD`| *(empty)* | `secret_password` | Database password |
| `PORT` | `8080` | `10000` | HTTP port provided by cloud host |

---

## 4. Local Quick Start & Testing

1. Ensure MySQL is running on `localhost:3306`.
2. Start the embedded server with 1 command:
   ```powershell
   .\mvnw.cmd compile exec:java
   ```
3. Open your browser at `http://localhost:8080` (or `http://localhost:8080/login`).
4. Default login accounts:
   - **Administrator**: `admin` / `Admin@123`
   - **Teacher**: `teacher` / `Teacher@123`
   - **Principal**: `principal` / `Principal@123`
   - **Student**: `student` / `Student@123`
