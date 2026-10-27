# Free Cloud Web Deployment Guide

## 1. Overview
The **Bule Hora Elementary School Management System** is packaged as both a standalone web application runner (`com.school.runner.EmbeddedServer`) and a standard `WAR` file (`target/StudentRegistrationApp.war`). This dual capability allows instantaneous deployment on conventional Apache Tomcat servers as well as modern containerized free-tier cloud platforms (Render, Railway, Fly.io).

---

## 2. 1-Click & Free-Tier Hosting Options

### Option A: Render (Free Web Service)
1. Fork or push your code to your GitHub account: `https://github.com/petersisay/student-registration-system`.
2. Sign in to [Render.com](https://render.com) (free account).
3. Click **New +** -> **Blueprint** or **Web Service**.
4. Select this repository. Render automatically reads `render.yaml` and `Dockerfile`.
5. Under Environment Variables, configure your free MySQL database credentials:
   - `DB_URL`: `jdbc:mysql://<host>:<port>/<dbname>?useSSL=true&characterEncoding=UTF-8`
   - `DB_USER`: `<username>`
   - `DB_PASSWORD`: `<password>`
6. Click **Deploy**. The system will build and launch at `https://bule-hora-elementary-school.onrender.com`.

### Option B: Railway.app (Free Automatic Cloud Deployment)
1. Sign in to [Railway.app](https://railway.app).
2. Click **New Project** -> **Deploy from GitHub repo** -> select `student-registration-system`.
3. Add a free **MySQL** database from the Railway canvas.
4. Railway automatically provides `MYSQL_URL` and `DATABASE_URL` which `DBConnection.java` detects out of the box with zero configuration!

### Option C: Containerized Deployment (Docker / Fly.io / AWS ECS)
Build and run the production container locally or on any cloud server:
```bash
docker build -t bule-hora-school:latest .
docker run -p 8080:8080 -e DB_HOST=host.docker.internal -e DB_USER=root bule-hora-school:latest
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
