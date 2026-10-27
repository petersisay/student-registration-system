<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h2 class="fw-bold mb-1" style="color: var(--primary-navy);">
            <i class="bi bi-diagram-2-fill me-2" style="color: var(--primary-blue);"></i>${i18n.get('nav.classes')}
        </h2>
        <p class="text-muted mb-0">Ethiopian Elementary Grades 1–8 Sections & Rosters | ${activeYear.name}</p>
    </div>
</div>

<div class="row g-4">
    <!-- Class Sections Grid -->
    <div class="${section != null ? 'col-lg-5' : 'col-12'}">
        <div class="card shadow-sm border-0">
            <div class="card-header bg-white py-3 border-0">
                <h5 class="fw-bold mb-0" style="color: var(--primary-navy);">
                    <i class="bi bi-grid-3x3-gap-fill me-2" style="color: var(--primary-blue);"></i>Class Sections (${sections.size()})
                </h5>
            </div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>Grade / ክፍል</th>
                                <th>Section</th>
                                <th>Room</th>
                                <th>Capacity</th>
                                <th class="text-end">Roster</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="s" items="${sections}">
                                <tr class="${section != null && section.id == s.id ? 'table-primary' : ''}">
                                    <td>
                                        <div class="fw-bold">${s.gradeName}</div>
                                        <div class="small text-muted">${s.gradeNameAmharic}</div>
                                    </td>
                                    <td>
                                        <span class="badge bg-primary-subtle text-primary border border-primary fs-6 px-2">Section ${s.sectionName}</span>
                                    </td>
                                    <td><i class="bi bi-door-open me-1"></i>${s.roomNumber}</td>
                                    <td><span class="badge bg-light text-dark border">${s.capacity} seats</span></td>
                                    <td class="text-end">
                                        <a href="${pageContext.request.contextPath}/admin/classes/roster?sectionId=${s.id}" class="btn btn-sm btn-outline-primary">
                                            <i class="bi bi-people-fill me-1"></i>View Roster
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Class Roster & Assigned Teachers View -->
    <c:if test="${section != null}">
        <div class="col-lg-7">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-white py-3 border-0 d-flex justify-content-between align-items-center">
                    <div>
                        <h5 class="fw-bold mb-0" style="color: var(--primary-navy);">
                            <i class="bi bi-people-fill me-2" style="color: var(--primary-blue);"></i>${section.gradeName} - Section ${section.sectionName} Roster
                        </h5>
                        <small class="text-muted">Room ${section.roomNumber} | Total Enrolled: ${roster.size()} / ${section.capacity}</small>
                    </div>
                    <a href="${pageContext.request.contextPath}/admin/classes" class="btn btn-sm btn-light">
                        <i class="bi bi-x-lg"></i>
                    </a>
                </div>
                <div class="card-body">
                    <!-- Faculty assigned to this class -->
                    <div class="mb-4">
                        <h6 class="fw-bold mb-2" style="color: var(--primary-navy);">Assigned Faculty for this Section</h6>
                        <c:choose>
                            <c:when test="${not empty assignments}">
                                <div class="row g-2">
                                    <c:forEach var="a" items="${assignments}">
                                        <div class="col-md-6">
                                            <div class="p-2 border rounded-3 bg-light">
                                                <div class="fw-bold text-dark small">${a.teacherName}</div>
                                                <div class="text-muted small">
                                                    <i class="bi bi-book me-1"></i>${a.subjectName} (${a.subjectNameAmharic})
                                                    <c:if test="${a.homeroom}">
                                                        <span class="badge bg-warning text-dark ms-1">Homeroom</span>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="alert alert-warning py-2 mb-0 small">
                                    <i class="bi bi-exclamation-triangle me-1"></i>No teachers assigned yet. <a href="${pageContext.request.contextPath}/admin/teachers" class="alert-link">Assign now</a>.
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Enrolled Students Table -->
                    <h6 class="fw-bold mb-2" style="color: var(--primary-navy);">Enrolled Students (${roster.size()})</h6>
                    <c:choose>
                        <c:when test="${not empty roster}">
                            <div class="table-responsive">
                                <table class="table table-sm table-hover align-middle mb-0">
                                    <thead class="table-light">
                                        <tr>
                                            <th>#</th>
                                            <th>Student ID</th>
                                            <th>Full Name</th>
                                            <th>Gender</th>
                                            <th>Status</th>
                                            <th class="text-end">Profile</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="st" items="${roster}" varStatus="status">
                                            <tr>
                                                <td>${status.index + 1}</td>
                                                <td><span class="badge bg-light text-dark border font-monospace">${st.studentCode}</span></td>
                                                <td>
                                                    <div class="fw-bold">${st.fullName}</div>
                                                    <c:if test="${not empty st.nameAmharic}">
                                                        <div class="text-muted small">${st.nameAmharic}</div>
                                                    </c:if>
                                                </td>
                                                <td><span class="badge bg-light text-dark">${st.gender}</span></td>
                                                <td><span class="badge bg-success">${st.enrollmentStatus}</span></td>
                                                <td class="text-end">
                                                    <a href="${pageContext.request.contextPath}/students/profile?id=${st.id}" class="btn btn-sm btn-outline-secondary">
                                                        <i class="bi bi-eye"></i>
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center py-4 text-muted">
                                <i class="bi bi-inbox fs-2 d-block mb-1"></i>
                                No students enrolled in this section for ${activeYear.name}.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </c:if>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
