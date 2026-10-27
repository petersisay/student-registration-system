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
    AcademicTerm term = (AcademicTerm) request.getAttribute("currentTerm");
    Map<String, Object> stats = (Map<String, Object>) request.getAttribute("stats");
    Map<String, Integer> todayAtt = (Map<String, Integer>) request.getAttribute("todayAttendance");
    List<ClassSection> sections = (List<ClassSection>) request.getAttribute("sections");
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="card-modern mb-4 bg-white" style="border-left: 6px solid var(--primary-navy);">
    <div class="d-flex flex-wrap justify-content-between align-items-center gap-3">
        <div>
            <h2 class="fw-bold mb-1" style="color: var(--primary-navy);">
                <i class="bi bi-mortarboard-fill text-primary"></i> School Principal Academic Oversight
            </h2>
            <p class="text-muted mb-0">
                <%= I18nUtil.getMessage(currentLocale, "app.name") %> &bull; <%= year != null ? year.getName() : "2026/2027" %> &bull; <%= term != null ? term.getName() : "Semester 1" %>
            </p>
        </div>
        <div class="d-flex gap-2">
            <a href="<%= ctx %>/reports" class="btn btn-shine btn-shine-primary">
                <i class="bi bi-award-fill"></i> Review & Approve Report Cards
            </a>
        </div>
    </div>
</div>

<div class="row g-4 mb-4">
    <div class="col-lg-6">
        <div class="card-modern h-100">
            <div class="card-header-clean">
                <h5 class="card-title-clean"><i class="bi bi-diagram-3-fill text-teal"></i> Elementary Class Sections</h5>
                <span class="badge bg-primary">Grades 1-8</span>
            </div>
            <div class="table-responsive">
                <table class="table table-clean">
                    <thead>
                        <tr>
                            <th>Class Section</th>
                            <th>Room</th>
                            <th class="text-end">Enrolled Students</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (sections != null && !sections.isEmpty()) {
                            for (ClassSection cs : sections) { %>
                                <tr>
                                    <td class="fw-bold text-navy"><%= cs.getFullClassName() %></td>
                                    <td><%= cs.getRoomNumber() != null ? cs.getRoomNumber() : "Room 101" %></td>
                                    <td class="text-end"><span class="badge bg-light text-dark border"><%= cs.getStudentCount() %> Students</span></td>
                                </tr>
                            <% }
                        } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="col-lg-6">
        <div class="card-modern h-100">
            <div class="card-header-clean">
                <h5 class="card-title-clean"><i class="bi bi-calendar-check-fill text-success"></i> School Attendance Today</h5>
                <span class="badge bg-success">Today</span>
            </div>
            <div class="row g-3 text-center my-auto">
                <div class="col-6">
                    <div class="p-3 border rounded bg-light">
                        <span class="badge-status badge-present mb-2">Present</span>
                        <h2 class="fw-bold text-success mb-0"><%= todayAtt != null ? todayAtt.getOrDefault("PRESENT", 0) : 0 %></h2>
                    </div>
                </div>
                <div class="col-6">
                    <div class="p-3 border rounded bg-light">
                        <span class="badge-status badge-absent mb-2">Absent</span>
                        <h2 class="fw-bold text-danger mb-0"><%= todayAtt != null ? todayAtt.getOrDefault("ABSENT", 0) : 0 %></h2>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
