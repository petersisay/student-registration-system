<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h2 class="fw-bold mb-1" style="color: var(--primary-navy);">
            <i class="bi bi-pencil-fill me-2" style="color: var(--primary-blue);"></i>${assessment.title}
        </h2>
        <p class="text-muted mb-0">
            ${assessment.className} | Subject: <strong>${assessment.subjectName}</strong> | Max: <strong>${assessment.maxScore}</strong> pts | Weight: <strong>${assessment.weightPercent}%</strong>
        </p>
    </div>
    <a href="${pageContext.request.contextPath}/assessments" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left me-1"></i>Back to Assessments
    </a>
</div>

<c:if test="${param.success eq 'saved'}">
    <div class="alert alert-success alert-dismissible fade show">
        <i class="bi bi-check-circle-fill me-2"></i>Scores saved successfully / ውጤቶች በተሳካ ሁኔታ ተመዝግበዋል።
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>
<c:if test="${param.error eq 'range'}">
    <div class="alert alert-danger alert-dismissible fade show">
        <i class="bi bi-exclamation-triangle-fill me-2"></i>Error: Score must be between 0 and maximum possible marks (${assessment.maxScore}).
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<div class="card shadow-sm border-0">
    <div class="card-header bg-white py-3 border-0 d-flex justify-content-between align-items-center">
        <h5 class="fw-bold mb-0" style="color: var(--primary-navy);">
            <i class="bi bi-people-fill me-2" style="color: var(--primary-blue);"></i>Student Score Entry (${roster.size()} enrolled)
        </h5>
        <span class="badge bg-primary fs-6">Max Mark: ${assessment.maxScore}</span>
    </div>
    <div class="card-body p-0">
        <form method="POST" action="${pageContext.request.contextPath}/assessments">
            <input type="hidden" name="action" value="saveScores" />
            <input type="hidden" name="assessmentId" value="${assessment.id}" />

            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th style="width: 50px;">#</th>
                            <th>Student ID</th>
                            <th>Student Name / ስም</th>
                            <th style="width: 180px;">Score / ውጤት (0 - ${assessment.maxScore}) *</th>
                            <th>Comments / አስተያየት</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="st" items="${roster}" varStatus="status">
                            <c:set var="savedScore" value="" />
                            <c:set var="savedComment" value="" />
                            <c:forEach var="sc" items="${scores}">
                                <c:if test="${sc.studentId == st.id}">
                                    <c:set var="savedScore" value="${sc.score}" />
                                    <c:set var="savedComment" value="${sc.comments}" />
                                </c:if>
                            </c:forEach>

                            <tr>
                                <td>${status.index + 1}</td>
                                <td><span class="badge bg-light text-dark border font-monospace">${st.studentCode}</span></td>
                                <td>
                                    <div class="fw-bold">${st.fullName}</div>
                                    <c:if test="${not empty st.nameAmharic}">
                                        <div class="text-muted small">${st.nameAmharic}</div>
                                    </c:if>
                                </td>
                                <td>
                                    <div class="input-group input-group-sm">
                                        <input type="number" step="0.5" min="0" max="${assessment.maxScore}" name="score_${st.id}" value="${savedScore}" class="form-control fw-bold" placeholder="0 - ${assessment.maxScore}" />
                                        <span class="input-group-text">/ ${assessment.maxScore}</span>
                                    </div>
                                </td>
                                <td>
                                    <input type="text" name="comment_${st.id}" value="${savedComment}" class="form-control form-control-sm" placeholder="Optional remark" />
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <div class="p-3 bg-light border-top d-flex justify-content-end">
                <button type="submit" class="btn btn-primary px-4">
                    <i class="bi bi-save me-1"></i>Save All Scores / ውጤት መዝግብ
                </button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
