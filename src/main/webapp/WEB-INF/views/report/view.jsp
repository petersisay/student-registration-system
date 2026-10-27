<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<style>
@media print {
    .no-print, header, footer, .sidebar, .navbar, .btn, .alert {
        display: none !important;
    }
    body {
        background: #fff !important;
        font-size: 12pt;
    }
    .report-card-container {
        border: 2px solid #17324D !important;
        box-shadow: none !important;
        padding: 20px !important;
        margin: 0 !important;
    }
}
.report-card-container {
    background: #fff;
    border: 1px solid #E2E8F0;
    border-radius: 12px;
    padding: 30px;
}
.seal-box {
    border: 2px dashed #CBD5E1;
    border-radius: 8px;
    padding: 15px;
    text-align: center;
    min-height: 100px;
}
</style>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2 no-print">
    <div>
        <a href="${pageContext.request.contextPath}/reports" class="btn btn-outline-secondary btn-sm mb-2">
            <i class="bi bi-arrow-left me-1"></i>Back to Reports
        </a>
        <h2 class="fw-bold mb-0" style="color: var(--primary-navy);">
            Official Student Report Card / የተማሪ ውጤት ካርድ
        </h2>
    </div>
    <div class="d-flex gap-2">
        <button onclick="window.print()" class="btn btn-outline-dark">
            <i class="bi bi-printer-fill me-1"></i>Print Report Card / አትም
        </button>
        <c:if test="${currentUser.role eq 'ADMIN' || currentUser.role eq 'PRINCIPAL' || currentUser.role eq 'TEACHER'}">
            <form action="${pageContext.request.contextPath}/reports" method="POST" class="d-inline">
                <input type="hidden" name="action" value="generate" />
                <input type="hidden" name="studentId" value="${student.id}" />
                <input type="hidden" name="sectionId" value="${reportCard != null ? reportCard.enrollmentId : 1}" />
                <input type="hidden" name="termId" value="${currentTerm.id}" />
                <input type="hidden" name="academicYearId" value="${activeYear.id}" />
                <button type="submit" class="btn btn-primary">
                    <i class="bi bi-arrow-repeat me-1"></i>Recalculate Scores
                </button>
            </form>
            <c:if test="${reportCard != null && reportCard.status ne 'PUBLISHED'}">
                <form action="${pageContext.request.contextPath}/reports" method="POST" class="d-inline">
                    <input type="hidden" name="action" value="publish" />
                    <input type="hidden" name="reportCardId" value="${reportCard.id}" />
                    <input type="hidden" name="studentId" value="${student.id}" />
                    <button type="submit" class="btn btn-success">
                        <i class="bi bi-check-all me-1"></i>Publish / አጽድቅ
                    </button>
                </form>
            </c:if>
        </c:if>
    </div>
</div>

<c:if test="${param.success eq 'generated'}">
    <div class="alert alert-success alert-dismissible fade show no-print">
        <i class="bi bi-check-circle-fill me-2"></i>Report card recalculated from active assessments and attendance.
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>
<c:if test="${param.success eq 'published'}">
    <div class="alert alert-success alert-dismissible fade show no-print">
        <i class="bi bi-check-circle-fill me-2"></i>Report card published! It is now visible to the student and guardian.
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<div class="report-card-container shadow-sm mb-5">
    <!-- Header -->
    <div class="text-center border-bottom pb-4 mb-4">
        <div class="d-flex justify-content-center align-items-center gap-3 mb-2">
            <div style="width: 50px; height: 50px; border-radius: 50%; background: #17324D; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 24px;">
                <i class="bi bi-mortarboard-fill"></i>
            </div>
            <div>
                <h3 class="fw-bold mb-0" style="color: #17324D;">ADDIS TESFA ELEMENTARY SCHOOL</h3>
                <h5 class="fw-bold mb-0 text-muted" style="font-family: 'Noto Sans Ethiopic', sans-serif;">አዲስ ተስፋ የመጀመሪያ ደረጃ ትምህርት ቤት (1ኛ - 8ኛ ክፍል)</h5>
            </div>
        </div>
        <p class="text-muted small mb-0">Addis Ababa, Ethiopia | Official Student Academic Progress Report | ${activeYear.name}</p>
    </div>

    <!-- Student Details -->
    <div class="row g-3 mb-4 p-3 rounded-3" style="background-color: #F8FAFC; border: 1px solid #E2E8F0;">
        <div class="col-md-3">
            <small class="text-muted d-block">Student ID / መለያ ቁጥር</small>
            <strong class="font-monospace text-primary fs-6">${student.studentCode}</strong>
        </div>
        <div class="col-md-3">
            <small class="text-muted d-block">Student Name / የተማሪው ስም</small>
            <strong>${student.fullName}</strong>
            <c:if test="${not empty student.nameAmharic}">
                <div class="small text-muted">${student.nameAmharic}</div>
            </c:if>
        </div>
        <div class="col-md-3">
            <small class="text-muted d-block">Grade & Section / ክፍል</small>
            <strong>${student.currentGradeName} - ${student.currentSectionName}</strong>
        </div>
        <div class="col-md-3">
            <small class="text-muted d-block">Academic Period / የትምህርት ዘመን</small>
            <strong>${activeYear.name} (${currentTerm.name})</strong>
        </div>
    </div>

    <!-- Subject Performance Table -->
    <div class="table-responsive mb-4">
        <table class="table table-bordered align-middle mb-0">
            <thead class="table-light text-center">
                <tr>
                    <th style="width: 40px;">#</th>
                    <th class="text-start">Subject / የትምህርት ዓይነት</th>
                    <th>Code</th>
                    <th>Total Score (100%)</th>
                    <th>Grade / ደረጃ</th>
                    <th>Remarks / አስተያየት</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${reportCard != null && not empty reportCard.items}">
                        <c:forEach var="item" items="${reportCard.items}" varStatus="status">
                            <tr>
                                <td class="text-center">${status.index + 1}</td>
                                <td>
                                    <strong>${item.subjectNameEn}</strong>
                                    <c:if test="${not empty item.subjectNameAm}">
                                        <span class="text-muted ms-1">(${item.subjectNameAm})</span>
                                    </c:if>
                                </td>
                                <td class="text-center"><span class="badge bg-light text-dark border font-monospace">${item.subjectCode}</span></td>
                                <td class="text-center fw-bold fs-6">${item.finalScore}</td>
                                <td class="text-center">
                                    <span class="badge ${item.letterGrade eq 'A' ? 'bg-success' : (item.letterGrade eq 'B' ? 'bg-primary' : (item.letterGrade eq 'C' ? 'bg-warning text-dark' : 'bg-danger'))} fs-6 px-3">
                                        ${item.letterGrade}
                                    </span>
                                </td>
                                <td class="text-center small">${item.remarks}</td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="6" class="text-center py-4 text-muted">
                                No subject marks found. Click "Recalculate Scores" above to compute.
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>

    <!-- Summary Metrics -->
    <div class="row g-3 mb-4">
        <div class="col-md-6">
            <div class="p-3 border rounded-3 bg-light h-100">
                <h6 class="fw-bold mb-3" style="color: #17324D;">Academic Performance Summary / የውጤት ማጠቃለያ</h6>
                <div class="d-flex justify-content-between mb-2 pb-2 border-bottom">
                    <span>Total Score / ድምር ውጤት:</span>
                    <strong class="fs-5">${reportCard != null ? reportCard.totalScore : '0.00'}</strong>
                </div>
                <div class="d-flex justify-content-between mb-2 pb-2 border-bottom">
                    <span>Average Score / አማካይ ውጤት:</span>
                    <strong class="fs-5 text-primary">${reportCard != null ? reportCard.averageScore : '0.00'}%</strong>
                </div>
                <div class="d-flex justify-content-between">
                    <span>Class Rank / ደረጃ:</span>
                    <span class="badge bg-dark fs-6">Rank #${reportCard != null ? reportCard.rankInClass : '1'}</span>
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="p-3 border rounded-3 bg-light h-100">
                <h6 class="fw-bold mb-3" style="color: #17324D;">Attendance & Conduct / የተገኝነት እና ጠባይ ማጠቃለያ</h6>
                <div class="d-flex justify-content-between mb-2 pb-2 border-bottom">
                    <span>Days Present (የተገኘበት):</span>
                    <strong class="text-success">${reportCard != null ? reportCard.attendancePresent : '0'} days</strong>
                </div>
                <div class="d-flex justify-content-between mb-2 pb-2 border-bottom">
                    <span>Days Absent (የቀረበት):</span>
                    <strong class="text-danger">${reportCard != null ? reportCard.attendanceAbsent : '0'} days</strong>
                </div>
                <div class="d-flex justify-content-between">
                    <span>Student Conduct / ጠባይ:</span>
                    <span class="badge bg-success fs-6">${reportCard != null ? reportCard.conduct : 'A (Excellent)'}</span>
                </div>
            </div>
        </div>
    </div>

    <!-- Sign-off & Seal Block -->
    <div class="row g-4 mt-2 pt-4 border-top">
        <div class="col-md-4">
            <div class="seal-box">
                <small class="text-muted d-block mb-4">Homeroom Teacher / የክፍል ኃላፊ መምህር</small>
                <div style="border-bottom: 1px solid #000; width: 80%; margin: 0 auto 5px;"></div>
                <small class="text-muted">Signature & Date</small>
            </div>
        </div>
        <div class="col-md-4">
            <div class="seal-box">
                <small class="text-muted d-block mb-4">School Seal / የትምህርት ቤቱ ማህተም</small>
                <div style="width: 60px; height: 60px; border: 2px dashed #94A3B8; border-radius: 50%; margin: 0 auto; display: flex; align-items: center; justify-content: center; color: #94A3B8;">
                    <i class="bi bi-award fs-4"></i>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="seal-box">
                <small class="text-muted d-block mb-4">School Principal / የትምህርት ቤቱ ርዕሰ መምህር</small>
                <div style="border-bottom: 1px solid #000; width: 80%; margin: 0 auto 5px;"></div>
                <small class="text-muted">Signature & Approval</small>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
