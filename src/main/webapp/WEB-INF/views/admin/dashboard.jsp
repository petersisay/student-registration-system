<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*" %>
<%@ page import="util.I18nUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    String currentLocale = (String) session.getAttribute("currentLocale");
    if (currentLocale == null) currentLocale = "en";
    String ctx = request.getContextPath();

    AcademicYear year = (AcademicYear) request.getAttribute("currentYear");
    Map<String, Object> stats = (Map<String, Object>) request.getAttribute("stats");
    Map<String, Integer> todayAtt = (Map<String, Integer>) request.getAttribute("todayAttendance");
    List<Student> recentStudents = (List<Student>) request.getAttribute("recentStudents");
    List<AuditLog> auditLogs = (List<AuditLog>) request.getAttribute("auditLogs");
    List<Announcement> announcements = (List<Announcement>) request.getAttribute("announcements");

    int totalStudents = (stats != null && stats.get("totalStudents") != null) ? (int) stats.get("totalStudents") : 0;
    int totalTeachers = (stats != null && stats.get("totalTeachers") != null) ? (int) stats.get("totalTeachers") : 0;
    int totalClasses = (stats != null && stats.get("totalClasses") != null) ? (int) stats.get("totalClasses") : 0;
    int totalSubjects = (stats != null && stats.get("totalSubjects") != null) ? (int) stats.get("totalSubjects") : 0;
    Map<Integer, Integer> gradeDist = (stats != null) ? (Map<Integer, Integer>) stats.get("gradeDistribution") : null;
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<!-- Page Title & Header -->
<div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: var(--primary-navy);">
            <i class="bi bi-shield-lock-fill text-primary"></i> <%= I18nUtil.getMessage(currentLocale, "nav.dashboard") %>
        </h2>
        <p class="text-muted mb-0">
            <%= I18nUtil.getMessage(currentLocale, "app.name") %> &bull; <%= year != null ? year.getName() : "2026/2027" %>
        </p>
    </div>
    <div class="d-flex gap-2">
        <a href="<%= ctx %>/students?action=new" class="btn btn-shine btn-shine-primary">
            <i class="bi bi-person-plus-fill"></i> <%= I18nUtil.getMessage(currentLocale, "nav.student_register") %>
        </a>
        <a href="<%= ctx %>/attendance" class="btn btn-shine btn-shine-teal">
            <i class="bi bi-calendar-check-fill"></i> <%= I18nUtil.getMessage(currentLocale, "btn.take_attendance") %>
        </a>
    </div>
</div>

<!-- Animated Metric Cards -->
<div class="row g-3 mb-4">
    <div class="col-sm-6 col-lg-3">
        <div class="stat-card">
            <div class="stat-icon stat-icon-blue">
                <i class="bi bi-people-fill"></i>
            </div>
            <div>
                <div class="stat-number" data-target="<%= totalStudents %>"><%= totalStudents %></div>
                <div class="stat-label">Enrolled Students (ተማሪዎች)</div>
            </div>
        </div>
    </div>
    <div class="col-sm-6 col-lg-3">
        <div class="stat-card">
            <div class="stat-icon stat-icon-teal">
                <i class="bi bi-person-badge-fill"></i>
            </div>
            <div>
                <div class="stat-number" data-target="<%= totalTeachers %>"><%= totalTeachers %></div>
                <div class="stat-label">Certified Teachers (መምህራን)</div>
            </div>
        </div>
    </div>
    <div class="col-sm-6 col-lg-3">
        <div class="stat-card">
            <div class="stat-icon stat-icon-gold">
                <i class="bi bi-building"></i>
            </div>
            <div>
                <div class="stat-number" data-target="<%= totalClasses %>"><%= totalClasses %></div>
                <div class="stat-label">Class Sections (ክፍሎች)</div>
            </div>
        </div>
    </div>
    <div class="col-sm-6 col-lg-3">
        <div class="stat-card">
            <div class="stat-icon stat-icon-navy">
                <i class="bi bi-journal-bookmark-fill"></i>
            </div>
            <div>
                <div class="stat-number" data-target="<%= totalSubjects %>"><%= totalSubjects %></div>
                <div class="stat-label">Curriculum Subjects (ትምህርቶች)</div>
            </div>
        </div>
    </div>
</div>

<div class="row g-4 mb-4">
    <!-- Today's Attendance Overview -->
    <div class="col-lg-6">
        <div class="card-modern h-100">
            <div class="card-header-clean">
                <h5 class="card-title-clean">
                    <i class="bi bi-pie-chart-fill text-teal"></i> <%= I18nUtil.getMessage(currentLocale, "attendance.today_overview") %>
                </h5>
                <span class="badge bg-light text-muted border">Today: <%= new java.sql.Date(System.currentTimeMillis()) %></span>
            </div>
            <div class="row g-3 text-center my-auto">
                <div class="col-3">
                    <div class="p-3 rounded bg-light border">
                        <span class="badge-status badge-present mb-2"><i class="bi bi-check-circle"></i> <%= I18nUtil.getMessage(currentLocale, "attendance.present") %></span>
                        <h3 class="fw-bold text-success mb-0"><%= todayAtt != null ? todayAtt.getOrDefault("PRESENT", 0) : 0 %></h3>
                    </div>
                </div>
                <div class="col-3">
                    <div class="p-3 rounded bg-light border">
                        <span class="badge-status badge-absent mb-2"><i class="bi bi-x-circle"></i> <%= I18nUtil.getMessage(currentLocale, "attendance.absent") %></span>
                        <h3 class="fw-bold text-danger mb-0"><%= todayAtt != null ? todayAtt.getOrDefault("ABSENT", 0) : 0 %></h3>
                    </div>
                </div>
                <div class="col-3">
                    <div class="p-3 rounded bg-light border">
                        <span class="badge-status badge-late mb-2"><i class="bi bi-clock-history"></i> <%= I18nUtil.getMessage(currentLocale, "attendance.late") %></span>
                        <h3 class="fw-bold text-warning mb-0"><%= todayAtt != null ? todayAtt.getOrDefault("LATE", 0) : 0 %></h3>
                    </div>
                </div>
                <div class="col-3">
                    <div class="p-3 rounded bg-light border">
                        <span class="badge-status badge-excused mb-2"><i class="bi bi-info-circle"></i> <%= I18nUtil.getMessage(currentLocale, "attendance.excused") %></span>
                        <h3 class="fw-bold text-info mb-0"><%= todayAtt != null ? todayAtt.getOrDefault("EXCUSED", 0) : 0 %></h3>
                    </div>
                </div>
            </div>
            <div class="mt-4 pt-3 border-top d-flex justify-content-between align-items-center">
                <small class="text-muted">Recorded daily by homeroom teachers & attendance officers.</small>
                <a href="<%= ctx %>/attendance" class="btn btn-sm btn-shine btn-shine-outline">Open Attendance Panel</a>
            </div>
        </div>
    </div>

    <!-- Student Distribution by Grade (Grades 1 to 8) -->
    <div class="col-lg-6">
        <div class="card-modern h-100">
            <div class="card-header-clean">
                <h5 class="card-title-clean">
                    <i class="bi bi-bar-chart-steps text-primary"></i> Elementary Grade Levels (ከክፍል 1 - 8)
                </h5>
                <span class="badge bg-primary">Grades 1 - 8</span>
            </div>
            <div class="table-responsive">
                <table class="table table-clean">
                    <thead>
                        <tr>
                            <th>Grade Level</th>
                            <th>Amharic Name</th>
                            <th class="text-end">Enrolled Students</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (int g = 1; g <= 8; g++) {
                            int count = (gradeDist != null) ? gradeDist.getOrDefault(g, 0) : 0;
                        %>
                            <tr>
                                <td class="fw-bold text-navy">Grade <%= g %></td>
                                <td class="text-muted">ክፍል <%= g %></td>
                                <td class="text-end fw-semibold">
                                    <span class="badge bg-light text-dark border px-2 py-1"><%= count %> Students</span>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Recent Registrations & Audit Activity -->
<div class="row g-4">
    <!-- Recent Student Admissions -->
    <div class="col-lg-7">
        <div class="card-modern">
            <div class="card-header-clean">
                <h5 class="card-title-clean">
                    <i class="bi bi-person-lines-fill text-success"></i> Recent Student Admissions
                </h5>
                <a href="<%= ctx %>/students" class="btn btn-sm btn-link text-decoration-none">View All Directory &rarr;</a>
            </div>
            <div class="table-responsive">
                <table class="table table-clean">
                    <thead>
                        <tr>
                            <th>ID Number</th>
                            <th>Student Name</th>
                            <th>Grade</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (recentStudents != null && !recentStudents.isEmpty()) {
                            for (Student s : recentStudents) { %>
                                <tr>
                                    <td><code><%= s.getStudentIdNumber() != null ? s.getStudentIdNumber() : "STD-" + s.getId() %></code></td>
                                    <td>
                                        <div class="fw-semibold text-navy"><%= s.getDisplayName(currentLocale) %></div>
                                        <small class="text-muted"><%= s.getEmail() != null ? s.getEmail() : "" %></small>
                                    </td>
                                    <td><%= s.getCurrentGradeName() != null ? s.getCurrentGradeName() : "Grade " + s.getYear() %></td>
                                    <td>
                                        <span class="badge-status badge-active"><%= s.getStatus() %></span>
                                    </td>
                                </tr>
                            <% }
                        } else { %>
                            <tr><td colspan="4" class="text-center text-muted py-3">No registrations found.</td></tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Authorized Audit Logs -->
    <div class="col-lg-5">
        <div class="card-modern">
            <div class="card-header-clean">
                <h5 class="card-title-clean">
                    <i class="bi bi-clock-history text-secondary"></i> Authorized Audit Logs
                </h5>
                <span class="badge bg-light text-dark border">Security</span>
            </div>
            <ul class="list-group list-group-flush small">
                <% if (auditLogs != null && !auditLogs.isEmpty()) {
                    for (AuditLog log : auditLogs) { %>
                        <li class="list-group-item px-0 py-2 d-flex justify-content-between align-items-start border-bottom">
                            <div>
                                <span class="badge bg-secondary mb-1"><%= log.getAction() %></span>
                                <div class="text-dark"><%= log.getDetails() != null ? log.getDetails() : log.getEntityType() %></div>
                            </div>
                            <small class="text-muted text-nowrap"><%= log.getCreatedAt() != null ? log.getCreatedAt().toString().substring(11, 16) : "" %></small>
                        </li>
                    <% }
                } else { %>
                    <li class="list-group-item text-center text-muted py-3">No recent logs recorded.</li>
                <% } %>
            </ul>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
