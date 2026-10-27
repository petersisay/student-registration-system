<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*" %>
<%@ page import="util.I18nUtil" %>
<%@ page import="java.util.List" %>
<%
    String currentLocale = (String) session.getAttribute("currentLocale");
    if (currentLocale == null) currentLocale = "en";
    String ctx = request.getContextPath();

    List<Student> students = (List<Student>) request.getAttribute("students");
    List<GradeLevel> gradeLevels = (List<GradeLevel>) request.getAttribute("gradeLevels");
    int totalCount = (request.getAttribute("totalCount") != null) ? (int) request.getAttribute("totalCount") : 0;
    int currentPage = (request.getAttribute("currentPage") != null) ? (int) request.getAttribute("currentPage") : 1;
    int totalPages = (request.getAttribute("totalPages") != null) ? (int) request.getAttribute("totalPages") : 1;
    String search = (String) request.getAttribute("search");
    Integer selectedGrade = (Integer) request.getAttribute("selectedGrade");
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
    <div>
        <h2 class="fw-bold mb-1" style="color: var(--primary-navy);">
            <i class="bi bi-people-fill text-primary"></i> <%= I18nUtil.getMessage(currentLocale, "nav.student_directory") %>
        </h2>
        <p class="text-muted mb-0">Total Enrolled Students: <strong><%= totalCount %></strong> (Grades 1-8)</p>
    </div>
    <div class="d-flex gap-2">
        <a href="<%= ctx %>/students?action=new" class="btn btn-shine btn-shine-primary">
            <i class="bi bi-person-plus-fill"></i> <%= I18nUtil.getMessage(currentLocale, "nav.student_register") %>
        </a>
        <a href="<%= ctx %>/students/progress" class="btn btn-shine btn-shine-gold">
            <i class="bi bi-arrow-up-right-circle-fill"></i> <%= I18nUtil.getMessage(currentLocale, "nav.grade_progression") %>
        </a>
    </div>
</div>

<% if ("saved".equals(request.getParameter("msg"))) { %>
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="bi bi-check-circle-fill me-2"></i> Student registration/update successfully saved to the database.
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
<% } %>

<!-- Search & Filtering Bar -->
<div class="card-modern mb-4 p-3">
    <form action="<%= ctx %>/students" method="get" class="row g-2 align-items-center">
        <div class="col-md-6 col-lg-7">
            <div class="input-group">
                <span class="input-group-text bg-light text-muted border-end-0"><i class="bi bi-search"></i></span>
                <input type="text" name="search" class="form-control border-start-0" placeholder="Search by English name, Amharic name, or ID number..." value="<%= search != null ? search : "" %>">
            </div>
        </div>
        <div class="col-md-4 col-lg-3">
            <select name="grade" class="form-select">
                <option value="">All Elementary Grades (1-8)</option>
                <% if (gradeLevels != null) {
                    for (GradeLevel gl : gradeLevels) { %>
                        <option value="<%= gl.getGradeNumber() %>" <%= (selectedGrade != null && selectedGrade == gl.getGradeNumber()) ? "selected" : "" %>>
                            Grade <%= gl.getGradeNumber() %> (<%= gl.getNameAm() %>)
                        </option>
                    <% }
                } %>
            </select>
        </div>
        <div class="col-md-2 col-lg-2 d-flex gap-2">
            <button type="submit" class="btn btn-shine btn-shine-primary w-100">
                <i class="bi bi-funnel"></i> <%= I18nUtil.getMessage(currentLocale, "btn.filter") %>
            </button>
            <a href="<%= ctx %>/students" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-counterclockwise"></i>
            </a>
        </div>
    </form>
</div>

<!-- Student Directory Table -->
<div class="card-modern p-0 overflow-hidden">
    <div class="table-responsive">
        <table class="table table-clean mb-0">
            <thead>
                <tr>
                    <th>Student ID</th>
                    <th>Full Name (English & አማርኛ)</th>
                    <th>Grade & Section</th>
                    <th>Gender</th>
                    <th>Guardian Contact</th>
                    <th>Status</th>
                    <th class="text-end">Actions</th>
                </tr>
            </thead>
            <tbody>
                <% if (students != null && !students.isEmpty()) {
                    for (Student s : students) { %>
                        <tr>
                            <td><code><%= s.getStudentIdNumber() != null ? s.getStudentIdNumber() : "STD-" + s.getId() %></code></td>
                            <td>
                                <div class="fw-bold text-navy"><%= s.getFullNameEn() %></div>
                                <% if (s.getFullNameAm() != null && !s.getFullNameAm().isEmpty()) { %>
                                    <div class="text-secondary small"><%= s.getFullNameAm() %></div>
                                <% } %>
                            </td>
                            <td>
                                <span class="badge bg-light text-dark border">
                                    <%= s.getCurrentGradeName() != null ? s.getCurrentGradeName() : "Grade " + s.getYear() %>
                                    <%= s.getCurrentSectionName() != null ? " - " + s.getCurrentSectionName() : "" %>
                                </span>
                            </td>
                            <td><%= s.getGender() != null ? s.getGender() : "—" %></td>
                            <td>
                                <div><%= s.getEmergencyContactName() != null ? s.getEmergencyContactName() : "—" %></div>
                                <small class="text-muted"><%= s.getEmergencyContactPhone() != null ? s.getEmergencyContactPhone() : "" %></small>
                            </td>
                            <td><span class="badge-status badge-active"><%= s.getStatus() %></span></td>
                            <td class="text-end">
                                <a href="<%= ctx %>/students?action=view&id=<%= s.getId() %>" class="btn btn-sm btn-outline-primary" title="View Profile">
                                    <i class="bi bi-eye"></i>
                                </a>
                                <a href="<%= ctx %>/students?action=edit&id=<%= s.getId() %>" class="btn btn-sm btn-outline-secondary ms-1" title="Edit Student">
                                    <i class="bi bi-pencil"></i>
                                </a>
                            </td>
                        </tr>
                    <% }
                } else { %>
                    <tr>
                        <td colspan="7" class="text-center py-5 text-muted">
                            <i class="bi bi-people fs-1 d-block mb-2 text-secondary"></i>
                            <%= I18nUtil.getMessage(currentLocale, "student.no_students") %>
                        </td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>

    <!-- Pagination Controls -->
    <% if (totalPages > 1) { %>
        <div class="p-3 border-top d-flex justify-content-between align-items-center">
            <small class="text-muted">Page <%= currentPage %> of <%= totalPages %></small>
            <ul class="pagination pagination-sm mb-0">
                <% for (int p = 1; p <= totalPages; p++) { %>
                    <li class="page-item <%= p == currentPage ? "active" : "" %>">
                        <a class="page-link" href="<%= ctx %>/students?page=<%= p %><%= search != null ? "&search=" + search : "" %><%= selectedGrade != null ? "&grade=" + selectedGrade : "" %>"><%= p %></a>
                    </li>
                <% } %>
            </ul>
        </div>
    <% } %>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
