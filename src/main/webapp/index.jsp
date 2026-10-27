<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.User" %>
<%@ page import="model.Role" %>
<%@ page import="util.I18nUtil" %>
<%
    // If user is already authenticated, immediately route to their role dashboard
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser != null && currentUser.getRole() != null) {
        String ctx = request.getContextPath();
        Role role = currentUser.getRole();
        if (role == Role.ADMIN) {
            response.sendRedirect(ctx + "/admin/dashboard");
            return;
        } else if (role == Role.TEACHER) {
            response.sendRedirect(ctx + "/teacher/dashboard");
            return;
        } else if (role == Role.STUDENT) {
            response.sendRedirect(ctx + "/student/portal");
            return;
        } else if (role == Role.PRINCIPAL) {
            response.sendRedirect(ctx + "/principal/dashboard");
            return;
        } else if (role == Role.REGISTRAR) {
            response.sendRedirect(ctx + "/registrar/dashboard");
            return;
        } else if (role == Role.ATTENDANCE_OFFICER) {
            response.sendRedirect(ctx + "/attendance");
            return;
        }
    }

    String currentLocale = (String) session.getAttribute("currentLocale");
    if (request.getParameter("lang") != null) {
        currentLocale = request.getParameter("lang");
    }
    if (currentLocale == null || currentLocale.isEmpty()) {
        currentLocale = "en";
    }
    boolean isAmharic = "am".equalsIgnoreCase(currentLocale);
%>
<!DOCTYPE html>
<html lang="<%= currentLocale %>">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= isAmharic ? "ቡሌ ሆራ የመጀመሪያ ደረጃ ትምህርት ቤት (1ኛ - 8ኛ ክፍል)" : "Bule Hora Elementary School (Grades 1–8)" %></title>
    <!-- Bootstrap 5.3 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <!-- Custom Design System -->
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
    <style>
        .feature-box {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(8px);
            border-radius: 16px;
            padding: 2rem 1.5rem;
            border: 1px solid var(--border-color);
            transition: all 0.3s ease;
            height: 100%;
        }
        .feature-box:hover {
            transform: translateY(-6px);
            box-shadow: 0 12px 28px rgba(23, 50, 77, 0.12);
            border-color: var(--primary-blue);
        }
        .feature-icon-wrapper {
            width: 56px;
            height: 56px;
            border-radius: 14px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.6rem;
            margin-bottom: 1.25rem;
        }
        .grade-badge {
            display: inline-block;
            background: rgba(37, 99, 235, 0.08);
            color: var(--primary-blue);
            font-weight: 600;
            padding: 0.35rem 0.75rem;
            border-radius: 8px;
            border: 1px solid rgba(37, 99, 235, 0.2);
            margin: 0.2rem;
            font-size: 0.85rem;
        }
    </style>
</head>
<body>

<!-- Movable and Animated School Landscape Background -->
<div class="movable-bg-wrapper">
    <div class="movable-bg-img"></div>
    <div class="movable-bg-overlay"></div>
</div>

<!-- Header / Navigation -->
<nav class="navbar navbar-expand-lg navbar-custom sticky-top">
    <div class="container">
        <a class="navbar-brand d-flex align-items-center gap-2" href="<%= request.getContextPath() %>/index.jsp">
            <img src="<%= request.getContextPath() %>/images/bule-hora-logo.png" alt="Bule Hora Logo" class="school-logo-img">
            <span><%= isAmharic ? "ቡሌ ሆራ የመጀመሪያ ደረጃ ትምህርት ቤት" : "Bule Hora Elementary School" %></span>
        </a>

        <button class="navbar-toggler text-white border-0" type="button" data-bs-toggle="collapse" data-bs-target="#publicNav">
            <i class="bi bi-list fs-2 text-white"></i>
        </button>

        <div class="collapse navbar-collapse" id="publicNav">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link-custom active" href="<%= request.getContextPath() %>/index.jsp">
                        <i class="bi bi-house-door"></i> <%= isAmharic ? "ዋና ገጽ" : "Home" %>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link-custom" href="<%= request.getContextPath() %>/students">
                        <i class="bi bi-people"></i> <%= isAmharic ? "የተማሪዎች ማውጫ" : "Student Directory" %>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link-custom" href="<%= request.getContextPath() %>/announcements">
                        <i class="bi bi-megaphone"></i> <%= isAmharic ? "ማስታወቂያዎች" : "Notices" %>
                    </a>
                </li>
            </ul>

            <div class="d-flex align-items-center gap-3">
                <!-- Bilingual Toggle -->
                <div class="lang-selector">
                    <a href="?lang=en" class="lang-btn <%= !isAmharic ? "active" : "" %>">EN</a>
                    <a href="?lang=am" class="lang-btn <%= isAmharic ? "active" : "" %>">አማ</a>
                </div>

                <!-- Sign In Action -->
                <a href="<%= request.getContextPath() %>/login" class="btn btn-shine btn-shine-gold text-dark fw-bold">
                    <i class="bi bi-box-arrow-in-right me-1"></i> <%= isAmharic ? "ወደ ፖርታል ግባ" : "Sign In to Portal" %>
                </a>
            </div>
        </div>
    </div>
</nav>

<!-- Hero Banner with Movable School Background & Bule Hora Logo -->
<section class="hero-banner-bule-hora text-center">
    <div class="hero-bg-anim"></div>
    <div class="hero-overlay-gradient"></div>
    <div class="container position-relative" style="z-index: 3;">
        <img src="<%= request.getContextPath() %>/images/bule-hora-logo.png" alt="Bule Hora School Logo" class="school-logo-lg">
        <h1 class="display-5 fw-bold mb-2 text-white">
            <%= isAmharic ? "ቡሌ ሆራ የመጀመሪያ ደረጃ ትምህርት ቤት" : "Bule Hora Elementary School" %>
        </h1>
        <p class="lead mb-4 mx-auto" style="max-width: 720px; color: #F1F5F9;">
            <%= isAmharic 
                ? "የ1ኛ እስከ 8ኛ ክፍል የተሟላ የትምህርት ቤት መረጃ አስተዳደር ሥርዓት - ተማሪዎች፣ ምዘና፣ የተገኝነት ክትትልና የውጤት ካርድ።" 
                : "Modern, bilingual elementary school management platform for Grades 1–8. Streamlining admissions, daily attendance, continuous assessments, and official report cards." 
            %>
        </p>
        <div class="d-flex justify-content-center gap-3 flex-wrap">
            <a href="<%= request.getContextPath() %>/login" class="btn btn-shine btn-shine-gold text-dark fw-bold px-4 py-3 fs-5 shadow">
                <i class="bi bi-shield-lock-fill me-2"></i><%= isAmharic ? "የሰራተኞችና የተማሪዎች መግቢያ" : "Staff & Student Portal" %>
            </a>
            <a href="<%= request.getContextPath() %>/students" class="btn btn-outline-light px-4 py-3 fs-5">
                <i class="bi bi-search me-2"></i><%= isAmharic ? "የተማሪዎችን ማውጫ ፈልግ" : "Browse Student Directory" %>
            </a>
        </div>
        <div class="mt-4 pt-2 text-warning small font-monospace">
            <i class="bi bi-geo-alt-fill me-1"></i>Bule Hora, West Guji Zone, Oromia, Ethiopia &bull; <%= isAmharic ? "የትምህርት ዘመን፡ 2019 ዓ.ም (2026/2027)" : "Academic Year: 2026/2027 (2019 E.C.)" %>
        </div>
    </div>
</section>

<!-- Curriculum & Structure (Grades 1-8) -->
<section class="py-5">
    <div class="container">
        <div class="text-center mb-5">
            <h2 class="fw-bold mb-2" style="color: var(--primary-navy);">
                <%= isAmharic ? "የ1ኛ እስከ 8ኛ ክፍል የትምህርት ደረጃዎች" : "Grades 1 through 8 Elementary Structure" %>
            </h2>
            <p class="text-muted">
                <%= isAmharic 
                    ? "በኢትዮጵያ የመጀመሪያ ደረጃ ትምህርት ሥርዓተ-ትምህርት መሠረት የተዋቀረ" 
                    : "Fully structured according to the Ethiopian Elementary Curriculum standards" 
                %>
            </p>
            <div class="d-flex justify-content-center flex-wrap gap-2 mt-3">
                <% for (int g = 1; g <= 8; g++) { %>
                    <span class="grade-badge">
                        <i class="bi bi-mortarboard-fill me-1"></i><%= isAmharic ? ("ክፍል " + g) : ("Grade " + g) %>
                    </span>
                <% } %>
            </div>
        </div>

        <div class="row g-4">
            <!-- Feature 1: Multi-Year Academic History -->
            <div class="col-md-4">
                <div class="feature-box">
                    <div class="feature-icon-wrapper" style="background: rgba(37, 99, 235, 0.1); color: var(--primary-blue);">
                        <i class="bi bi-clock-history"></i>
                    </div>
                    <h5 class="fw-bold mb-2" style="color: var(--primary-navy);">
                        <%= isAmharic ? "የዓመታት የትምህርት ታሪክ" : "Enrollment & History" %>
                    </h5>
                    <p class="text-muted small mb-0">
                        <%= isAmharic 
                            ? "ተማሪዎች ከ1ኛ ወደ 2ኛ ክፍል ሲዘዋወሩ የነበራቸው የቀደመ የውጤት፣ የተገኝነትና የምደባ ታሪክ ሳይጠፋ በቋሚነት ይጠበቃል።" 
                            : "Preserves multi-year academic progression. Advancing a student from Grade 1 to 2 archives previous enrollments without overwriting historical marks." 
                        %>
                    </p>
                </div>
            </div>

            <!-- Feature 2: Teacher Authorization Boundaries -->
            <div class="col-md-4">
                <div class="feature-box">
                    <div class="feature-icon-wrapper" style="background: rgba(15, 157, 138, 0.1); color: var(--teal);">
                        <i class="bi bi-shield-check"></i>
                    </div>
                    <h5 class="fw-bold mb-2" style="color: var(--primary-navy);">
                        <%= isAmharic ? "የመምህራን ትክክለኛ ምደባ" : "Teacher Authorization" %>
                    </h5>
                    <p class="text-muted small mb-0">
                        <%= isAmharic 
                            ? "መምህራን በትምህርት ቤቱ አስተዳዳሪ የተመደቡላቸውን ክፍሎችና የትምህርት ዓይነቶች ብቻ ማስተዳደርና ውጤት ማስገባት ይችላሉ።" 
                            : "Server-side boundary enforcement. Teachers can only record attendance and enter assessment marks for explicitly assigned classes and subjects." 
                        %>
                    </p>
                </div>
            </div>

            <!-- Feature 3: Official Printable Report Cards -->
            <div class="col-md-4">
                <div class="feature-box">
                    <div class="feature-icon-wrapper" style="background: rgba(244, 185, 66, 0.15); color: #B45309;">
                        <i class="bi bi-file-earmark-medical"></i>
                    </div>
                    <h5 class="fw-bold mb-2" style="color: var(--primary-navy);">
                        <%= isAmharic ? "ይፋዊ የውጤት ካርድ" : "Official Report Cards" %>
                    </h5>
                    <p class="text-muted small mb-0">
                        <%= isAmharic 
                            ? "የሁሉም የትምህርት ዓይነቶች ድምር፣ አማካይ ውጤት፣ የክፍል ደረጃ፣ የተገኝነት ቁጥርና የትምህርት ቤቱ ማህተም ያካተተ ዝግጁ ካርድ።" 
                            : "Calculated from active assessments and attendance totals, with letter grade thresholds (A-F), class ranks, and printable official seals." 
                        %>
                    </p>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Demo Accounts Helper -->
<section class="py-4 bg-light border-top border-bottom">
    <div class="container">
        <div class="p-4 bg-white rounded-3 border shadow-sm">
            <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3">
                <h5 class="fw-bold mb-0 text-navy" style="color: var(--primary-navy);">
                    <i class="bi bi-key-fill text-warning me-2"></i><%= isAmharic ? "የትምህርት ቤቱ መግቢያ መለያዎች (ለሙከራ)" : "School Portal Test Accounts" %>
                </h5>
                <a href="<%= request.getContextPath() %>/login" class="btn btn-sm btn-primary">
                    <i class="bi bi-box-arrow-in-right me-1"></i><%= isAmharic ? "አሁን ግባ" : "Sign In Now" %>
                </a>
            </div>
            <div class="row g-3">
                <div class="col-md-4">
                    <div class="p-3 border rounded bg-light">
                        <div class="fw-bold text-dark"><i class="bi bi-person-gear text-primary me-1"></i> Administrator</div>
                        <div class="small text-muted font-monospace mt-1">Username: <strong>admin</strong> | Password: <strong>admin123</strong></div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="p-3 border rounded bg-light">
                        <div class="fw-bold text-dark"><i class="bi bi-person-video3 text-success me-1"></i> Teacher (Abebe)</div>
                        <div class="small text-muted font-monospace mt-1">Username: <strong>teacher.abebe</strong> | Password: <strong>teacher123</strong></div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="p-3 border rounded bg-light">
                        <div class="fw-bold text-dark"><i class="bi bi-backpack text-info me-1"></i> Student (Dawit)</div>
                        <div class="small text-muted font-monospace mt-1">Username: <strong>student.dawit</strong> | Password: <strong>student123</strong></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Footer -->
<footer class="app-footer text-center">
    <div class="container">
        <img src="<%= request.getContextPath() %>/images/bule-hora-logo.png" alt="Bule Hora Logo" class="school-logo-img mx-auto mb-2" style="height: 38px;">
        <p class="mb-1 fw-bold text-navy" style="color: var(--primary-navy);">Bule Hora Elementary School (ቡሌ ሆራ የመጀመሪያ ደረጃ ትምህርት ቤት)</p>
        <p class="text-muted small mb-0">Bule Hora, West Guji Zone, Oromia, Ethiopia &bull; 2026 Elementary School Management System</p>
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= request.getContextPath() %>/js/app.js"></script>
</body>
</html>
