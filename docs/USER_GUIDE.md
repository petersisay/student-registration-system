# Bule Hora Elementary School Management System: User Manual & Operating Guide
# የቡሌ ሆራ የመጀመሪያ ደረጃ ትምህርት ቤት መረጃ አስተዳደር ሥርዓት የተጠቃሚ መመሪያ

---

## 1. Introduction / መግቢያ
The **Bule Hora Elementary School Management System** is a modern, bilingual school operations platform tailored for Ethiopian elementary schools (Grades 1 through 8). It empowers school administrators, teachers, administrative staff, students, and parents with dedicated portals, granular role-based security, and full bilingual support in **English** and **አማርኛ (Amharic)**.

---

## 2. Authentication & Credential Management / የመግቢያ መረጃ አስተዳደር

### 2.1. Logging into the System / ወደ ሲስተሙ መግባት
1. Open your web browser and navigate to `http://localhost:8080/login` (or your school's deployment URL).
2. Enter your assigned **Username (የተጠቃሚ ስም)** and **Password (የይለፍ ቃል)** provided to you by the School Administrator.
3. Click **Sign In (ግባ)**.
4. The system verifies your credentials using PBKDF2 cryptography and automatically redirects you to your role-specific dashboard:
   - **Administrators** -> `/admin/dashboard`
   - **Principals** -> `/admin/dashboard` (Academic Supervision)
   - **Teachers** -> `/teacher/dashboard`
   - **Registrars** -> `/students` (Admissions & Directory)
   - **Attendance Officers** -> `/attendance`
   - **Students** -> `/student/portal`

---

## 3. Administrator Operations / የአስተዳዳሪ ተግባራት

### 3.1. Registering Users & Assigning Credentials (`/admin/users`)
**አዳዲስ ተጠቃሚዎችን መመዝገብና የመግቢያ መረጃ መስጠት**

The Administrator can register accounts for all school personnel:
1. Navigate to **Users (ተጠቃሚዎች)** in the top navigation bar or go to `/admin/users`.
2. Review existing user accounts, search by name/username, or filter by role.
3. Click **+ Register New User / አዲስ ተጠቃሚ መዝግብ**.
4. In the registration modal:
   - **Full Name (ሙሉ ስም)**: Enter the user's complete name (e.g. `Chaltu Dibaba`).
   - **User Role (ኃላፊነት)**: Select the appropriate role:
     - `TEACHER` (መምህር)
     - `REGISTRAR` (ሬጅስትራር)
     - `PRINCIPAL` (ርዕሰ መምህር)
     - `ATTENDANCE_OFFICER` (የክትትል ኃላፊ)
     - `STUDENT` (ተማሪ)
     - `ADMIN` (አስተዳዳሪ)
   - **Login Credentials (የመግቢያ መረጃ)**:
     - **Username (የተጠቃሚ ስም)**: Enter a unique identifier (e.g. `registrar.chaltu`).
     - **Password (የይለፍ ቃል)**: Enter the initial password (e.g. `ChaltuPass@2026`).
   - **Email & Phone (ኢሜይል እና ስልክ)**: Optional contact information.
   - **Preferred Language (ቋንቋ)**: English or አማርኛ.
5. Click **Create User Account**.
6. The user is instantly registered. When creating a `TEACHER` account through this form, a linked Faculty Profile is automatically initialized.
7. Inform the user of their assigned username and password. They will use this exact pair to sign in at `/login` every time.

### 3.2. Registering Teachers in Faculty Directory (`/admin/teachers`)
**መምህራንን መመዝገብና የመግቢያ መረጃ መስጠት**

Administrators can also register faculty directly from the Faculty Directory:
1. Navigate to **Teachers (መምህራን)** -> `/admin/teachers`.
2. Click **+ Register New Faculty**.
3. Fill in the teacher's details:
   - First Name & Last Name (English & Amharic).
   - Email & Phone Number.
   - Specialization (e.g. `Mathematics`, `Amharic`, `Environmental Science`).
   - Qualification (e.g. `B.Ed in Mathematics`, `Diploma`).
   - **Login Username (የተጠቃሚ ስም)**: Custom username for the teacher (e.g. `teacher.meles`).
   - **Login Password (የይለፍ ቃል)**: Custom initial password (e.g. `MelesPass@2026`).
4. Click **Register Faculty**.
5. The system creates the `User` account with role `TEACHER`, links `teacher.user_id = user.id`, and saves the faculty record.
6. The teacher can immediately sign in at `/login`.

### 3.3. Resetting User or Teacher Passwords
**የይለፍ ቃል መቀየር ወይም ማደስ**

If a staff member, teacher, or student forgets their password:
1. Go to **Users (`/admin/users`)** or **Teachers (`/admin/teachers`)**.
2. Locate the person in the table.
3. Click the yellow **Password (የይለፍ ቃል)** key button next to their name.
4. Enter the **New Password (አዲስ የይለፍ ቃል)**.
5. Click **Update Password**.
6. The password is immediately re-hashed with a fresh cryptographic salt. The user can sign in with their new password on their very next login.

### 3.4. Teacher Class & Subject Allocation (`/admin/teachers/assign`)
**የክፍል እና የትምህርት ዓይነት ምደባ**

Strict classroom access control requires assigning teachers to specific classes:
1. On the Faculty Directory page (`/admin/teachers`), click **Assign (መድብ)** for a teacher.
2. Select the **Class Section** (e.g. `Grade 1 - Section A`, `Grade 4 - Section B`).
3. Select the **Subject** (e.g. `English`, `Mathematics`, `General Science`).
4. Check **Homeroom Teacher (የክፍል ኃላፊ)** if applicable.
5. Click **Assign Class & Subject**.
6. The teacher is now strictly authorized to take attendance and enter marks for that section and subject.

### 3.5. Student Admissions with Optional Portal Credentials (`/students/new`)
**የተማሪዎች ምዝገባ እና የተማሪ ፖርታል መለያ**

1. Navigate to **Students -> + New Registration**.
2. Complete Section 1: **Student Identification (የተማሪው መረጃ)** (English & Amharic names, Gender, Date of Birth).
3. Complete Section 2: **Academic Enrollment (የትምህርት ምደባ)** (Grade 1-8, Section A/B/C, Academic Year).
4. Complete Section 3: **Guardian & Emergency Contact (የወላጅ/አሳዳጊ መረጃ)** (Guardian name, phone, relationship).
5. Optional Section 4: **Student Portal Login Account (የተማሪው መግቢያ መለያ)**:
   - Provide a unique **Portal Username** (e.g. `student.almaz`) and **Password**.
   - This provisions a login account so the student/parent can sign in and view personal records at `/student/portal`.
6. Click **Complete Student Registration**.

### 3.6. Multi-Year Grade Progression (`/students/progression`)
**የተማሪዎች የክፍል ማስተላለፍ**

At the end of an academic year, promote students without losing historical records:
1. Navigate to **Students -> Grade Progression**.
2. Select Source Academic Year and Target Academic Year.
3. Select the current Grade Level.
4. Review student eligibility based on final report card averages.
5. Select students to promote, repeat, or transfer.
6. Click **Process Progression**.
7. Past enrollments, marks, and attendance remain permanently archived while new active enrollments are created for the new school year.

---

## 4. Teacher Operations / የመምህራን ተግባራት

### 4.1. Teacher Dashboard (`/teacher/dashboard`)
**የመምህራን መነሻ ገጽ**
- View your daily schedule and assigned class sections.
- Quick links to take attendance, enter continuous assessment marks, and post class announcements.

### 4.2. Daily Classroom Attendance (`/attendance`)
**የዕለት ተዕለት የክፍል ክትትል መመዝገብ**
1. Select your assigned **Class Section** and the **Attendance Date**.
2. Click **Load Attendance Roster**.
3. Use the quick action **Mark All Present (ሁሉንም አለ በል)** or individually mark:
   - **Present (አለ)**
   - **Absent (ቀረ)**
   - **Late (አረፈደ)**
   - **Excused (ፈቃድ)**
4. Click **Save Attendance (ክትትል መዝግብ)**.
5. Attendance totals are immediately aggregated into official report cards and administrative summaries.

### 4.3. Continuous Assessment Mark Entry (`/assessments`)
**የተከታታይ ምዘና ውጤት መመዝገብ**
1. Select your assigned class section and subject.
2. Click **+ Create Assessment (አዲስ ምዘና ፍጠር)**:
   - Title (e.g. `Midterm Examination`, `Chapter 3 Quiz`).
   - Type: `QUIZ`, `ASSIGNMENT`, `MIDTERM`, `FINAL`, or `PROJECT`.
   - Max Score (e.g. `20`, `40`, `100`).
   - Weight Percentage (e.g. `20%`, `30%`).
3. Click **Enter Scores**:
   - Enter student marks. The system enforces client and server boundaries: `0 <= score <= max_score`.
4. Click **Save Marks**.

---

## 5. Official Term Report Cards / ይፋዊ የተማሪዎች ውጤት ካርድ

### 5.1. Generation & Calculation (`/reports`)
1. Select the Academic Year, Term (Semester 1 / Semester 2), and Class Section.
2. The system automatically compiles:
   - Subject assessment totals and percentage calculations.
   - Letter grades (`A` = 90-100%, `B` = 80-89%, `C` = 60-79%, `D` = 50-59%, `F` = below 50%).
   - Total score and term average.
   - Class rank based on class average ranking algorithm.
   - Attendance roll (Days Present, Absent, Late).
3. Teachers submit the draft for review.

### 5.2. Principal Approval & Publication
1. The School Principal reviews the class report cards.
2. The Principal clicks **Approve (አጽድቅ)**.
3. The Administrator clicks **Publish (ይፋ አድርግ)**.
4. Once published, students and guardians can access their report card on the Student Portal, and teachers can print official physical copies with school stamp and signature lines.

---

## 6. Child-Friendly Student Portal / የተማሪዎች ፖርታል (`/student/portal`)

Designed specifically for elementary students (Grades 1 through 8):
- **Visual Progress**: Friendly cards showing today's attendance status, current semester courses, and teachers.
- **Weekly Schedule**: Visual timetable showing periods 1 to 6.
- **Official Report Card**: Printable term summary with motivational badges and teacher remarks.
- **School Announcements**: Important dates, exam schedules, and holiday notifications.

---

## 7. Bilingual Interface & Accessibility / ቋንቋ መቀየሪያ

- **One-Click Toggle**: Click the language badge in the upper right header (`EN` or `አማ`) to toggle the entire application between English and Amharic.
- **Ethiopic Typography**: Optimized with Google's `Noto Sans Ethiopic` font for crisp, legible Amharic rendering across all screens.
- **Responsive Layout**: Designed to adapt effortlessly to desktop computers, school computer lab monitors, tablets, and smartphones without horizontal scrollbars or truncated content.
