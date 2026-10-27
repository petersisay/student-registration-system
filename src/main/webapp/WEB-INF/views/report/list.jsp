<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h2 class="fw-bold mb-1" style="color: var(--primary-navy);">
            <i class="bi bi-file-earmark-medical-fill me-2" style="color: var(--primary-blue);"></i>${i18n.get('nav.report_cards')}
        </h2>
        <p class="text-muted mb-0">Official Ethiopian Elementary School Report Cards | የተማሪዎች ካርድ (${currentTerm.name})</p>
    </div>
</div>

<!-- Class Selector -->
<div class="card shadow-sm border-0 mb-4">
    <div class="card-body">
        <form method="GET" action="${pageContext.request.contextPath}/reports" class="row g-3 align-items-end">
            <div class="col-md-9">
                <label class="form-label small fw-bold text-muted">Select Class Section / ክፍል ይምረጡ *</label>
                <select name="sectionId" class="form-select" onchange="this.form.submit()" required>
                    <option value="">-- Choose Section --</option>
                    <c:forEach var="s" items="${sections}">
                        <option value="${s.id}" ${selectedSection != null && selectedSection.id == s.id ? 'selected' : ''}>
                            ${s.gradeName} - Section ${s.sectionName} (Room ${s.roomNumber})
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-3">
                <button type="submit" class="btn btn-outline-primary w-100">
                    <i class="bi bi-search me-1"></i>Load Class Cards
                </button>
            </div>
        </form>
    </div>
</div>

<!-- Report Cards Table -->
<c:if test="${selectedSection != null}">
    <div class="card shadow-sm border-0">
        <div class="card-header bg-white py-3 border-0 d-flex justify-content-between align-items-center">
            <h5 class="fw-bold mb-0" style="color: var(--primary-navy);">
                <i class="bi bi-people-fill me-2" style="color: var(--primary-blue);"></i>${selectedSection.gradeName} - Section ${selectedSection.sectionName} Cards
            </h5>
            <small class="text-muted">${currentTerm.name} | ${activeYear.name}</small>
        </div>
        <div class="card-body p-0">
            <c:choose>
                <c:when test="${not empty reportCards}">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>Rank</th>
                                    <th>Student ID</th>
                                    <th>Student Name / ስም</th>
                                    <th>Total Score</th>
                                    <th>Average Score</th>
                                    <th>Status</th>
                                    <th class="text-end">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="rc" items="${reportCards}" varStatus="status">
                                    <tr>
                                        <td><span class="badge bg-secondary-subtle text-secondary border font-monospace fs-6">#${status.index + 1}</span></td>
                                        <td><span class="badge bg-light text-dark border font-monospace">${rc.studentIdNumber}</span></td>
                                        <td>
                                            <div class="fw-bold">${rc.studentName}</div>
                                        </td>
                                        <td><span class="fw-bold">${rc.totalScore}</span></td>
                                        <td>
                                            <span class="badge ${rc.averageScore >= 75 ? 'bg-success' : (rc.averageScore >= 50 ? 'bg-primary' : 'bg-danger')} fs-6">
                                                ${rc.averageScore}%
                                            </span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${rc.status eq 'PUBLISHED'}">
                                                    <span class="badge bg-success"><i class="bi bi-check-circle me-1"></i>Published</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-warning text-dark"><i class="bi bi-hourglass-split me-1"></i>Draft</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-end">
                                            <a href="${pageContext.request.contextPath}/reports/view?studentId=${rc.studentId}" class="btn btn-sm btn-outline-primary">
                                                <i class="bi bi-eye-fill me-1"></i>View Official Card
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="text-center py-5 text-muted">
                        <i class="bi bi-file-earmark-x fs-1 d-block mb-2"></i>
                        No report cards calculated yet for this section.
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
