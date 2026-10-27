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
    List<ClassSection> sections = (List<ClassSection>) request.getAttribute("sections");
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="card-modern mb-4 bg-white" style="border-left: 6px solid var(--primary-blue);">
    <div class="d-flex flex-wrap justify-content-between align-items-center gap-3">
        <div>
            <h2 class="fw-bold mb-1" style="color: var(--primary-navy);">
                <i class="bi bi-file-earmark-person-fill text-primary"></i> Academic Registrar Operations
            </h2>
            <p class="text-muted mb-0">
                Student Admissions &bull; Class Section Placements &bull; Grade Progression History
            </p>
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
</div>

<div class="card-modern">
    <div class="card-header-clean">
        <h5 class="card-title-clean"><i class="bi bi-diagram-3-fill text-primary"></i> Class Section Enrollment Allocations</h5>
        <a href="<%= ctx %>/students" class="btn btn-sm btn-shine btn-shine-outline">Search Student Directory</a>
    </div>
    <div class="table-responsive">
        <table class="table table-clean">
            <thead>
                <tr>
                    <th>Class Section</th>
                    <th>Grade Level</th>
                    <th>Capacity</th>
                    <th>Enrolled</th>
                    <th class="text-end">Actions</th>
                </tr>
            </thead>
            <tbody>
                <% if (sections != null && !sections.isEmpty()) {
                    for (ClassSection cs : sections) { %>
                        <tr>
                            <td class="fw-bold text-navy"><%= cs.getFullClassName() %></td>
                            <td>Grade <%= cs.getGradeNumber() %></td>
                            <td><%= cs.getMaxCapacity() %> Max</td>
                            <td><span class="badge bg-light text-dark border"><%= cs.getStudentCount() %> Students</span></td>
                            <td class="text-end">
                                <a href="<%= ctx %>/students?grade=<%= cs.getGradeNumber() %>" class="btn btn-sm btn-outline-primary">
                                    <i class="bi bi-people"></i> View Students
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
