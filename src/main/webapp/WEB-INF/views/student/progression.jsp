<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*" %>
<%@ page import="util.I18nUtil" %>
<%@ page import="java.util.List" %>
<%
    String currentLocale = (String) session.getAttribute("currentLocale");
    if (currentLocale == null) currentLocale = "en";
    String ctx = request.getContextPath();
    String csrfToken = (String) session.getAttribute("csrfToken");

    List<AcademicYear> allYears = (List<AcademicYear>) request.getAttribute("academicYears");
    List<GradeLevel> gradeLevels = (List<GradeLevel>) request.getAttribute("gradeLevels");
    List<ClassSection> sections = (List<ClassSection>) request.getAttribute("classSections");
    ClassSection selectedSection = (ClassSection) request.getAttribute("selectedSection");
    List<Student> enrolledStudents = (List<Student>) request.getAttribute("enrolledStudents");
    AcademicYear currentYear = (AcademicYear) request.getAttribute("currentYear");
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="card-modern mb-4 bg-white" style="border-left: 6px solid var(--warm-gold);">
    <div class="d-flex flex-wrap justify-content-between align-items-center gap-3">
        <div>
            <h2 class="fw-bold mb-1" style="color: var(--primary-navy);">
                <i class="bi bi-arrow-up-right-circle-fill text-gold"></i> Student Grade Progression Workflow
            </h2>
            <p class="text-muted mb-0">
                Advance students from Grade 1 through Grade 8 across academic periods while preserving historical academic records.
            </p>
        </div>
        <a href="<%= ctx %>/students" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left"></i> Return to Directory
        </a>
    </div>
</div>

<% if ("promoted".equals(request.getParameter("msg"))) { %>
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="bi bi-check-circle-fill me-2"></i> Student has been successfully promoted to the next grade. Previous academic records were preserved.
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
<% } %>

<!-- Class Section Filter -->
<div class="card-modern mb-4 p-3">
    <form action="<%= ctx %>/students/progress" method="get" class="row g-3 align-items-center">
        <div class="col-md-8">
            <label class="form-label fw-semibold small text-muted">Select Current Class Section to Promote:</label>
            <select name="classId" class="form-select" onchange="this.form.submit()">
                <option value="">-- Choose Class Section --</option>
                <% if (sections != null) {
                    for (ClassSection cs : sections) { %>
                        <option value="<%= cs.getId() %>" <%= selectedSection != null && selectedSection.getId() == cs.getId() ? "selected" : "" %>>
                            <%= cs.getFullClassName() %> (<%= cs.getStudentCount() %> Students Enrolled)
                        </option>
                    <% }
                } %>
            </select>
        </div>
        <div class="col-md-4 d-flex align-items-end">
            <button type="submit" class="btn btn-shine btn-shine-primary w-100">
                <i class="bi bi-funnel"></i> Load Enrolled Students
            </button>
        </div>
    </form>
</div>

<!-- Enrolled Students Promotion Roster -->
<% if (selectedSection != null) { %>
    <div class="card-modern">
        <div class="card-header-clean">
            <h5 class="card-title-clean">
                <i class="bi bi-people-fill text-primary"></i> Students Enrolled in <%= selectedSection.getFullClassName() %>
            </h5>
            <span class="badge bg-primary">Grade <%= selectedSection.getGradeNumber() %></span>
        </div>

        <div class="table-responsive">
            <table class="table table-clean">
                <thead>
                    <tr>
                        <th>ID Number</th>
                        <th>Student Name (English / አማርኛ)</th>
                        <th>Current Year</th>
                        <th>Target Academic Year & Next Grade</th>
                        <th class="text-end">Action</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (enrolledStudents != null && !enrolledStudents.isEmpty()) {
                        for (Student s : enrolledStudents) {
                            int nextGradeNum = Math.min(8, selectedSection.getGradeNumber() + 1);
                    %>
                            <tr>
                                <td><code><%= s.getStudentIdNumber() %></code></td>
                                <td>
                                    <div class="fw-bold text-navy"><%= s.getFullNameEn() %></div>
                                    <small class="text-muted"><%= s.getFullNameAm() != null ? s.getFullNameAm() : "" %></small>
                                </td>
                                <td><%= currentYear != null ? currentYear.getName() : "2026/2027" %></td>
                                <td>
                                    <form action="<%= ctx %>/students/progress" method="post" class="d-flex flex-wrap gap-2 align-items-center">
                                        <input type="hidden" name="csrfToken" value="<%= csrfToken != null ? csrfToken : "" %>">
                                        <input type="hidden" name="studentId" value="<%= s.getId() %>">
                                        <input type="hidden" name="currentYearId" value="<%= currentYear != null ? currentYear.getId() : 1 %>">

                                        <select name="nextYearId" class="form-select form-select-sm" style="max-width: 160px;">
                                            <% for (AcademicYear y : allYears) { %>
                                                <option value="<%= y.getId() %>" <%= y.isCurrent() ? "selected" : "" %>><%= y.getName() %></option>
                                            <% } %>
                                        </select>

                                        <select name="nextGradeId" class="form-select form-select-sm" style="max-width: 140px;">
                                            <% for (GradeLevel gl : gradeLevels) { %>
                                                <option value="<%= gl.getId() %>" <%= gl.getGradeNumber() == nextGradeNum ? "selected" : "" %>>
                                                    Grade <%= gl.getGradeNumber() %>
                                                </option>
                                            <% } %>
                                        </select>

                                        <select name="nextSectionId" class="form-select form-select-sm" style="max-width: 140px;">
                                            <% for (ClassSection cs : sections) { %>
                                                <option value="<%= cs.getId() %>"><%= cs.getFullClassName() %></option>
                                            <% } %>
                                        </select>

                                        <button type="submit" class="btn btn-sm btn-shine btn-shine-gold text-nowrap">
                                            <i class="bi bi-arrow-up-right"></i> Promote
                                        </button>
                                    </form>
                                </td>
                                <td></td>
                            </tr>
                        <% }
                    } else { %>
                        <tr>
                            <td colspan="5" class="text-center py-4 text-muted">
                                No active students currently enrolled in <%= selectedSection.getFullClassName() %>.
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
<% } %>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
