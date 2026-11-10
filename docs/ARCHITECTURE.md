# Bule Hora Elementary School Management System: Architecture & Design

## 1. System Overview
The **Bule Hora Elementary School Management System** is a specialized, bilingual (Amharic & English), role-based school management platform engineered specifically for Ethiopian elementary schools (Grades 1 through 8). It modernizes traditional student registration into an enterprise-grade academic platform covering:

- Multi-year student enrollment and historical academic progression.
- Centralized user account provisioning and on-demand password resets.
- Strict teacher-to-class and teacher-to-subject assignments.
- Configurable academic grading policies and weighted score calculations.
- Daily attendance tracking and reporting.
- Conflict-free timetable scheduling.
- Bilingual (Amharic & English) user interface with Ethiopic script support.
- Role-based server-side access control (RBAC).
- Responsive UI design system without horizontal overflows.

---

## 2. Technology Stack
- **Language**: Java 17 / 21 / 25
- **Platform**: Jakarta EE (Servlet 6.0 / 5.0+, JSP 3.0, JSTL 3.0)
- **Database**: MySQL 8.x / MariaDB (InnoDB, `utf8mb4_unicode_ci`)
- **Build Tool**: Apache Maven (via Maven Wrapper `mvnw`)
- **Server Options**: Apache Tomcat 10.1.x, IntelliJ SmartTomcat, or Embedded Tomcat runner
- **UI Framework**: Bootstrap 5.3 + Custom CSS Design System (`#17324D`, `#2563EB`, `#0F9D8A`, `#F4B942`)
- **Typography**: Noto Sans Ethiopic / Inter / System UI

---

## 3. Core Architectural Layers
The project follows standard Model-View-Controller (MVC) architecture with clear separation of concerns:

```
src/
├── main/
│   ├── java/
│   │   ├── com/school/runner/EmbeddedServer.java # Embedded Tomcat launcher
│   │   ├── config/          # Application & Database Configuration
│   │   ├── filter/          # AuthFilter, CsrfFilter, CharacterEncodingFilter, LocaleFilter
│   │   ├── model/           # Strongly typed domain entities (User, Teacher, Student...)
│   │   ├── dao/             # Data Access Objects using JDBC & PreparedStatements
│   │   ├── service/         # Business logic, validation & progression rules
│   │   ├── servlet/         # Controller servlets handling HTTP requests
│   │   │   ├── AdminUserServlet.java      # User provisioning & password resets
│   │   │   ├── AdminTeacherServlet.java   # Teacher directory & class assignment
│   │   │   ├── AdminDashboardServlet.java # Metric cards & school analytics
│   │   │   ├── StudentServlet.java        # Student admissions & directory
│   │   │   ├── StudentProgressionServlet.java # Multi-year grade progression
│   │   │   ├── AttendanceServlet.java     # Daily classroom attendance roll
│   │   │   ├── AssessmentServlet.java     # Continuous evaluation mark entry
│   │   │   ├── ReportCardServlet.java     # Official term report cards
│   │   │   ├── TimetableServlet.java      # Weekly schedule planner
│   │   │   ├── AnnouncementServlet.java   # School broadcasts
│   │   │   └── LoginServlet.java          # Authentication & session renewal
│   │   └── util/            # PasswordUtil (PBKDF2), DBConnection, EnvUtil, I18nUtil
│   ├── resources/
│   │   ├── db/migrations/   # Versioned SQL migrations (V1, V2)
│   │   ├── messages_en.properties # English localization
│   │   └── messages_am.properties # Amharic (አማርኛ) localization
│   └── webapp/
│       ├── WEB-INF/
│       │   ├── views/       # Protected JSP views (admin, teacher, student, reports)
│       │   └── web.xml      # Servlet mappings & security constraints
│       ├── css/             # Modern CSS with animations, glassmorphic cards & responsive rules
│       ├── js/              # Client-side micro-interactions & form validation
│       └── images/          # Genuine educational badges and illustrations
```

---

## 4. Authentication & Credential Architecture

```
[Administrator via /admin/users or /admin/teachers]
                         |
                         v (Custom Username & Plain Password)
+--------------------------------------------------------+
|                      PasswordUtil                      |
|  1. Generate cryptographically secure 16-byte salt     |
|  2. Hash via PBKDF2WithHmacSHA256 (65,536 iterations)  |
|  3. Produce Base64(salt) + Base64(hash)                |
+--------------------------------------------------------+
                         |
                         v
+--------------------------------------------------------+
|                      UserDAO                           |
|  INSERT INTO users (username, password_hash,           |
|                     password_salt, role, ...)          |
+--------------------------------------------------------+
                         |
                         v (Subsequent Login by User)
[User enters Username & Password at /login]
                         |
                         v
+--------------------------------------------------------+
|                      LoginServlet                      |
|  1. Lookup user by username via UserDAO                |
|  2. Recompute hash using stored user salt              |
|  3. MessageDigest.isEqual (constant-time verification) |
|  4. Invalidate old session & generate new session ID   |
|  5. Store User in session & dispatch to role dashboard |
+--------------------------------------------------------+
```

---

## 5. Role-Based Access Control (RBAC) Matrix

| Feature / URL Pattern | Administrator | Principal | Registrar | Teacher | Attendance Officer | Student |
|---|:---:|:---:|:---:|:---:|:---:|:---:|
| User Management & Provisioning (`/admin/users`) | ✅ Full | ❌ Denied | ❌ Denied | ❌ Denied | ❌ Denied | ❌ Denied |
| Password Resets & Profile Edits | ✅ Full | ❌ Denied | ❌ Denied | ❌ Denied | ❌ Denied | ❌ Denied |
| System Settings & Staff Accounts (`/admin/*`) | ✅ Full | ❌ Denied | ❌ Denied | ❌ Denied | ❌ Denied | ❌ Denied |
| Academic Years & Grading Policies (`/academic/*`) | ✅ Full | 👁️ View | ❌ Denied | ❌ Denied | ❌ Denied | ❌ Denied |
| Student Registration & Enrollment (`/students/*`) | ✅ Full | 👁️ View | ✅ Full | 👁️ Assigned | ❌ Denied | ❌ Denied |
| Grade Progression (`/students/progress`) | ✅ Full | 👁️ View | ✅ Full | ❌ Denied | ❌ Denied | ❌ Denied |
| Teacher Class/Subject Assignments (`/admin/teachers/assign`) | ✅ Full | 👁️ View | ❌ Denied | ❌ Denied | ❌ Denied | ❌ Denied |
| Attendance Tracking (`/attendance/*`) | ✅ Full | 👁️ View | 👁️ View | ✅ Assigned | ✅ Full | ❌ Denied |
| Mark Entry & Assessment (`/assessments/*`) | ✅ Full | 👁️ View | ❌ Denied | ✅ Assigned | ❌ Denied | ❌ Denied |
| Report Card Generation & Approval (`/reports/*`) | ✅ Full | ✅ Approve | 👁️ View | ✅ Assigned | ❌ Denied | ❌ Denied |
| Student Dashboard (`/student/portal`) | ❌ Denied | ❌ Denied | ❌ Denied | ❌ Denied | ❌ Denied | ✅ Own Records |
| Timetable Management (`/timetable/*`) | ✅ Full | 👁️ View | ❌ Denied | 👁️ Assigned | ❌ Denied | 👁️ Assigned |

---

## 6. Historical Academic Record Preservation
To prevent destructive updates when students advance grades:
1. Student profiles (`students`) contain immutable core details (identity, date of birth, admission date).
2. Annual grade placements are stored in `student_enrollments`, recording the specific `academic_year_id`, `grade_level_id`, and `class_section_id`.
3. Assessments, attendance records, and report cards point to the historical enrollment and academic period, never to a mutable single grade column.
4. Promoting a student to the next grade creates a new active enrollment record while marking previous years as `COMPLETED` or `PROMOTED`.
