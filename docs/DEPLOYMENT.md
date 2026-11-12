# Free Cloud Web Deployment & Production Guide: Bule Hora Elementary School

## 1. Overview
The **Bule Hora Elementary School Management System** is packaged as both a standalone web application runner (`com.school.runner.EmbeddedServer`) and a standard Java `WAR` file (`target/StudentRegistrationApp.war`). This dual capability allows instantaneous deployment on conventional Apache Tomcat servers as well as modern containerized free-tier cloud platforms (Render, Railway, Fly.io).

---

## 2. 1-Click & Free-Tier Hosting Options

### Option A: Render + TiDB Cloud Serverless (Recommended Production Setup)
1. Fork or push your code to your GitHub account: `https://github.com/petersisay/student-registration-system`.
2. Sign in to [Render.com](https://render.com) (free account).
3. Click **New +** -> **Web Service** -> connect your GitHub repository.
4. Render automatically reads `Dockerfile` and builds the production container.
5. Under **Environment Variables**, configure your database connection via TiDB Cloud Serverless or direct MySQL:
   - `DATABASE_URL`: `mysql://<user>:<password>@gateway01.<region>.prod.aws.tidbcloud.com:4000/test`
   *(Or individually: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`)*
6. Click **Deploy**. The system will build, run automated database migrations, seed default accounts, and launch live.

### Option B: Railway.app (Free Automatic Cloud Deployment)
1. Sign in to [Railway.app](https://railway.app).
2. Click **New Project** -> **Deploy from GitHub repo** -> select `student-registration-system`.
3. Add a free **MySQL** database from the Railway canvas.
4. Railway automatically provides `MYSQL_URL` and `DATABASE_URL` which `DBConnection.java` detects out of the box with zero configuration!

### Option C: Containerized Deployment (Docker / Fly.io / AWS ECS)
Build and run the production container locally or on any cloud server:
```bash
docker build -t bule-hora-school:latest .
docker run -p 8080:8080 -e DB_HOST=host.docker.internal -e DB_USER=root -e DB_PASSWORD="" bule-hora-school:latest
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

## 4. Default Accounts & Production Credential Provisioning

### 4.1. Development Fixtures (Built-In Test Accounts)
The automated migration initializes these accounts for immediate testing:

| Role | Username | Password | Purpose |
|---|---|---|---|
| **Administrator** | `admin` | `admin123` | Complete school management, user registration & password resets |
| **Principal** | `principal` | `principal123` | Academic supervision & report card approval |
| **Teacher** | `teacher.abebe` | `teacher123` | Assigned classes, attendance, and mark entry |
| **Registrar** | `registrar` | `registrar123` | Student admissions & multi-year progression |
| **Attendance Officer** | `attendance` | `attendance123` | Daily school-wide attendance recording |
| **Student** | `student.dawit` | `student123` | Child-friendly student portal & personal report card |

### 4.2. Production User Provisioning Workflow
In a live school environment:
1. Log in with the initial `admin` account.
2. Go to **Users (`/admin/users`)** or **Teachers (`/admin/teachers`)**.
3. Create accounts for each faculty member and administrative officer with custom usernames and passwords.
4. When teachers and staff log in, they use their own assigned credentials at `/login`.
5. Passwords can be updated or reset at any time by the administrator.
