# User Manual & Operating Guide / የተጠቃሚ መመሪያ

## 1. Overview / አጠቃላይ መግለጫ
The **Modern Ethiopian Elementary School Management System** provides tailored portals for School Administrators, Teachers, Students, and School Leadership. The interface supports instantaneous bilingual switching between **English** and **አማርኛ (Amharic)** with a single click.

---

## 2. Roles & Portals

### 2.1. School Administrator / የትምህርት ቤት አስተዳዳሪ
- **Dashboard (`/admin/dashboard`)**: School metrics, total enrollment, faculty headcount, grade distribution chart (Grades 1-8), today's attendance summary, and quick action shortcuts.
- **Student Admissions (`/students/new`)**: Register new students with English & Amharic names, Ethiopian date of birth, grade assignment, section selection, and guardian emergency contact.
- **Academic Progression (`/students/progression`)**: Promote students from one grade to the next (e.g. Grade 1 to Grade 2) while preserving all historical enrollment records, marks, and attendance.
- **Teacher Assignment (`/admin/teachers`)**: Assign faculty members to specific class sections (Grade 1-8, Sections A/B/C) and curriculum subjects (Mathematics, Amharic, English, Environmental Science, etc.).
- **Timetable (`/timetable`)**: Create conflict-free weekly schedules (Monday to Friday, Periods 1 to 6).

### 2.2. Teacher / መምህር
- **Teacher Dashboard (`/teacher/dashboard`)**: Displays assigned sections, assigned subjects, and today's teaching schedule.
- **Attendance (`/attendance`)**:
  1. Select assigned class section and date.
  2. Click **Mark All Present (ሁሉንም አለ በል)** or individually mark **Present (አለ)**, **Absent (ቀረ)**, **Late (አረፈደ)**, or **Excused (ፈቃድ)**.
  3. Click **Save Attendance**.
- **Continuous Evaluation & Mark Entry (`/assessments`)**:
  1. Create assessment (Quiz, Assignment, Midterm, Final Exam) with maximum marks and weight percentage.
  2. Enter student scores with automatic range validation (`0 <= score <= max_mark`).
  3. Save marks to update subject totals.

### 2.3. Student Portal / የተማሪ ፖርታል (`/student/portal`)
- **Child-Friendly Design**: Large, colorful icons, high contrast, and simplified layout suitable for young elementary children (Grades 1-8).
- **Personalized Access**: Students can only view their own timetable, daily attendance rate, recent published marks, and official school announcements.
- **Report Card (`/reports/view?studentId=...`)**: View official term report card once published by the school administration.

---

## 3. Bilingual Language Selection / የቋንቋ ምርጫ
- Click the language selector in the top navigation bar at any time to toggle between:
  - `English`
  - `አማርኛ`
- The selected language is stored in the user's session and database profile for seamless continuity.
