<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h2 class="fw-bold mb-1" style="color: var(--primary-navy);">
            <i class="bi bi-calendar-check-fill me-2" style="color: var(--primary-blue);"></i>${i18n.get('nav.attendance')}
        </h2>
        <p class="text-muted mb-0">Daily Class Attendance Tracker | የዕለት ተገኝነት መመዝገቢያ (${activeYear.name})</p>
    </div>
</div>

<c:if test="${param.success eq 'saved'}">
    <div class="alert alert-success alert-dismissible fade show">
        <i class="bi bi-check-circle-fill me-2"></i>Attendance saved successfully / የተማሪዎች ተገኝነት በተሳካ ሁኔታ ተመዝግቧል።
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<!-- Selection Card -->
<div class="card shadow-sm border-0 mb-4">
    <div class="card-body">
        <form method="GET" action="${pageContext.request.contextPath}/attendance" class="row g-3 align-items-end">
            <div class="col-md-5">
                <label class="form-label fw-bold small text-muted">Select Class Section / ክፍል ይምረጡ *</label>
                <select name="sectionId" class="form-select" required onchange="this.form.submit()">
                    <option value="">-- Choose Assigned Class --</option>
                    <c:forEach var="s" items="${allowedSections}">
                        <option value="${s.id}" ${selectedSection != null && selectedSection.id == s.id ? 'selected' : ''}>
                            ${s.gradeName} - Section ${s.sectionName} (Room ${s.roomNumber})
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-4">
                <label class="form-label fw-bold small text-muted">Date / ቀን *</label>
                <input type="date" name="date" class="form-control" value="${attendanceDate}" onchange="this.form.submit()" required />
            </div>
            <div class="col-md-3">
                <button type="submit" class="btn btn-outline-primary w-100">
                    <i class="bi bi-search me-1"></i>Load Roster / ፈልግ
                </button>
            </div>
        </form>
    </div>
</div>

<c:if test="${selectedSection != null}">
    <div class="card shadow-sm border-0">
        <div class="card-header bg-white py-3 border-0 d-flex justify-content-between align-items-center flex-wrap gap-2">
            <div>
                <h5 class="fw-bold mb-0" style="color: var(--primary-navy);">
                    <i class="bi bi-people-fill me-2" style="color: var(--primary-blue);"></i>${selectedSection.gradeName} - Section ${selectedSection.sectionName}
                </h5>
                <small class="text-muted">Date: ${attendanceDate} | Total Enrolled: ${roster.size()}</small>
            </div>
            <div class="d-flex gap-2">
                <button type="button" class="btn btn-sm btn-outline-success" onclick="markAll('PRESENT')">
                    <i class="bi bi-check-all me-1"></i>Mark All Present / ሁሉንም አለ በል
                </button>
            </div>
        </div>
        <div class="card-body p-0">
            <c:choose>
                <c:when test="${not empty roster}">
                    <form method="POST" action="${pageContext.request.contextPath}/attendance">
                        <input type="hidden" name="sectionId" value="${selectedSection.id}" />
                        <input type="hidden" name="date" value="${attendanceDate}" />

                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th style="width: 50px;">#</th>
                                        <th>Student ID</th>
                                        <th>Student Name / ስም</th>
                                        <th class="text-center" style="min-width: 320px;">Attendance Status / ሁኔታ</th>
                                        <th>Notes / ማስታወሻ</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="st" items="${roster}" varStatus="status">
                                        <c:set var="curStatus" value="${statusMap[st.id] != null ? statusMap[st.id] : 'PRESENT'}" />
                                        <tr>
                                            <td>${status.index + 1}</td>
                                            <td><span class="badge bg-light text-dark border font-monospace">${st.studentCode}</span></td>
                                            <td>
                                                <div class="fw-bold">${st.fullName}</div>
                                                <c:if test="${not empty st.nameAmharic}">
                                                    <div class="text-muted small">${st.nameAmharic}</div>
                                                </c:if>
                                            </td>
                                            <td class="text-center">
                                                <div class="btn-group w-100" role="group">
                                                    <input type="radio" class="btn-check" name="status_${st.id}" id="p_${st.id}" value="PRESENT" ${curStatus eq 'PRESENT' ? 'checked' : ''} />
                                                    <label class="btn btn-outline-success btn-sm" for="p_${st.id}">
                                                        <i class="bi bi-check-circle me-1"></i>Present (አለ)
                                                    </label>

                                                    <input type="radio" class="btn-check" name="status_${st.id}" id="a_${st.id}" value="ABSENT" ${curStatus eq 'ABSENT' ? 'checked' : ''} />
                                                    <label class="btn btn-outline-danger btn-sm" for="a_${st.id}">
                                                        <i class="bi bi-x-circle me-1"></i>Absent (ቀረ)
                                                    </label>

                                                    <input type="radio" class="btn-check" name="status_${st.id}" id="l_${st.id}" value="LATE" ${curStatus eq 'LATE' ? 'checked' : ''} />
                                                    <label class="btn btn-outline-warning btn-sm" for="l_${st.id}">
                                                        <i class="bi bi-clock me-1"></i>Late (አረፈደ)
                                                    </label>

                                                    <input type="radio" class="btn-check" name="status_${st.id}" id="e_${st.id}" value="EXCUSED" ${curStatus eq 'EXCUSED' ? 'checked' : ''} />
                                                    <label class="btn btn-outline-info btn-sm" for="e_${st.id}">
                                                        <i class="bi bi-envelope-paper me-1"></i>Excused (ፈቃድ)
                                                    </label>
                                                </div>
                                            </td>
                                            <td>
                                                <input type="text" name="notes_${st.id}" class="form-control form-control-sm" placeholder="Optional notes" />
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <div class="p-3 bg-light border-top d-flex justify-content-end">
                            <button type="submit" class="btn btn-primary px-4">
                                <i class="bi bi-save me-1"></i>Save Attendance / መዝግብ
                            </button>
                        </div>
                    </form>
                </c:when>
                <c:otherwise>
                    <div class="text-center py-5 text-muted">
                        <i class="bi bi-inbox fs-1 d-block mb-2"></i>
                        No active students enrolled in this section for ${activeYear.name}.
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</c:if>

<script>
function markAll(status) {
    document.querySelectorAll('input[type="radio"]').forEach(r => {
        if (r.value === status) {
            r.checked = true;
        }
    });
}
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
