-- ============================================================================
-- Ethiopian Elementary School Management System (Grades 1-8)
-- V2__seed_ethiopian_elementary_data.sql: Reference Data & Seed Records
-- ============================================================================

INSERT INTO schools (id, name, code, logo_url, phone, email, address, default_locale)
VALUES (1, 'Bule Hora Elementary School (ቡሌ ሆራ የመጀመሪያ ደረጃ ትምህርት ቤት)', 'BHES-001', 'images/bule-hora-logo.png', '+251 46 443 0123', 'contact@bulehora.edu.et', 'Bule Hora, West Guji Zone, Oromia, Ethiopia', 'en')
ON DUPLICATE KEY UPDATE name=VALUES(name), logo_url=VALUES(logo_url), email=VALUES(email), address=VALUES(address);

-- 2. Seed Academic Year & Semesters (Ethiopian Academic Calendar)
INSERT INTO academic_years (id, name, start_date, end_date, is_current)
VALUES 
(1, '2026/2027 (2019 E.C.)', '2026-09-15', '2027-07-05', TRUE),
(2, '2027/2028 (2020 E.C.)', '2027-09-15', '2028-07-05', FALSE)
ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO academic_terms (id, academic_year_id, name, term_number, start_date, end_date, is_current)
VALUES 
(1, 1, 'Semester 1 (አንደኛ ሴሚስተር)', 1, '2026-09-15', '2027-01-30', TRUE),
(2, 1, 'Semester 2 (ሁለተኛ ሴሚስተር)', 2, '2027-02-15', '2027-07-05', FALSE),
(3, 2, 'Semester 1 (አንደኛ ሴሚስተር)', 1, '2027-09-15', '2028-01-30', FALSE),
(4, 2, 'Semester 2 (ሁለተኛ ሴሚስተር)', 2, '2028-02-15', '2028-07-05', FALSE)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 3. Seed Grades 1 through 8
INSERT INTO grade_levels (id, grade_number, name_en, name_am) VALUES
(1, 1, 'Grade 1', 'ክፍል 1'),
(2, 2, 'Grade 2', 'ክፍል 2'),
(3, 3, 'Grade 3', 'ክፍል 3'),
(4, 4, 'Grade 4', 'ክፍል 4'),
(5, 5, 'Grade 5', 'ክፍል 5'),
(6, 6, 'Grade 6', 'ክፍል 6'),
(7, 7, 'Grade 7', 'ክፍል 7'),
(8, 8, 'Grade 8', 'ክፍል 8')
ON DUPLICATE KEY UPDATE name_en=VALUES(name_en), name_am=VALUES(name_am);

-- 4. Seed Class Sections for 2026/2027 and 2027/2028
INSERT INTO class_sections (id, grade_level_id, academic_year_id, section_name, room_number, max_capacity) VALUES
(1, 1, 1, 'A', 'Room 101', 40),
(2, 1, 1, 'B', 'Room 102', 40),
(3, 2, 1, 'A', 'Room 103', 40),
(4, 3, 1, 'A', 'Room 104', 40),
(5, 4, 1, 'A', 'Room 105', 40),
(6, 5, 1, 'A', 'Room 201', 45),
(7, 6, 1, 'A', 'Room 202', 45),
(8, 7, 1, 'A', 'Room 203', 45),
(9, 8, 1, 'A', 'Room 204', 45),
(10, 2, 2, 'A', 'Room 103', 40)
ON DUPLICATE KEY UPDATE room_number=VALUES(room_number);

-- 5. Seed Ethiopian National Elementary Curriculum Subjects
INSERT INTO subjects (id, code, name_en, name_am, grade_level_id, pass_mark, max_mark) VALUES
-- Grade 1 Subjects
(1, 'G1-AMH', 'Amharic Language', 'አማርኛ ቋንቋ', 1, 50.00, 100.00),
(2, 'G1-ENG', 'English Language', 'እንግሊዝኛ ቋንቋ', 1, 50.00, 100.00),
(3, 'G1-MATH', 'Mathematics', 'ሂሳብ', 1, 50.00, 100.00),
(4, 'G1-ENV', 'Environmental Science', 'አካባቢ ሳይንስ', 1, 50.00, 100.00),
(5, 'G1-ART', 'Visual & Performing Arts', 'ኪነ-ጥበባት', 1, 50.00, 100.00),
(6, 'G1-PHE', 'Physical & Health Education', 'የሰውነት ማጎልመሻና ጤና', 1, 50.00, 100.00),

-- Grade 4 Subjects
(7, 'G4-AMH', 'Amharic Language', 'አማርኛ ቋንቋ', 4, 50.00, 100.00),
(8, 'G4-ENG', 'English Language', 'እንግሊዝኛ ቋንቋ', 4, 50.00, 100.00),
(9, 'G4-MATH', 'Mathematics', 'ሂሳብ', 4, 50.00, 100.00),
(10, 'G4-ENV', 'Environmental Science', 'አካባቢ ሳይንስ', 4, 50.00, 100.00),

-- Grade 7 & 8 Subjects
(11, 'G7-AMH', 'Amharic Language', 'አማርኛ ቋንቋ', 7, 50.00, 100.00),
(12, 'G7-ENG', 'English Language', 'እንግሊዝኛ ቋንቋ', 7, 50.00, 100.00),
(13, 'G7-MATH', 'Mathematics', 'ሂሳብ', 7, 50.00, 100.00),
(14, 'G7-GSCI', 'General Science', 'አጠቃላይ ሳይንስ', 7, 50.00, 100.00),
(15, 'G7-SOC', 'Social Studies', 'ህብረተሰብ ሳይንስ', 7, 50.00, 100.00),
(16, 'G7-CIT', 'Citizenship Education', 'የዜግነት ትምህርት', 7, 50.00, 100.00),
(17, 'G7-IT', 'Information Technology', 'የኢንፎርሜሽን ቴክኖሎጂ', 7, 50.00, 100.00),

(18, 'G8-AMH', 'Amharic Language', 'አማርኛ ቋንቋ', 8, 50.00, 100.00),
(19, 'G8-ENG', 'English Language', 'እንግሊዝኛ ቋንቋ', 8, 50.00, 100.00),
(20, 'G8-MATH', 'Mathematics', 'ሂሳብ', 8, 50.00, 100.00),
(21, 'G8-GSCI', 'General Science', 'አጠቃላይ ሳይንስ', 8, 50.00, 100.00),
(22, 'G8-SOC', 'Social Studies', 'ህብረተሰብ ሳይንስ', 8, 50.00, 100.00),
(23, 'G8-CIT', 'Citizenship Education', 'የዜግነት ትምህርት', 8, 50.00, 100.00),
(24, 'G8-IT', 'Information Technology', 'የኢንፎርሜሽን ቴክኖሎጂ', 8, 50.00, 100.00)
ON DUPLICATE KEY UPDATE name_en=VALUES(name_en), name_am=VALUES(name_am);

-- 6. Seed Configurable Grading Policy (Ethiopian Primary Assessment Standards)
-- Tests: 20%, Midterm: 30%, Final Exam: 40%, Class Assignment & Participation: 10%
INSERT INTO grading_policies (id, school_id, academic_year_id, name, policy_json, is_active)
VALUES (1, 1, 1, 'Standard Ethiopian Primary Grading Scale 2026/2027', 
'{"categories":[{"name":"Tests","weight":20},{"name":"Midterm","weight":30},{"name":"Final Exam","weight":40},{"name":"Class Activity","weight":10}],"scales":[{"min":90,"max":100,"grade":"A","desc":"Excellent (እጅግ በጣም ጥሩ)"},{"min":80,"max":89.99,"grade":"B","desc":"Very Good (በጣም ጥሩ)"},{"min":65,"max":79.99,"grade":"C","desc":"Good (ጥሩ)"},{"min":50,"max":64.99,"grade":"D","desc":"Satisfactory (አጥጋቢ)"},{"min":0,"max":49.99,"grade":"F","desc":"Unsatisfactory (ያልተሟላ)"}]}', 
TRUE)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 7. Seed School Announcements
INSERT INTO announcements (id, title_en, title_am, content_en, content_am, target_role, published_date, is_active)
VALUES 
(1, 
 'Welcome to Academic Year 2026/2027', 
 'እንኳን ለ2019 ዓ.ም የትምህርት ዘመን በሰላም አደረሳችሁ', 
 'Welcome all teachers, students, and parents to the new academic year. Please ensure class registrations are finalized.', 
 'ሁሉንም መምህራን፣ ተማሪዎችና ወላጆችን ለአዲሱ የትምህርት ዘመን እንኳን በደህና መጣችሁ እያልን የክፍል ምዝገባዎችን በአፋጣኝ እንዲያጠናቅቁ እናሳስባለን።', 
 'ALL', '2026-09-15', TRUE),
(2, 
 'Upcoming Midterm Assessment Week', 
 'የአጋማሽ ፈተና ሳምንት ማስታወቂያ', 
 'Midterm examinations for Grades 1 through 8 will commence in accordance with the school calendar.', 
 'ከክፍል 1 እስከ 8 ላሉ ተማሪዎች የአጋማሽ ፈተናዎች በትምህርት ቤቱ መርሃ-ግብር መሰረት የሚሰጡ መሆኑን እንገልጻለን።', 
 'ALL', '2026-10-20', TRUE)
ON DUPLICATE KEY UPDATE title_en=VALUES(title_en);
