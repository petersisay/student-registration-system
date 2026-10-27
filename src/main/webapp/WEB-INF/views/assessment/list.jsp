<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h2 class="fw-bold mb-1" style="color: var(--primary-navy);">
            <i class="bi bi-pencil-square me-2" style="color: var(--primary-blue);"></i>${i18n.get('nav.grades')}
        </h2>
        <p class="text-muted mb-0">Assessments & Continuous Evaluation | ምዘናዎች እና ውጤት መዝገብ (${currentTerm.name})</p>
    </div>
    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#newAssessmentModal">
        <i class="bi bi-plus-circle-fill me-1"></i> New Assessment / አዲስ ምዘና
    </button>
</div>

<!-- Filter Form -->
<div class="card shadow-sm border-0 mb-4">
    <div class="card-body">
        <form method="GET" action="${pageContext.request.contextPath}/assessments" class="row g-3 align-items-end">
            <div class="col-md-5">
                <label class="form-label small fw-bold text-muted">Class Section / ክፍል *</label>
                <select name="sectionId" class="form-select" required>
                    <c:choose>
                        <c:when test="${not empty assignments}">
                            <c:forEach var="a" items="${assignments}">
                                <option value="${a.classSectionId}" ${selectedSectionId == a.classSectionId ? 'selected' : ''}>
                                    ${a.gradeName} - Section ${a.sectionName}
                                </option>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="s" items="${sections}">
                                <option value="${s.id}" ${selectedSectionId == s.id ? 'selected' : ''}>
                                    ${s.gradeName} - Section ${s.sectionName}
                                </option>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </select>
            </div>
            <div class="col-md-5">
                <label class="form-label small fw-bold text-muted">Subject / የትምህርት ዓይነት *</label>
                <select name="subjectId" class="form-select" required>
                    <c:choose>
                        <c:when test="${not empty assignments}">
                            <c:forEach var="a" items="${assignments}">
                                <option value="${a.subjectId}" ${selectedSubjectId == a.subjectId ? 'selected' : ''}>
                                    ${a.subjectName}
                                </option>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="sub" items="${subjects}">
                                <option value="${sub.id}" ${selectedSubjectId == sub.id ? 'selected' : ''}>
                                    ${sub.name} (${sub.nameAmharic})
                                </option>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </select>
            </div>
            <div class="col-md-2">
                <button type="submit" class="btn btn-outline-primary w-100">
                    <i class="bi bi-filter me-1"></i>Filter / አሳይ
                </button>
            </div>
        </form>
    </div>
</div>

<!-- Assessments Table -->
<div class="card shadow-sm border-0">
    <div class="card-header bg-white py-3 border-0">
        <h5 class="fw-bold mb-0" style="color: var(--primary-navy);">
            <i class="bi bi-journal-text me-2" style="color: var(--primary-blue);"></i>Assessments List (${assessments.size()})
        </h5>
    </div>
    <div class="card-body p-0">
        <c:choose>
            <c:when test="${not empty assessments}">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>Title / ርዕስ</th>
                                <th>Type</th>
                                <th>Date</th>
                                <th>Max Mark</th>
                                <th>Weight</th>
                                <th>Scores Entered</th>
                                <th class="text-end">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="a" items="${assessments}">
                                <tr>
                                    <td>
                                        <div class="fw-bold">${a.title}</div>
                                        <div class="small text-muted">${a.className} | ${a.subjectName}</div>
                                    </td>
                                    <td><span class="badge bg-primary-subtle text-primary border border-primary">${a.assessmentType}</span></td>
                                    <td>${a.assessmentDate}</td>
                                    <td><span class="badge bg-light text-dark border font-monospace">${a.maxScore} pts</span></td>
                                    <td><span class="badge bg-info-subtle text-info border border-info">${a.weightPercent}%</span></td>
                                    <td>
                                        <span class="badge bg-success">${a.scoresCount} students</span>
                                    </td>
                                    <td class="text-end">
                                        <a href="${pageContext.request.contextPath}/assessments/scores?id=${a.id}" class="btn btn-sm btn-primary">
                                            <i class="bi bi-pencil-fill me-1"></i>Enter Marks / ውጤት አስገባ
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
                    <i class="bi bi-journal-x fs-1 d-block mb-2"></i>
                    No assessments found for the selected class and subject. Click "New Assessment" to create one.
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Modal: New Assessment -->
<div class="modal fade" id="newAssessmentModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/assessments" method="POST">
                <input type="hidden" name="action" value="create" />
                <input type="hidden" name="termId" value="${currentTerm.id}" />
                <div class="modal-header">
                    <h5 class="modal-title fw-bold" style="color: var(--primary-navy);">Create Assessment / አዲስ ምዘና</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="row g-3">
                        <div class="col-12">
                            <label class="form-label small fw-bold">Assessment Title *</label>
                            <input type="text" name="title" class="form-control" placeholder="e.g. Midterm Examination / አጋማሽ ፈተና" required />
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">Class Section *</label>
                            <select name="classSectionId" class="form-select" required>
                                <c:choose>
                                    <c:when test="${not empty assignments}">
                                        <c:forEach var="a" items="${assignments}">
                                            <option value="${a.classSectionId}">${a.gradeName} - Section ${a.sectionName}</option>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="s" items="${sections}">
                                            <option value="${s.id}">${s.gradeName} - Section ${s.sectionName}</option>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">Subject *</label>
                            <select name="subjectId" class="form-select" required>
                                <c:choose>
                                    <c:when test="${not empty assignments}">
                                        <c:forEach var="a" items="${assignments}">
                                            <option value="${a.subjectId}">${a.subjectName}</option>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="sub" items="${subjects}">
                                            <option value="${sub.id}">${sub.name} (${sub.nameAmharic})</option>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">Type / ዓይነት *</label>
                            <select name="assessmentType" class="form-select" required>
                                <option value="QUIZ">Quiz / ፈተና</option>
                                <option value="ASSIGNMENT">Assignment / የቤት ሥራ</option>
                                <option value="MIDTERM">Midterm Exam / አጋማሽ ፈተና</option>
                                <option value="FINAL">Final Exam / የማጠቃለያ ፈተና</option>
                                <option value="PROJECT">Project / ፕሮጀክት</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">Assessment Date *</label>
                            <input type="date" name="assessmentDate" class="form-control" value="2026-10-27" required />
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">Maximum Marks *</label>
                            <input type="number" name="maxScore" class="form-control" value="20" min="1" max="100" required />
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">Weight Percentage (%) *</label>
                            <input type="number" name="weightPercent" class="form-control" value="20" min="1" max="100" required />
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary"><i class="bi bi-save me-1"></i>Create & Enter Scores</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
