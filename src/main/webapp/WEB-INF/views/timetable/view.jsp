<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h2 class="fw-bold mb-1" style="color: var(--primary-navy);">
            <i class="bi bi-clock-history me-2" style="color: var(--primary-blue);"></i>${i18n.get('nav.timetable')}
        </h2>
        <p class="text-muted mb-0">Weekly Class Timetable & Schedule | የክፍለ ጊዜ ሰሌዳ (${activeYear.name})</p>
    </div>
    <c:if test="${currentUser.role eq 'ADMIN'}">
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#newPeriodModal">
            <i class="bi bi-plus-circle-fill me-1"></i> Add Timetable Period / ክፍለ ጊዜ መድብ
        </button>
    </c:if>
</div>

<c:if test="${param.success eq 'saved'}">
    <div class="alert alert-success alert-dismissible fade show">
        <i class="bi bi-check-circle-fill me-2"></i>Timetable period scheduled successfully / ክፍለ ጊዜው በተሳካ ሁኔታ ተመድቧል።
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<c:if test="${currentUser.role ne 'STUDENT' && currentUser.role ne 'TEACHER'}">
    <div class="card shadow-sm border-0 mb-4">
        <div class="card-body">
            <form method="GET" action="${pageContext.request.contextPath}/timetable" class="row g-3 align-items-end">
                <div class="col-md-9">
                    <label class="form-label small fw-bold text-muted">Select Class Section / ክፍል ይምረጡ *</label>
                    <select name="sectionId" class="form-select" onchange="this.form.submit()">
                        <c:forEach var="s" items="${sections}">
                            <option value="${s.id}" ${selectedSectionId == s.id ? 'selected' : ''}>
                                ${s.gradeName} - Section ${s.sectionName} (Room ${s.roomNumber})
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-3">
                    <button type="submit" class="btn btn-outline-primary w-100">
                        <i class="bi bi-search me-1"></i>View Schedule
                    </button>
                </div>
            </form>
        </div>
    </div>
</c:if>

<!-- Weekly Timetable Grid -->
<div class="card shadow-sm border-0">
    <div class="card-header bg-white py-3 border-0">
        <h5 class="fw-bold mb-0" style="color: var(--primary-navy);">
            <i class="bi bi-calendar3 me-2" style="color: var(--primary-blue);"></i>Weekly Class Schedule (ከሰኞ - አርብ)
        </h5>
    </div>
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table table-bordered text-center align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th style="width: 120px;">Period</th>
                        <th>Monday / ሰኞ</th>
                        <th>Tuesday / ማክሰኞ</th>
                        <th>Wednesday / ረቡዕ</th>
                        <th>Thursday / ሐሙስ</th>
                        <th>Friday / ዓርብ</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" begin="1" end="6">
                        <tr>
                            <td class="table-light fw-bold">
                                Period ${p}
                                <div class="text-muted small">
                                    <c:choose>
                                        <c:when test="${p == 1}">08:30 - 09:15</c:when>
                                        <c:when test="${p == 2}">09:15 - 10:00</c:when>
                                        <c:when test="${p == 3}">10:15 - 11:00</c:when>
                                        <c:when test="${p == 4}">11:00 - 11:45</c:when>
                                        <c:when test="${p == 5}">01:30 - 02:15</c:when>
                                        <c:when test="${p == 6}">02:15 - 03:00</c:when>
                                    </c:choose>
                                </div>
                            </td>
                            <c:forEach var="day" items="${['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY']}">
                                <td>
                                    <c:set var="matched" value="false" />
                                    <c:forEach var="item" items="${entries}">
                                        <c:if test="${item.periodNumber == p && item.dayOfWeek eq day}">
                                            <div class="p-2 border rounded-3 bg-light">
                                                <div class="fw-bold text-primary">${item.subjectName}</div>
                                                <div class="small text-muted"><i class="bi bi-person me-1"></i>${item.teacherName}</div>
                                                <c:if test="${currentUser.role eq 'TEACHER'}">
                                                    <span class="badge bg-secondary-subtle text-secondary small">${item.fullClassName}</span>
                                                </c:if>
                                            </div>
                                            <c:set var="matched" value="true" />
                                        </c:if>
                                    </c:forEach>
                                    <c:if test="${!matched}">
                                        <span class="text-muted small">-- Free --</span>
                                    </c:if>
                                </td>
                            </c:forEach>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Modal: New Period -->
<c:if test="${currentUser.role eq 'ADMIN'}">
    <div class="modal fade" id="newPeriodModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="${pageContext.request.contextPath}/timetable" method="POST">
                    <div class="modal-header">
                        <h5 class="modal-title fw-bold" style="color: var(--primary-navy);">Schedule Period / ክፍለ ጊዜ መድብ</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row g-3">
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">Class Section *</label>
                                <select name="classSectionId" class="form-select" required>
                                    <c:forEach var="s" items="${sections}">
                                        <option value="${s.id}" ${selectedSectionId == s.id ? 'selected' : ''}>
                                            ${s.gradeName} - Section ${s.sectionName}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">Day of Week *</label>
                                <select name="dayOfWeek" class="form-select" required>
                                    <option value="MONDAY">Monday / ሰኞ</option>
                                    <option value="TUESDAY">Tuesday / ማክሰኞ</option>
                                    <option value="WEDNESDAY">Wednesday / ረቡዕ</option>
                                    <option value="THURSDAY">Thursday / ሐሙስ</option>
                                    <option value="FRIDAY">Friday / ዓርብ</option>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">Period Number *</label>
                                <select name="periodNumber" class="form-select" required>
                                    <option value="1">Period 1 (08:30 - 09:15)</option>
                                    <option value="2">Period 2 (09:15 - 10:00)</option>
                                    <option value="3">Period 3 (10:15 - 11:00)</option>
                                    <option value="4">Period 4 (11:00 - 11:45)</option>
                                    <option value="5">Period 5 (01:30 - 02:15)</option>
                                    <option value="6">Period 6 (02:15 - 03:00)</option>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">Subject *</label>
                                <select name="subjectId" class="form-select" required>
                                    <c:forEach var="sub" items="${subjects}">
                                        <option value="${sub.id}">${sub.name} (${sub.nameAmharic})</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-12">
                                <label class="form-label small fw-bold">Assigned Teacher *</label>
                                <select name="teacherId" class="form-select" required>
                                    <c:forEach var="t" items="${teachers}">
                                        <option value="${t.id}">${t.fullName} (${t.specialization})</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary"><i class="bi bi-save me-1"></i>Save Schedule</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
