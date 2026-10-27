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

The **Ethiopian Elementary School Management System** is an enterprise-grade digital platform engineered specifically for elementary schools serving **Grades 1 through 8** in Ethiopia.

### The Educational Problem Addressed
Traditional paper-based school operations in elementary schools suffer from:
1. **Lost Historical Records**: When students promote from Grade 1 to Grade 2, paper files or basic registration spreadsheets frequently overwrite or displace previous academic performance, attendance records, and teacher remarks.
2. **Unauthorized Grade Alterations & Lack of Boundary Enforcement**: Teachers often have unrestricted or unvalidated access to modify marks for subjects and classes they do not teach.
3. **Language Barriers**: Administrative tools in English alienate non-English fluent parents, guardians, and young students.
4. **Disorganized Continuous Assessment**: Lack of standardized aggregation for quizzes, midterms, projects, and final examinations based on national curriculum standards.
5. **Slow Manual Report Card Generation**: Calculating ranks, term averages, conduct scores, and attendance roll for hundreds of students takes weeks of manual arithmetic.

### The Solution: Bule Hora Elementary System
This modernized platform provides a unified, secure, bilingual digital hub with:
- **Historical Academic Record Preservation**: Multi-year student enrollment tables ensure that promoting a student creates a new active enrollment while archiving previous grade records without deletion.
- **Explicit Role-Based Access Control (RBAC)**: Teachers can *only* take attendance and enter marks for classes and subjects explicitly assigned to them by the administrator.
- **Full Bilingual Localization**: Seamless 1-click toggle between **English** and **አማርኛ (Amharic)** across every dashboard, form, table, report, and error page with UTF-8 / `utf8mb4` support.
- **Child-Friendly Student Portal**: Designed with large interactive cards and visual progress tracking tailored for young children (Grades 1-8).
- **Automated Printable Official Report Cards**: Instant calculation of subject totals, class averages, ranks, and attendance roll with official seal and signature blocks.
- **Zero-Dependency 1-Click Embedded Tomcat Runner**: Allows immediate local execution or deployment to free cloud tiers (Render, Railway, Fly.io).

---

## 🏛️ 2. System Architecture & Request Lifecycle

The application adheres strictly to the classic **MVC (Model-View-Controller)** pattern powered by **Jakarta Servlets**, **JSP**, **JSTL**, and native **JDBC** over an **InnoDB MySQL** relational database.

```
+-------------------------------------------------------------------------+
|                              Web Browser                                |
|  Bootstrap 5.3 + Custom CSS Theme + Amharic Ethiopic Noto Typography    |
+------------------------------------+------------------------------------+
                                     |  HTTP (UTF-8)
                                     v
+------------------------------------+------------------------------------+
|                      Security & Filter Pipeline                         |
|  - CharacterEncodingFilter (UTF-8 request/response enforcement)         |
|  - LocaleFilter (i18n session resolution: English / አማርኛ)             |
|  - CsrfFilter (Anti-CSRF token verification on POST requests)           |
|  - AuthFilter (Server-side Role-Based Access Control & route protection)|
+------------------------------------+------------------------------------+
                                     |
                                     v
+------------------------------------+------------------------------------+
|                         Controller Layer                                |
|  - LoginServlet / LogoutServlet (Session fixation renewal, SHA-256)     |
|  - AdminDashboardServlet / TeacherDashboardServlet / StudentPortal...   |
|  - StudentServlet / StudentProgressionServlet (Grade 1-8 promotion)     |
|  - AdminTeacherServlet (Teacher-class-subject allocations)             |
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
|  UserDAO | StudentDAO | TeacherDAO | AcademicDAO | AttendanceDAO        |
|  AssessmentDAO | ReportCardDAO | SchoolOperationsDAO                    |
|  (Pure JDBC, Prepared Statements, Multi-statement ACID Transactions)   |
+------------------------------------+------------------------------------+
                                     |
                                     v
+------------------------------------+------------------------------------+
|                   Relational Database (MySQL 8.x)                      |
|  InnoDB, utf8mb4, Foreign Keys, Unique Progression Constraints          |
+-------------------------------------------------------------------------+
```

---

## 👥 3. Role-Based Permissions Matrix

| Module / Operation | Administrator | Principal | Teacher | Registrar | Attendance Officer | Student |
|---|:---:|:---:|:---:|:---:|:---:|:---:|
| **Admin Dashboard** | Full | Read-Only | - | - | - | - |
| **Student Registration** | Create/Edit | Read | Read | Create/Edit | - | - |
| **Grade Progression** | Execute | Review | - | Execute | - | - |
| **Faculty Assignments** | Create/Delete | Read | - | - | - | - |
| **Daily Attendance** | All Classes | Read | Assigned Only | - | Record/Review | Own Only |
| **Mark Entry** | Review | Review | Assigned Only | - | - | - |
| **Report Card Publication** | Publish | Approve | Draft | - | - | Own (If Published) |
| **Timetable Scheduling** | Manage | Read | Own Schedule | Read | - | Class Schedule |
| **Broadcast Notices** | Publish | Publish | Read | Read | Read | Read |

---

## 📊 4. Database Schema & Multi-Year Progression

The database features 20 normalized tables with strict foreign keys:
- **`students`**: Permanent identity record (`student_id_number`, names in English & Amharic, birth date, gender, admission date, guardian emergency contact).
- **`student_enrollments`**: Multi-year progression record (`student_id`, `academic_year_id`, `grade_level_id`, `class_section_id`, `status`). Unique index on `(student_id, academic_year_id)`.
- **`teacher_class_assignments`**: Explicit assignment of teacher to section and subject (`teacher_id`, `class_section_id`, `subject_id`, `academic_year_id`).
- **`attendances`**: Daily attendance roll (`student_id`, `attendance_date`, `status`, `recorded_by_user_id`).
- **`assessments`** & **`assessment_scores`**: Continuous evaluation marks (`0 <= score <= max_score`).
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
   *All 7 unit and integration tests (AuthFilter, DB Migration, I18n, Score Calculation, Progression, Teacher Authorization, and PBKDF2 Password Security) execute and pass 100%.*

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
   | `/show_all` | Legacy redirect to modern student directory | Auto 302 Redirect | - |
   | `/students` | Searchable Student Directory & Profiles | Staff / Auth Required | EN / አማርኛ |
   | `/admin/dashboard` | Administrator Operations & Metric Cards | ADMIN role | EN / አማርኛ |
   | `/teacher/dashboard`| Teacher Dashboard & Assigned Classes | TEACHER role | EN / አማርኛ |
   | `/student/portal` | Child-Friendly Student Hub & Report Card | STUDENT role | EN / አማርኛ |

---

## 🔑 6. Default User Accounts (Development Fixtures)

The database migration automatically provisions the following test accounts with PBKDF2 cryptographic password hashing:

| Role | Username | Password | Access Scope |
|---|---|---|---|
| **Administrator** | `admin` | `admin123` | Complete school management & user control |
| **Principal** | `principal` | `principal123` | Academic supervision & report card approval |
| **Teacher** | `teacher.abebe` | `teacher123` | Assigned classes, attendance, and mark entry |
| **Registrar** | `registrar` | `registrar123` | Student admissions & multi-year progression |
| **Attendance Officer** | `attendance` | `attendance123` | Daily school-wide attendance recording |
| **Student** | `student.dawit` | `student123` | Child-friendly student portal & report card |

---

## 🌐 7. Free Cloud Deployment Options

The project includes zero-cost cloud deployment readiness:
- **Render**: Deploy as a Web Service running `./mvnw clean package` and `java -cp target/classes:target/dependency/* com.school.runner.EmbeddedServer`.
- **Railway.app**: Instant GitHub integration with auto-provisioned free MySQL instance.
- **Free Cloud MySQL**: Compatible with Aiven (5GB free) or Clever Cloud.
- Detailed step-by-step instructions are documented in [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md).

---

## 📁 8. Project Structure

```
student-registration-system/
├── docs/
│   ├── ARCHITECTURE.md          # Full architectural breakdown and RBAC matrix
│   ├── DATABASE.md              # Database schema, ERD, and migration guide
│   ├── DEPLOYMENT.md            # Free-tier cloud hosting & Docker guides
│   └── USER_GUIDE.md             # Bilingual operating manual
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/school/runner/EmbeddedServer.java  # 1-click embedded Tomcat
│   │   │   ├── dao/             # Pure JDBC DAOs with prepared statements
│   │   │   ├── filter/          # UTF-8, CSRF, Locale, and RBAC filters
│   │   │   ├── model/           # Enterprise domain models
│   │   │   ├── servlet/         # Jakarta 10 Controller Servlets
│   │   │   └── util/            # DBConnection, PasswordUtil, I18nUtil
│   │   ├── resources/
│   │   │   ├── db/migrations/   # Versioned SQL migrations (V1, V2)
│   │   │   ├── messages_en.properties # English language bundle
│   │   │   └── messages_am.properties # Amharic language bundle
│   │   └── webapp/
│   │       ├── css/style.css    # Modern UI design system with animations
│   │       ├── js/app.js        # Micro-interactions and counter animations
│   │       ├── login.jsp        # Bilingual animated login portal
│   │       └── WEB-INF/views/   # Protected JSP templates by role
│   └── test/java/               # Comprehensive automated test suite
├── pom.xml                      # Maven configuration (Java 17, Tomcat 10.1 embed)
└── README.md                    # Project documentation
```

---

## 🛡️ 9. Security & Privacy for Children

1. **Child Privacy By Default**: Student personal records, contact details, and grades are protected by server-side authorization. Direct URL tampering returns `403 Forbidden`.
2. **Cryptographic Password Hashing**: Utilizes PBKDF2WithHmacSHA256 (65,536 iterations) with cryptographically generated per-user 16-byte random salts and constant-time digest verification (`MessageDigest.isEqual`).
3. **Session Fixation Defense**: Active session IDs are destroyed and regenerated upon authentication.
4. **Anti-CSRF Protection**: State-changing POST submissions require session CSRF token verification.
5. **SQL Injection Immunity**: 100% of database queries utilize parameterized JDBC `PreparedStatement` instances.

---

## 📄 10. License & Author
- **Author**: Peter Sisay
- **Repository**: [https://github.com/petersisay/student-registration-system.git](https://github.com/petersisay/student-registration-system.git)
- **License**: MIT License
