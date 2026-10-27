<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*" %>
<%@ page import="util.I18nUtil" %>
<%@ page import="java.util.List" %>
<%
    String currentLocale = (String) session.getAttribute("currentLocale");
    if (currentLocale == null) currentLocale = "en";
    String ctx = request.getContextPath();
    String csrfToken = (String) session.getAttribute("csrfToken");

    Student student = (Student) request.getAttribute("student");
    List<GradeLevel> gradeLevels = (List<GradeLevel>) request.getAttribute("gradeLevels");
    List<ClassSection> sections = (List<ClassSection>) request.getAttribute("classSections");
    AcademicYear currentYear = (AcademicYear) request.getAttribute("currentYear");
    boolean isEdit = (student != null && student.getId() > 0);
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="row justify-content-center">
    <div class="col-lg-10">
        <div class="card-modern">
            <div class="card-header-clean">
                <h4 class="card-title-clean">
                    <i class="bi bi-person-badge-fill text-primary"></i>
                    <%= isEdit ? "Edit Student Details" : I18nUtil.getMessage(currentLocale, "nav.student_register") %>
                </h4>
                <a href="<%= ctx %>/students" class="btn btn-sm btn-outline-secondary">
                    <i class="bi bi-arrow-left"></i> <%= I18nUtil.getMessage(currentLocale, "btn.back") %>
                </a>
            </div>

            <form action="<%= ctx %>/students" method="post">
                <input type="hidden" name="csrfToken" value="<%= csrfToken != null ? csrfToken : "" %>">
                <% if (isEdit) { %>
                    <input type="hidden" name="id" value="<%= student.getId() %>">
                <% } %>
                <input type="hidden" name="academicYearId" value="<%= currentYear != null ? currentYear.getId() : 1 %>">

                <div class="row g-3 mb-4">
                    <div class="col-12"><h6 class="fw-bold text-primary border-bottom pb-2">1. Identity & Names (ማንነት እና ስም)</h6></div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold"><%= I18nUtil.getMessage(currentLocale, "student.full_name_en") %> <span class="text-danger">*</span></label>
                        <input type="text" name="fullNameEn" class="form-control" required placeholder="e.g. Dawit Yohannes" value="<%= isEdit ? student.getFullNameEn() : "" %>">
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold"><%= I18nUtil.getMessage(currentLocale, "student.full_name_am") %></label>
                        <input type="text" name="fullNameAm" class="form-control" placeholder="ምሳሌ፦ ዳዊት ዮሐንስ" value="<%= isEdit && student.getFullNameAm() != null ? student.getFullNameAm() : "" %>">
                    </div>

                    <div class="col-md-4">
                        <label class="form-label fw-semibold"><%= I18nUtil.getMessage(currentLocale, "student.id_number") %></label>
                        <input type="text" name="studentIdNumber" class="form-control" placeholder="e.g. STD-2026-045" value="<%= isEdit && student.getStudentIdNumber() != null ? student.getStudentIdNumber() : "" %>">
                        <small class="text-muted">Leave blank for auto-generated ID.</small>
                    </div>

                    <div class="col-md-4">
                        <label class="form-label fw-semibold"><%= I18nUtil.getMessage(currentLocale, "student.date_of_birth") %></label>
                        <input type="date" name="dateOfBirth" class="form-control" value="<%= isEdit && student.getDateOfBirth() != null ? student.getDateOfBirth().toString() : "" %>">
                    </div>

                    <div class="col-md-4">
                        <label class="form-label fw-semibold"><%= I18nUtil.getMessage(currentLocale, "student.gender") %></label>
                        <select name="gender" class="form-select">
                            <option value="MALE" <%= isEdit && "MALE".equalsIgnoreCase(student.getGender()) ? "selected" : "" %>><%= I18nUtil.getMessage(currentLocale, "student.gender_male") %></option>
                            <option value="FEMALE" <%= isEdit && "FEMALE".equalsIgnoreCase(student.getGender()) ? "selected" : "" %>><%= I18nUtil.getMessage(currentLocale, "student.gender_female") %></option>
                        </select>
                    </div>
                </div>

                <div class="row g-3 mb-4">
                    <div class="col-12"><h6 class="fw-bold text-teal border-bottom pb-2">2. Elementary Academic Placement (የክፍል ምደባ)</h6></div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold"><%= I18nUtil.getMessage(currentLocale, "student.current_grade") %> (Grades 1 - 8) <span class="text-danger">*</span></label>
                        <select name="gradeLevelId" class="form-select" required>
                            <% if (gradeLevels != null) {
                                for (GradeLevel gl : gradeLevels) { %>
                                    <option value="<%= gl.getId() %>" <%= isEdit && student.getYear() == gl.getGradeNumber() ? "selected" : "" %>>
                                        Grade <%= gl.getGradeNumber() %> - <%= gl.getNameAm() %>
                                    </option>
                                <% }
                            } %>
                        </select>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold"><%= I18nUtil.getMessage(currentLocale, "student.current_section") %> <span class="text-danger">*</span></label>
                        <select name="classSectionId" class="form-select" required>
                            <% if (sections != null) {
                                for (ClassSection cs : sections) { %>
                                    <option value="<%= cs.getId() %>">
                                        <%= cs.getFullClassName() %> (<%= cs.getRoomNumber() != null ? cs.getRoomNumber() : "Main Building" %>)
                                    </option>
                                <% }
                            } %>
                        </select>
                    </div>
                </div>

                <div class="row g-3 mb-4">
                    <div class="col-12"><h6 class="fw-bold text-secondary border-bottom pb-2">3. Guardian & Contact (የአሳዳጊ መረጃ)</h6></div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold"><%= I18nUtil.getMessage(currentLocale, "student.emergency_contact") %></label>
                        <input type="text" name="emergencyContactName" class="form-control" placeholder="Guardian's Full Name" value="<%= isEdit && student.getEmergencyContactName() != null ? student.getEmergencyContactName() : "" %>">
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold"><%= I18nUtil.getMessage(currentLocale, "student.emergency_phone") %></label>
                        <input type="text" name="emergencyContactPhone" class="form-control" placeholder="+251 91 123 4567" value="<%= isEdit && student.getEmergencyContactPhone() != null ? student.getEmergencyContactPhone() : "" %>">
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Email Address (Optional)</label>
                        <input type="email" name="email" class="form-control" placeholder="student@example.com" value="<%= isEdit && student.getEmail() != null ? student.getEmail() : "" %>">
                    </div>
                </div>

                <div class="d-flex justify-content-end gap-2 pt-3 border-top">
                    <a href="<%= ctx %>/students" class="btn btn-outline-secondary">
                        <%= I18nUtil.getMessage(currentLocale, "btn.cancel") %>
                    </a>
                    <button type="submit" class="btn btn-shine btn-shine-primary px-4">
                        <i class="bi bi-check-lg"></i> <%= I18nUtil.getMessage(currentLocale, "btn.save") %>
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
