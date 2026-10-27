<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*" %>
<%@ page import="util.I18nUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    String currentLocale = (String) session.getAttribute("currentLocale");
    if (currentLocale == null) currentLocale = "en";
    String ctx = request.getContextPath();

    Student student = (Student) request.getAttribute("student");
    Map<String, Integer> attSummary = (Map<String, Integer>) request.getAttribute("attendanceSummary");
    ReportCard reportCard = (ReportCard) request.getAttribute("reportCard");
    List<StudentEnrollment> history = (List<StudentEnrollment>) request.getAttribute("enrollmentHistory");
    List<TimetableEntry> timetable = (List<TimetableEntry>) request.getAttribute("timetable");
    AcademicYear year = (AcademicYear) request.getAttribute("currentYear");
    AcademicTerm term = (AcademicTerm) request.getAttribute("currentTerm");
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<!-- Friendly Child-Themed Student Welcome Banner -->
<div class="card-modern mb-4" style="background: linear-gradient(135deg, #17324D 0%, #2563EB 100%); color: #FFFFFF; border: none;">
    <div class="d-flex flex-wrap justify-content-between align-items-center gap-3">
        <div class="d-flex align-items-center gap-3">
            <div style="width: 64px; height: 64px; background: rgba(255,255,255,0.2); border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 2rem;">
                🎓
            </div>
            <div>
                <h3 class="fw-bold mb-1 text-white">
                    <%= student != null ? student.getDisplayName(currentLocale) : "Student Portal" %>
                </h3>
                <p class="mb-0 text-white-50">
                    <span class="badge bg-warning text-dark"><%= student != null ? student.getStudentIdNumber() : "" %></span>
                    &bull; <%= student != null && student.getCurrentGradeName() != null ? student.getCurrentGradeName() : "Grade " + (student != null ? student.getYear() : "4") %>
                    &bull; Section: <strong><%= student != null && student.getCurrentSectionName() != null ? student.getCurrentSectionName() : "A" %></strong>
                </p>
            </div>
        </div>
        <div>
            <span class="badge bg-light text-dark px-3 py-2 fs-6">
                <i class="bi bi-calendar-event"></i> <%= year != null ? year.getName() : "2026/2027" %>
            </span>
        </div>
    </div>
</div>

<div class="row g-4 mb-4">
    <!-- Attendance Summary Card -->
    <div class="col-lg-4">
        <div class="card-modern h-100">
            <div class="card-header-clean">
                <h5 class="card-title-clean">
                    <i class="bi bi-calendar-check text-teal"></i> My Attendance
                </h5>
                <span class="badge bg-light text-dark border">This Year</span>
            </div>
            <div class="row g-2 text-center my-auto">
                <div class="col-4">
                    <div class="p-3 rounded bg-light border">
                        <div class="fs-4 fw-bold text-success"><%= attSummary != null ? attSummary.getOrDefault("PRESENT", 0) : 0 %></div>
                        <small class="text-muted"><%= I18nUtil.getMessage(currentLocale, "attendance.present") %></small>
                    </div>
                </div>
                <div class="col-4">
                    <div class="p-3 rounded bg-light border">
                        <div class="fs-4 fw-bold text-danger"><%= attSummary != null ? attSummary.getOrDefault("ABSENT", 0) : 0 %></div>
                        <small class="text-muted"><%= I18nUtil.getMessage(currentLocale, "attendance.absent") %></small>
                    </div>
                </div>
                <div class="col-4">
                    <div class="p-3 rounded bg-light border">
                        <div class="fs-4 fw-bold text-warning"><%= attSummary != null ? attSummary.getOrDefault("LATE", 0) : 0 %></div>
                        <small class="text-muted"><%= I18nUtil.getMessage(currentLocale, "attendance.late") %></small>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Official Published Academic Results Card -->
    <div class="col-lg-8">
        <div class="card-modern h-100">
            <div class="card-header-clean">
                <h5 class="card-title-clean">
                    <i class="bi bi-award-fill text-gold"></i> Official Academic Report Card
                </h5>
                <% if (reportCard != null && reportCard.isPublished()) { %>
                    <span class="badge-status badge-published"><i class="bi bi-check-all"></i> Published</span>
                <% } else { %>
                    <span class="badge-status badge-draft"><i class="bi bi-hourglass-split"></i> Pending Teacher Approval</span>
                <% } %>
            </div>

            <% if (reportCard != null && reportCard.isPublished()) { %>
                <div class="row g-3 mb-3 text-center">
                    <div class="col-sm-4">
                        <div class="p-2 border rounded bg-light">
                            <small class="text-muted">Total Score</small>
                            <h4 class="fw-bold text-primary mb-0"><%= reportCard.getTotalScore() %></h4>
                        </div>
                    </div>
                    <div class="col-sm-4">
                        <div class="p-2 border rounded bg-light">
                            <small class="text-muted">Grade Average</small>
                            <h4 class="fw-bold text-success mb-0"><%= reportCard.getAverageScore() %>%</h4>
                        </div>
                    </div>
                    <div class="col-sm-4">
                        <div class="p-2 border rounded bg-light">
                            <small class="text-muted">Class Rank</small>
                            <h4 class="fw-bold text-navy mb-0">#<%= reportCard.getRankInClass() %></h4>
                        </div>
                    </div>
                </div>

                <div class="table-responsive mb-3">
                    <table class="table table-clean">
                        <thead>
                            <tr>
                                <th>Subject</th>
                                <th class="text-center">Score</th>
                                <th class="text-center">Grade</th>
                                <th>Remark</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (ReportCardItem item : reportCard.getItems()) { %>
                                <tr>
                                    <td class="fw-bold text-navy"><%= item.getSubjectDisplayName(currentLocale) %></td>
                                    <td class="text-center fw-semibold"><%= item.getFinalScore() %></td>
                                    <td class="text-center"><span class="badge bg-primary px-2"><%= item.getLetterGrade() %></span></td>
                                    <td class="small text-muted"><%= item.getRemark() %></td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>

                <div class="text-end">
                    <a href="<%= ctx %>/reports?action=view&studentId=<%= student.getId() %>&termId=<%= reportCard.getTermId() %>" class="btn btn-shine btn-shine-primary btn-sm">
                        <i class="bi bi-printer"></i> View & Print Official Report Card
                    </a>
                </div>
            <% } else { %>
                <div class="text-center py-4 text-muted">
                    <i class="bi bi-journal-text fs-1 text-secondary d-block mb-2"></i>
                    Assessment marks are currently being entered and reviewed by teachers.
                    Your official report card will appear here once finalized and published by the school administration.
                </div>
            <% } %>
        </div>
    </div>
</div>

<!-- Academic Progression History & Daily Timetable -->
<div class="row g-4">
    <!-- Academic Progression (Preserving Multi-Year History) -->
    <div class="col-lg-6">
        <div class="card-modern">
            <div class="card-header-clean">
                <h5 class="card-title-clean">
                    <i class="bi bi-clock-history text-primary"></i> Academic Progression History
                </h5>
                <span class="badge bg-light text-dark border">Multi-Year Records</span>
            </div>
            <div class="table-responsive">
                <table class="table table-clean">
                    <thead>
                        <tr>
                            <th>Academic Year</th>
                            <th>Grade & Section</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (history != null && !history.isEmpty()) {
                            for (StudentEnrollment se : history) { %>
                                <tr>
                                    <td class="fw-bold text-navy"><%= se.getAcademicYearName() %></td>
                                    <td><%= se.getFullClassName() %></td>
                                    <td><span class="badge-status badge-active"><%= se.getStatus() %></span></td>
                                </tr>
                            <% }
                        } else { %>
                            <tr><td colspan="3" class="text-center text-muted">No historical records available.</td></tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Class Timetable -->
    <div class="col-lg-6">
        <div class="card-modern">
            <div class="card-header-clean">
                <h5 class="card-title-clean">
                    <i class="bi bi-calendar3 text-teal"></i> Class Timetable
                </h5>
                <span class="badge bg-teal text-white">Daily Schedule</span>
            </div>
            <% if (timetable != null && !timetable.isEmpty()) { %>
                <ul class="list-group list-group-flush">
                    <% for (TimetableEntry tt : timetable) { %>
                        <li class="list-group-item px-0 py-2 d-flex justify-content-between align-items-center">
                            <div>
                                <span class="badge bg-light text-dark border me-2">Period <%= tt.getPeriodNumber() %></span>
                                <span class="fw-bold text-navy"><%= tt.getSubjectName() %></span>
                                <small class="text-muted ms-2">(<%= tt.getStartTime() %> - <%= tt.getEndTime() %>)</small>
                            </div>
                            <span class="badge bg-primary"><%= tt.getLocalizedDay(currentLocale) %></span>
                        </li>
                    <% } %>
                </ul>
            <% } else { %>
                <div class="text-center py-4 text-muted">No timetable entries configured.</div>
            <% } %>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
