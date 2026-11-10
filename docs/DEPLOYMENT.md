# Free Cloud Web Deployment & Production Guide: Bule Hora Elementary School

## 1. Overview
The **Bule Hora Elementary School Management System** is packaged as both a standalone web application runner (`com.school.runner.EmbeddedServer`) and a standard Java `WAR` file (`target/StudentRegistrationApp.war`). This dual capability allows instantaneous deployment on conventional Apache Tomcat servers as well as modern containerized free-tier cloud platforms (Render, Railway, Fly.io).

---

## 2. Cloud & Container Hosting Options

### Option A: Render (Web Service)
1. Fork or push your code to your GitHub account repository.
2. Sign in to [Render.com](https://render.com).
3. Click **New +** -> **Blueprint** or **Web Service**.
4. Select this repository. Render automatically reads `render.yaml` and `Dockerfile`.
5. Under Environment Variables in the Render dashboard, configure your database credentials securely:
   - `DATABASE_URL`: Full connection URI, or:
   - `DB_HOST`: Hostname of remote MySQL database
   - `DB_PORT`: `3306` (or custom host port)
   - `DB_NAME`: Database schema name
   - `DB_USER`: Remote database user
   - `DB_PASSWORD`: Remote database password
   - `INITIAL_ADMIN_PASSWORD`: Your chosen secure administrator password
   - `SEED_DEFAULT_ACCOUNTS`: Set to `false` for hardened production
6. Click **Deploy**. The system builds and launches automatically.

### Option B: Railway.app (Automatic Cloud Deployment)
1. Sign in to [Railway.app](https://railway.app).
2. Click **New Project** -> **Deploy from GitHub repo** -> select your repository.
3. Add a **MySQL** database service from the Railway canvas.
4. Railway automatically injects `DATABASE_URL` / `MYSQL_URL`, which `DBConnection.java` detects out of the box with zero manual configuration.
5. In the service's **Variables** tab, set `INITIAL_ADMIN_PASSWORD` and optionally `SEED_DEFAULT_ACCOUNTS=false`.

### Option C: Containerized Deployment (Docker / Fly.io / AWS ECS)
Build and run the production container locally or on any cloud server using environment files:
```bash
# Build the production container
docker build -t bule-hora-school:latest .

# Run with environment variables loaded securely from .env
docker run -p 8080:8080 --env-file .env bule-hora-school:latest
```

---

## 3. Environment Variables Configuration

| Variable | Default (Local) | Production Example | Description |
|---|---|---|---|
| `PORT` | `8080` | `10000` | HTTP port provided by cloud host |
| `DATABASE_URL` | *(empty)* | `mysql://user:pass@host:3306/db` | Unified connection string (preferred for cloud) |
| `DB_HOST` | `localhost` | `db.example.com` | Hostname of MySQL database server |
| `DB_PORT` | `3306` | `3306` | Port of MySQL server |
| `DB_NAME` | `student_db` | `school_production_db` | Schema name |
| `DB_USER` | `root` | `app_user` | Database username |
| `DB_PASSWORD`| *(empty)* | `<strong_random_password>` | Database password |
| `INITIAL_ADMIN_USERNAME` | `admin` | `admin` | Initial admin account username |
| `INITIAL_ADMIN_PASSWORD` | `admin123` | `<strong_admin_password>` | Initial admin account password |
| `SEED_DEFAULT_ACCOUNTS` | `true` | `false` | Whether to seed sample demo accounts on migration |

> [!IMPORTANT]
> Never hardcode passwords or API keys in source files or Dockerfiles. Store them in `.env` locally (which is git-ignored) or in your hosting provider's secrets manager.

---

## 4. Secure Account Provisioning & Credential Workflow

### 4.1. Initial Administrator Setup
When the application first boots against a new database:
1. It applies schema migrations (`V1__init_schema.sql` and `V2__seed_ethiopian_elementary_data.sql`).
2. It provisions the primary administrator account using the username and password specified by `INITIAL_ADMIN_USERNAME` and `INITIAL_ADMIN_PASSWORD`.
3. Passwords are never stored in plaintext: they are hashed using **PBKDF2WithHmacSHA256** (65,536 iterations) with a cryptographically generated 16-byte random salt.

### 4.2. Demonstration Accounts (Local Development)
For local development and testing, setting `SEED_DEFAULT_ACCOUNTS=true` initializes sample accounts for each role (Principal, Registrar, Teacher, Attendance Officer, Student). In production environments, set `SEED_DEFAULT_ACCOUNTS=false` to ensure only the authorized initial administrator account is created.

### 4.3. Production User Provisioning Workflow
In a live school operations environment:
1. Log in with the initial administrator account.
2. Navigate to **Users (`/admin/users`)** or **Teachers (`/admin/teachers`)**.
3. Create accounts for each faculty member and administrative officer with individual usernames and strong initial passwords.
4. Users log in with their own assigned credentials at `/login`.
5. Passwords can be updated or reset on-demand by the administrator without database access.
