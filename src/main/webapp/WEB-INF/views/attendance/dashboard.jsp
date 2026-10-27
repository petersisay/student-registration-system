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
    Map<String, Integer> todayAtt = (Map<String, Integer>) request.getAttribute("todayAttendance");
    List<ClassSection> sections = (List<ClassSection>) request.getAttribute("sections");
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="card-modern mb-4 bg-white" style="border-left: 6px solid var(--accent-teal);">
    <div class="d-flex flex-wrap justify-content-between align-items-center gap-3">
        <div>
            <h2 class="fw-bold mb-1" style="color: var(--primary-navy);">
                <i class="bi bi-calendar-check-fill text-teal"></i> School Attendance Control Center
            </h2>
            <p class="text-muted mb-0">
                Daily Student Attendance Tracking &bull; Absentee Auditing &bull; Section Verification
            </p>
        </div>
        <div class="d-flex gap-2">
            <a href="<%= ctx %>/attendance" class="btn btn-shine btn-shine-teal">
                <i class="bi bi-pencil-fill"></i> <%= I18nUtil.getMessage(currentLocale, "btn.take_attendance") %>
            </a>
        </div>
    </div>
</div>

<div class="card-modern mb-4">
    <div class="card-header-clean">
        <h5 class="card-title-clean"><i class="bi bi-bar-chart-fill text-teal"></i> Daily Section Attendance Roster</h5>
        <span class="badge bg-light text-muted border">Select Section to Record or Review</span>
    </div>
    <div class="table-responsive">
        <table class="table table-clean">
            <thead>
                <tr>
                    <th>Class Section</th>
                    <th>Grade Level</th>
                    <th>Enrolled</th>
                    <th class="text-end">Record / View Attendance</th>
                </tr>
            </thead>
            <tbody>
                <% if (sections != null && !sections.isEmpty()) {
                    for (ClassSection cs : sections) { %>
                        <tr>
                            <td class="fw-bold text-navy"><%= cs.getFullClassName() %></td>
                            <td>Grade <%= cs.getGradeNumber() %></td>
                            <td><span class="badge bg-light text-dark border"><%= cs.getStudentCount() %> Students</span></td>
                            <td class="text-end">
                                <a href="<%= ctx %>/attendance?classId=<%= cs.getId() %>" class="btn btn-sm btn-shine btn-shine-teal">
                                    <i class="bi bi-check2-circle"></i> <%= I18nUtil.getMessage(currentLocale, "btn.take_attendance") %>
                                </a>
                            </td>
                        </tr>
                    <% }
                } %>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
