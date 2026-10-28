# 🇪🇹 Modern Ethiopian Elementary School Management System (Grades 1–8)
### Bule Hora Elementary School Information Management System
**ቡሌ ሆራ የመጀመሪያ ደረጃ ትምህርት ቤት መረጃ አስተዳደር ሥርዓት (ከ1ኛ - 8ኛ ክፍል)**

[![Java](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://openjdk.org/)
[![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10.0-orange.svg)](https://jakarta.ee/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-00758F.svg)](https://www.mysql.com/)
[![Tomcat](https://img.shields.io/badge/Tomcat-10.1-yellow.svg)](https://tomcat.apache.org/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-7952B3.svg)](https://getbootstrap.com/)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

---

## 📌 1. Project Overview & Educational Problem

The **Bule Hora Elementary School Management System** is an enterprise-grade digital school operations platform engineered specifically for primary schools serving **Grades 1 through 8** in Ethiopia.

### The Educational Problem Addressed
Traditional paper-based school operations in elementary schools suffer from:
1. **Lost Historical Records**: When students promote from Grade 1 to Grade 2, paper files or basic registration spreadsheets frequently overwrite or displace previous academic performance, attendance records, and teacher remarks.
2. **Unauthorized Grade Alterations & Lack of Boundary Enforcement**: Teachers often have unrestricted or unvalidated access to modify marks for subjects and classes they do not teach.
3. **Language Barriers**: Administrative tools in English alienate non-English fluent parents, guardians, and young students.
4. **Disorganized Continuous Assessment**: Lack of standardized aggregation for quizzes, midterms, projects, and final examinations based on national curriculum standards.
5. **Slow Manual Report Card Generation**: Calculating ranks, term averages, conduct scores, and attendance roll for hundreds of students takes weeks of manual arithmetic.
6. **Uncontrolled Credential Management**: Staff and faculty lack self-contained, securely managed user credentials with role-specific authorization.

### The Solution: Bule Hora Elementary System
This modernized platform provides a unified, secure, bilingual digital hub featuring:
- **Centralized User Account & Credential Provisioning**: The Administrator can register teachers and all staff roles with custom, admin-assigned usernames and passwords. Users subsequently log in using their own unique credentials every time.
- **On-Demand Password Resets**: Administrators can reset or update passwords for any faculty or staff member at any time directly through the UI.
- **Historical Academic Record Preservation**: Multi-year student enrollment tables ensure that promoting a student creates a new active enrollment while archiving previous grade records without deletion.
- **Explicit Role-Based Access Control (RBAC)**: Teachers can *only* take attendance and enter marks for classes and subjects explicitly assigned to them by the administrator.
- **Full Bilingual Localization**: Seamless 1-click toggle between **English** and **አማርኛ (Amharic)** across every dashboard, form, table, report, and modal with UTF-8 / `utf8mb4` support.
- **Child-Friendly Student Portal**: Designed with large interactive cards and visual progress tracking tailored for young children (Grades 1-8).
- **Automated Printable Official Report Cards**: Instant calculation of subject totals, class averages, ranks, and attendance roll with official seal and signature blocks.
- **Modern Responsive Aesthetics**: Glassmorphic styling, glowing action buttons, subtle animated backgrounds, and 100% responsive layouts tested across all screen resolutions without horizontal overflow.
- **Zero-Dependency 1-Click Embedded Tomcat Runner**: Allows immediate local execution or deployment to free cloud tiers (Render, Railway, Fly.io).

---

## 🏛️ 2. System Architecture & Request Lifecycle

The application adheres strictly to the classic **MVC (Model-View-Controller)** pattern powered by **Jakarta Servlets**, **JSP**, **JSTL**, and native **JDBC** over an **InnoDB MySQL** relational database.

```
+-------------------------------------------------------------------------+
|                              Web Browser                                |
|  Bootstrap 5.3 + Custom CSS Theme + Amharic Ethiopic Noto Typography    |
|  Responsive Breakpoints (Desktop, Tablet, Mobile) - Zero X-Overflow     |
+------------------------------------+------------------------------------+
                                     |  HTTP (UTF-8)
                                     v
+------------------------------------+------------------------------------+
|                      Security & Filter Pipeline                         |
|  - CharacterEncodingFilter (UTF-8 request/response enforcement)         |
|  - LocaleFilter (i18n session resolution: English / አማርኛ)             |
|  - CsrfFilter (Anti-CSRF token verification on state-changing POSTs)   |
|  - AuthFilter (Server-side Role-Based Access Control & route protection)|
+------------------------------------+------------------------------------+
                                     |
                                     v
+------------------------------------+------------------------------------+
|                         Controller Layer                                |
|  - LoginServlet / LogoutServlet (PBKDF2 validation, session renewal)    |
|  - AdminUserServlet (User account creation, role assign, pass reset)   |
|  - AdminTeacherServlet (Teacher registration & class-subject allocation)|
|  - AdminDashboardServlet / TeacherDashboardServlet / StudentPortal...   |
|  - StudentServlet / StudentProgressionServlet (Grade 1-8 promotion)     |
|  - AttendanceServlet (Batch roll marking, teacher assignment check)     |
|  - AssessmentServlet (Score entry with [0, max_score] boundary checks)  |
|  - ReportCardServlet (Calculation, draft review, approval, publishing)  |
|  - TimetableServlet (Conflict-free weekly scheduler)                    |
|  - AnnouncementServlet (Targeted broadcast notices)                     |
+------------------------------------+------------------------------------+
                                     |
                                     v
+------------------------------------+------------------------------------+
|                         Data Access Layer (DAO)                         |
|  UserDAO | TeacherDAO | StudentDAO | AcademicDAO | AttendanceDAO        |
|  AssessmentDAO | ReportCardDAO | SchoolOperationsDAO                    |
|  (Pure JDBC, Prepared Statements, Multi-statement ACID Transactions)   |
+------------------------------------+------------------------------------+
                                     |
                                     v
+------------------------------------+------------------------------------+
|                   Relational Database (MySQL 8.x)                      |
|  InnoDB, utf8mb4, Foreign Keys, PBKDF2 Salted Hashes, Multi-Year History|
+-------------------------------------------------------------------------+
```

---

## 👥 3. Role-Based Permissions Matrix

| Module / Operation | Administrator | Principal | Teacher | Registrar | Attendance Officer | Student |
|---|:---:|:---:|:---:|:---:|:---:|:---:|
| **User Account Provisioning (`/admin/users`)** | ✅ Full | ❌ Denied | ❌ Denied | ❌ Denied | ❌ Denied | ❌ Denied |
| **Password Resets & Account Edits** | ✅ Full | ❌ Denied | ❌ Denied | ❌ Denied | ❌ Denied | ❌ Denied |
| **Admin Dashboard (`/admin/dashboard`)** | ✅ Full | 👁️ Read-Only | ❌ Denied | ❌ Denied | ❌ Denied | ❌ Denied |
| **Teacher Registration & Assignment (`/admin/teachers`)** | ✅ Full | 👁️ Read-Only | ❌ Denied | ❌ Denied | ❌ Denied | ❌ Denied |
| **Student Registration (`/students/new`)** | ✅ Full | 👁️ Read-Only | 👁️ Read-Only | ✅ Full | ❌ Denied | ❌ Denied |
| **Grade Progression (`/students/progress`)** | ✅ Full | 👁️ Review | ❌ Denied | ✅ Execute | ❌ Denied | ❌ Denied |
| **Daily Attendance (`/attendance`)** | ✅ All Classes | 👁️ Read-Only | ✅ Assigned Only | ❌ Denied | ✅ Record/Review | 👁️ Own Only |
| **Continuous Assessment (`/assessments`)** | 👁️ Review | 👁️ Review | ✅ Assigned Only | ❌ Denied | ❌ Denied | ❌ Denied |
| **Official Report Cards (`/reports`)** | ✅ Publish | ✅ Approve | ✏️ Draft | ❌ Denied | ❌ Denied | 👁️ Own (If Published) |
| **Timetable Scheduling (`/timetable`)** | ✅ Manage | 👁️ Read-Only | 👁️ Own Schedule | 👁️ Read-Only | ❌ Denied | 👁️ Class Schedule |
| **Broadcast Announcements (`/announcements`)** | ✅ Publish | ✅ Publish | 👁️ Read | 👁️ Read | 👁️ Read | 👁️ Read |

---

## 📊 4. Database Schema & Key Integrity Rules

The database features 20 normalized tables with strict foreign keys and transactional integrity:
- **`users`**: Central authentication table (`id`, `username` UNIQUE, `password_hash`, `password_salt`, `role`, `full_name`, `email`, `phone`, `preferred_locale`, `is_active`, `created_at`).
- **`teachers`**: Faculty profile (`id`, `user_id` FK, `employee_id` UNIQUE, `full_name_en`, `full_name_am`, `email`, `phone`, `specialization`, `qualification`, `status`).
- **`students`**: Permanent identity record (`id`, `user_id` FK optional, `student_id_number` UNIQUE e.g. `BHES-2026-001`, English & Amharic names, birth date, gender, admission date, guardian contact).
- **`student_enrollments`**: Multi-year progression record (`student_id`, `academic_year_id`, `grade_level_id`, `class_section_id`, `status`). Unique index on `(student_id, academic_year_id)`.
- **`teacher_class_assignments`**: Explicit assignment of teacher to section and subject (`teacher_id`, `class_section_id`, `subject_id`, `academic_year_id`).
- **`attendances`**: Daily attendance roll (`student_id`, `attendance_date`, `status`, `recorded_by_user_id`). Unique index on `(student_id, attendance_date)`.
- **`assessments`** & **`assessment_scores`**: Continuous evaluation marks with range constraints (`0 <= score <= max_score`).
- **`report_cards`** & **`report_card_items`**: Calculated term results, letter grades (`A`-`F`), averages, conduct, and attendance totals.

---

## 🚀 5. Local Setup & Quick Execution

### Prerequisites
- **Java**: JDK 17 or higher (`java -version`)
- **MySQL**: 8.0 or higher running on `localhost:3306` (e.g. WAMP, XAMPP, or standalone MySQL)
- **Maven**: Maven wrapper `mvnw` included with the repository.

### Step-by-Step Instructions

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/petersisay/student-registration-system.git
   cd student-registration-system
   ```

2. **Verify or Configure Database Connection**:
   By default, `DBConnection.java` connects to `localhost:3306` with user `root` and blank password, automatically creating `student_db` and executing migrations.
   To override credentials, set environment variables:
   ```powershell
   $env:DB_USER="root"
   $env:DB_PASSWORD=""
   ```

3. **Run Automated Tests**:
   ```powershell
   .\mvnw.cmd test
   ```
   *All 7 automated test suites (AuthFilter, DB Migration, I18n, Score Calculation, Progression, Teacher Authorization, and PBKDF2 Password Security) execute and pass 100%.*

4. **Launch the Application**:
   ```powershell
   .\mvnw.cmd compile exec:java
   ```
   *The embedded Tomcat 10.1 server starts immediately on `http://localhost:8080/`.*

5. **Open Browser & Verified Route Summary**:
   Navigate to [http://localhost:8080/](http://localhost:8080/) in your web browser.

   | Route | Purpose | Access Control | Language Support |
   |---|---|---|---|
   | `/` or `/index.jsp` | Modern Bilingual Portal & Overview | Public | EN / አማርኛ (`?lang=am`) |
   | `/login` | Staff & Student Authentication Portal | Public | EN / አማርኛ (`?lang=am`) |
   | `/admin/dashboard` | Administrator Operations & Metric Cards | ADMIN role | EN / አማርኛ |
   | `/admin/users` | User Account Provisioning & Password Resets | ADMIN role | EN / አማርኛ |
   | `/admin/teachers` | Faculty Directory & Class/Subject Allocations | ADMIN role | EN / አማርኛ |
   | `/students` | Searchable Student Directory & Profiles | Staff / Auth Required | EN / አማርኛ |
   | `/teacher/dashboard`| Teacher Dashboard & Assigned Classes | TEACHER role | EN / አማርኛ |
   | `/attendance` | Daily Attendance Roll Recording | TEACHER / ATTENDANCE | EN / አማርኛ |
   | `/assessments` | Continuous Assessment Mark Entry | TEACHER role | EN / አማርኛ |
   | `/reports` | Term Report Card Generation & Review | Staff / Auth Required | EN / አማርኛ |
   | `/student/portal` | Child-Friendly Student Hub & Report Card | STUDENT role | EN / አማርኛ |

---

## 🔑 6. Default User Accounts (Development Fixtures)

The database migration automatically provisions the following default accounts. In production, the Administrator creates custom user credentials via `/admin/users` or `/admin/teachers`:

| Role | Default Username | Default Password | Access Scope |
|---|---|---|---|
| **Administrator** | `admin` | `admin123` | Complete school management, user provisioning & password resets |
| **Principal** | `principal` | `principal123` | Academic supervision, timetable review & report card approval |
| **Teacher** | `teacher.abebe` | `teacher123` | Assigned classes, attendance, and continuous assessment marks |
| **Registrar** | `registrar` | `registrar123` | Student admissions, profiles & multi-year grade progression |
| **Attendance Officer** | `attendance` | `attendance123` | Daily school-wide attendance recording |
| **Student** | `student.dawit` | `student123` | Child-friendly student portal, personal attendance & report card |

> **Note on Custom Provisioning**: When the Admin creates a new teacher or staff member, they assign a custom username and password. The user logs in at `/login` using their own credentials every time.

---

## 🌐 7. Free Cloud Deployment Options

The project includes zero-cost cloud deployment readiness:
- **Render**: Deploy as a Web Service running `./mvnw clean package` and `java -cp target/classes:target/dependency/* com.school.runner.EmbeddedServer`.
- **Railway.app**: Instant GitHub integration with auto-provisioned free MySQL instance.
- **Free Cloud MySQL**: Fully compatible with Aiven (5GB free) or Clever Cloud.
- Detailed step-by-step instructions are documented in [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md).

---

## 📁 8. Project Structure

```
student-registration-system/
├── docs/
│   ├── ARCHITECTURE.md          # Full architectural breakdown, security & RBAC matrix
│   ├── DATABASE.md              # Database schema, ERD, and migration guide
│   ├── DEPLOYMENT.md            # Free-tier cloud hosting & Docker guides
│   └── USER_GUIDE.md             # Comprehensive bilingual operating manual
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/school/runner/EmbeddedServer.java  # 1-click embedded Tomcat
│   │   │   ├── dao/             # Pure JDBC DAOs (UserDAO, TeacherDAO, StudentDAO...)
│   │   │   ├── filter/          # UTF-8, CSRF, Locale, and RBAC filters
│   │   │   ├── model/           # Enterprise domain models (User, Teacher, Student...)
│   │   │   ├── servlet/         # Jakarta 10 Controller Servlets (AdminUserServlet...)
│   │   │   └── util/            # DBConnection, PasswordUtil, I18nUtil
│   │   ├── resources/
│   │   │   ├── db/migrations/   # Versioned SQL migrations (V1, V2)
│   │   │   ├── messages_en.properties # English language bundle
│   │   │   └── messages_am.properties # Amharic language bundle
│   │   └── webapp/
│   │       ├── css/style.css    # Modern UI design system with animations & responsive rules
│   │       ├── js/app.js        # Micro-interactions and counter animations
│   │       ├── login.jsp        # Bilingual animated login portal
│   │       └── WEB-INF/views/   # Protected JSP templates by role (admin, teacher, student...)
│   └── test/java/               # Comprehensive automated test suite
├── pom.xml                      # Maven configuration (Java 17, Tomcat 10.1 embed)
├── bule-hora-school-management-system.zip # Complete offline release package
└── README.md                    # Project documentation
```

---

## 🛡️ 9. Security & Child Privacy Protection

1. **Child Privacy By Default**: Student personal records, contact details, and marks are strictly guarded by server-side authorization. Direct URL tampering returns `403 Forbidden`.
2. **Cryptographic Password Hashing**: Utilizes PBKDF2WithHmacSHA256 (65,536 iterations) with cryptographically generated per-user 16-byte random salts and constant-time digest verification (`MessageDigest.isEqual`).
3. **Session Fixation Defense**: Active session IDs are destroyed and regenerated upon authentication.
4. **Anti-CSRF Protection**: State-changing POST submissions across all admin, faculty, and student forms require session CSRF token verification.
5. **SQL Injection Immunity**: 100% of database queries utilize parameterized JDBC `PreparedStatement` instances.
6. **XSS Prevention**: All user-submitted fields rendered in JSP templates utilize `<c:out>` escaping or JSTL equivalent.

---

## 📄 10. License & Author
- **Author**: Peter Sisay
- **Repository**: [https://github.com/petersisay/student-registration-system.git](https://github.com/petersisay/student-registration-system.git)
- **License**: MIT License
