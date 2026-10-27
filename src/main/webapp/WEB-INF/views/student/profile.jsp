<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*" %>
<%@ page import="util.I18nUtil" %>
<%@ page import="java.util.List" %>
<%
    String currentLocale = (String) session.getAttribute("currentLocale");
    if (currentLocale == null) currentLocale = "en";
    String ctx = request.getContextPath();

    Student student = (Student) request.getAttribute("student");
    List<StudentEnrollment> history = (List<StudentEnrollment>) request.getAttribute("enrollmentHistory");
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="row g-4">
    <!-- Left Column: Student Profile Details -->
    <div class="col-lg-4">
        <div class="card-modern text-center">
            <div style="width: 80px; height: 80px; background: rgba(37, 99, 235, 0.12); color: var(--primary-blue); border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 2.5rem; margin: 0 auto 1rem;">
                <i class="bi bi-person"></i>
            </div>
            <h4 class="fw-bold text-navy mb-1"><%= student != null ? student.getFullNameEn() : "" %></h4>
            <% if (student != null && student.getFullNameAm() != null && !student.getFullNameAm().isEmpty()) { %>
                <p class="text-secondary mb-2"><%= student.getFullNameAm() %></p>
            <% } %>
            <span class="badge bg-primary px-3 py-2 fs-6 mb-3"><%= student != null ? student.getStudentIdNumber() : "" %></span>

            <div class="list-group list-group-flush text-start small border-top pt-3">
                <div class="list-group-item px-0 d-flex justify-content-between">
                    <span class="text-muted"><%= I18nUtil.getMessage(currentLocale, "student.current_grade") %>:</span>
                    <span class="fw-bold"><%= student != null && student.getCurrentGradeName() != null ? student.getCurrentGradeName() : "Grade " + (student != null ? student.getYear() : "1") %></span>
                </div>
                <div class="list-group-item px-0 d-flex justify-content-between">
                    <span class="text-muted"><%= I18nUtil.getMessage(currentLocale, "student.current_section") %>:</span>
                    <span class="fw-bold"><%= student != null && student.getCurrentSectionName() != null ? student.getCurrentSectionName() : "A" %></span>
                </div>
                <div class="list-group-item px-0 d-flex justify-content-between">
                    <span class="text-muted"><%= I18nUtil.getMessage(currentLocale, "student.gender") %>:</span>
                    <span><%= student != null ? student.getGender() : "—" %></span>
                </div>
                <div class="list-group-item px-0 d-flex justify-content-between">
                    <span class="text-muted"><%= I18nUtil.getMessage(currentLocale, "student.date_of_birth") %>:</span>
                    <span><%= student != null && student.getDateOfBirth() != null ? student.getDateOfBirth() : "—" %></span>
                </div>
                <div class="list-group-item px-0 d-flex justify-content-between">
                    <span class="text-muted"><%= I18nUtil.getMessage(currentLocale, "student.emergency_contact") %>:</span>
                    <span><%= student != null ? student.getEmergencyContactName() : "—" %></span>
                </div>
                <div class="list-group-item px-0 d-flex justify-content-between">
                    <span class="text-muted">Guardian Phone:</span>
                    <span><%= student != null ? student.getEmergencyContactPhone() : "—" %></span>
                </div>
            </div>

            <div class="mt-4 pt-2">
                <a href="<%= ctx %>/students?action=edit&id=<%= student != null ? student.getId() : 0 %>" class="btn btn-shine btn-shine-outline w-100">
                    <i class="bi bi-pencil"></i> <%= I18nUtil.getMessage(currentLocale, "btn.edit") %> Student Profile
                </a>
            </div>
        </div>
    </div>

    <!-- Right Column: Academic Progression History & Multi-Year Timeline -->
    <div class="col-lg-8">
        <div class="card-modern">
            <div class="card-header-clean">
                <h5 class="card-title-clean">
                    <i class="bi bi-mortarboard-fill text-gold"></i> Academic Progression & Enrollment History
                </h5>
                <span class="badge bg-light text-dark border">Preserved Records</span>
            </div>
            <p class="text-muted small">
                Every academic year of attendance is recorded permanently. When the student advances to a new grade, historical attendance, assessments, and report cards remain intact.
            </p>

            <div class="table-responsive">
                <table class="table table-clean">
                    <thead>
                        <tr>
                            <th>Academic Period</th>
                            <th>Grade Level</th>
                            <th>Section</th>
                            <th>Enrollment Date</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (history != null && !history.isEmpty()) {
                            for (StudentEnrollment se : history) { %>
                                <tr>
                                    <td class="fw-bold text-navy"><%= se.getAcademicYearName() %></td>
                                    <td>Grade <%= se.getGradeNumber() %></td>
                                    <td>Section <%= se.getSectionName() %></td>
                                    <td class="text-muted"><%= se.getEnrollmentDate() %></td>
                                    <td>
                                        <% if ("PROMOTED".equalsIgnoreCase(se.getStatus())) { %>
                                            <span class="badge-status badge-promoted"><i class="bi bi-arrow-up-circle"></i> Promoted</span>
                                        <% } else { %>
                                            <span class="badge-status badge-active"><i class="bi bi-check-circle"></i> Active</span>
                                        <% } %>
                                    </td>
                                </tr>
                            <% }
                        } else { %>
                            <tr><td colspan="5" class="text-center text-muted py-4">No enrollment records logged yet.</td></tr>
                        <% } %>
                    </tbody>
                </table>
            </div>

            <div class="mt-4 pt-3 border-top d-flex justify-content-between">
                <a href="<%= ctx %>/students" class="btn btn-outline-secondary">
                    <i class="bi bi-arrow-left"></i> Back to Student List
                </a>
                <a href="<%= ctx %>/students/progress" class="btn btn-shine btn-shine-gold">
                    <i class="bi bi-arrow-up-right-circle"></i> Promote to Next Grade
                </a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
