<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*" %>
<%@ page import="util.I18nUtil" %>
<%@ page import="java.util.List" %>
<%
    String currentLocale = (String) session.getAttribute("currentLocale");
    if (currentLocale == null) currentLocale = "en";
    String ctx = request.getContextPath();

    Teacher teacher = (Teacher) request.getAttribute("teacher");
    List<TeacherAssignment> assignments = (List<TeacherAssignment>) request.getAttribute("assignments");
    List<TimetableEntry> timetable = (List<TimetableEntry>) request.getAttribute("timetable");
    AcademicYear year = (AcademicYear) request.getAttribute("currentYear");
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<!-- Teacher Profile Banner -->
<div class="card-modern mb-4 bg-white" style="border-left: 6px solid var(--accent-teal);">
    <div class="d-flex flex-wrap justify-content-between align-items-center gap-3">
        <div class="d-flex align-items-center gap-3">
            <div class="stat-icon stat-icon-teal fs-2">
                <i class="bi bi-person-workspace"></i>
            </div>
            <div>
                <h3 class="fw-bold mb-1" style="color: var(--primary-navy);">
                    <%= teacher != null ? teacher.getDisplayName(currentLocale) : "Teacher Portal" %>
                </h3>
                <p class="text-muted mb-0">
                    <span class="badge bg-light text-dark border"><%= teacher != null ? teacher.getEmployeeId() : "" %></span>
                    &bull; Specialization: <strong><%= teacher != null ? teacher.getSpecialization() : "Elementary Education" %></strong>
                    &bull; <%= year != null ? year.getName() : "2026/2027" %>
                </p>
            </div>
        </div>
        <div class="d-flex gap-2">
            <a href="<%= ctx %>/attendance" class="btn btn-shine btn-shine-teal">
                <i class="bi bi-calendar-check-fill"></i> <%= I18nUtil.getMessage(currentLocale, "btn.take_attendance") %>
            </a>
            <a href="<%= ctx %>/assessments" class="btn btn-shine btn-shine-primary">
                <i class="bi bi-pencil-square"></i> <%= I18nUtil.getMessage(currentLocale, "btn.enter_marks") %>
            </a>
        </div>
    </div>
</div>

<div class="row g-4 mb-4">
    <!-- Authorized Class & Subject Assignments -->
    <div class="col-lg-7">
        <div class="card-modern h-100">
            <div class="card-header-clean">
                <h5 class="card-title-clean">
                    <i class="bi bi-card-checklist text-primary"></i> My Assigned Classes & Subjects
                </h5>
                <span class="badge bg-light text-muted border">Explicit Authorization</span>
            </div>
            <p class="text-muted small">
                You are strictly authorized to view students, take attendance, and record assessment scores for these specific classes and subjects:
            </p>
            <div class="table-responsive">
                <table class="table table-clean">
                    <thead>
                        <tr>
                            <th>Class Section</th>
                            <th>Curriculum Subject</th>
                            <th>Role</th>
                            <th class="text-end">Quick Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (assignments != null && !assignments.isEmpty()) {
                            for (TeacherAssignment a : assignments) { %>
                                <tr>
                                    <td class="fw-bold text-navy"><%= a.getFullClassName() %></td>
                                    <td><%= a.getSubjectName() != null ? a.getSubjectName() : "<span class='text-muted'>All Homeroom Subjects</span>" %></td>
                                    <td>
                                        <% if (a.isPrimaryHomeroom()) { %>
                                            <span class="badge bg-success">Homeroom Teacher</span>
                                        <% } else { %>
                                            <span class="badge bg-light text-dark border">Subject Teacher</span>
                                        <% } %>
                                    </td>
                                    <td class="text-end">
                                        <a href="<%= ctx %>/attendance?classId=<%= a.getClassSectionId() %>" class="btn btn-sm btn-outline-success">
                                            <i class="bi bi-check2-circle"></i> Attendance
                                        </a>
                                        <a href="<%= ctx %>/assessments?classId=<%= a.getClassSectionId() %><%= a.getSubjectId() != null ? "&subjectId=" + a.getSubjectId() : "" %>" class="btn btn-sm btn-outline-primary ms-1">
                                            <i class="bi bi-pencil"></i> Marks
                                        </a>
                                    </td>
                                </tr>
                            <% }
                        } else { %>
                            <tr><td colspan="4" class="text-center text-muted py-3">No active teaching assignments found for current year.</td></tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Teaching Timetable -->
    <div class="col-lg-5">
        <div class="card-modern h-100">
            <div class="card-header-clean">
                <h5 class="card-title-clean">
                    <i class="bi bi-clock-history text-gold"></i> Today's Schedule
                </h5>
                <span class="badge bg-warning text-dark">Timetable</span>
            </div>
            <% if (timetable != null && !timetable.isEmpty()) { %>
                <div class="list-group list-group-flush">
                    <% for (TimetableEntry tt : timetable) { %>
                        <div class="list-group-item px-0 py-3 d-flex justify-content-between align-items-center border-bottom">
                            <div>
                                <span class="badge bg-light text-primary border mb-1">Period <%= tt.getPeriodNumber() %> &bull; <%= tt.getStartTime() %> - <%= tt.getEndTime() %></span>
                                <div class="fw-bold text-navy fs-6"><%= tt.getSubjectName() %></div>
                                <small class="text-muted"><i class="bi bi-geo-alt"></i> <%= tt.getClassName() %></small>
                            </div>
                            <span class="badge bg-primary px-3 py-2"><%= tt.getLocalizedDay(currentLocale) %></span>
                        </div>
                    <% } %>
                </div>
            <% } else { %>
                <div class="text-center py-5 text-muted">
                    <i class="bi bi-calendar2-x fs-1 d-block mb-2 text-secondary"></i>
                    No scheduled timetable periods configured yet.
                </div>
            <% } %>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
