<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h2 class="fw-bold mb-1" style="color: var(--primary-navy);">
            <i class="bi bi-megaphone-fill me-2" style="color: var(--primary-blue);"></i>${i18n.get('nav.announcements')}
        </h2>
        <p class="text-muted mb-0">Official School Notices & Academic Calendar Events | የትምህርት ቤቱ ማስታወቂያዎች</p>
    </div>
    <c:if test="${currentUser.role eq 'ADMIN' || currentUser.role eq 'PRINCIPAL'}">
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#newAnnouncementModal">
            <i class="bi bi-plus-circle-fill me-1"></i> Post Notice / ማስታወቂያ ለጥፍ
        </button>
    </c:if>
</div>

<c:if test="${param.success eq 'posted'}">
    <div class="alert alert-success alert-dismissible fade show">
        <i class="bi bi-check-circle-fill me-2"></i>Announcement broadcasted successfully / ማስታወቂያው በተሳካ ሁኔታ ተለጥፏል።
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<div class="row g-4">
    <c:choose>
        <c:when test="${not empty announcements}">
            <c:forEach var="a" items="${announcements}">
                <div class="col-md-6">
                    <div class="card h-100 shadow-sm border-0 border-start border-4 border-primary">
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-start mb-2">
                                <span class="badge bg-primary-subtle text-primary border border-primary">${a.targetRole}</span>
                                <small class="text-muted"><i class="bi bi-calendar-event me-1"></i>${a.publishedDate}</small>
                            </div>
                            <h5 class="fw-bold mb-1" style="color: var(--primary-navy);">${a.titleEn}</h5>
                            <c:if test="${not empty a.titleAm}">
                                <h6 class="text-muted fw-bold mb-3" style="font-family: 'Noto Sans Ethiopic', sans-serif;">${a.titleAm}</h6>
                            </c:if>
                            <p class="card-text mb-3">${a.contentEn}</p>
                            <c:if test="${not empty a.contentAm}">
                                <div class="p-2 rounded bg-light border-start border-3 border-secondary small text-muted mb-3" style="font-family: 'Noto Sans Ethiopic', sans-serif;">
                                    ${a.contentAm}
                                </div>
                            </c:if>
                            <div class="border-top pt-2 d-flex justify-content-between align-items-center text-muted small">
                                <span><i class="bi bi-person me-1"></i>${not empty a.authorName ? a.authorName : 'Administration'}</span>
                                <span class="badge bg-light text-dark border">Official Notice</span>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <div class="col-12">
                <div class="text-center py-5 text-muted card shadow-sm border-0">
                    <i class="bi bi-megaphone fs-1 d-block mb-2"></i>
                    No active announcements at this time.
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Modal: New Announcement -->
<c:if test="${currentUser.role eq 'ADMIN' || currentUser.role eq 'PRINCIPAL'}">
    <div class="modal fade" id="newAnnouncementModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="${pageContext.request.contextPath}/announcements" method="POST">
                    <div class="modal-header">
                        <h5 class="modal-title fw-bold" style="color: var(--primary-navy);">Post New Announcement / ማስታወቂያ ለጥፍ</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row g-3">
                            <div class="col-12">
                                <label class="form-label small fw-bold">Title (English) *</label>
                                <input type="text" name="titleEn" class="form-control" required placeholder="e.g. End of Term Examination Schedule" />
                            </div>
                            <div class="col-12">
                                <label class="form-label small fw-bold">አርዕስት (አማርኛ)</label>
                                <input type="text" name="titleAm" class="form-control" placeholder="ለምሳሌ፡ የ1ኛ መንፈቀ ዓመት ማጠቃለያ ፈተና ፕሮግራም" />
                            </div>
                            <div class="col-12">
                                <label class="form-label small fw-bold">Target Audience / ለማን የሚደርስ *</label>
                                <select name="targetRole" class="form-select" required>
                                    <option value="ALL">All School (ሁሉም - ተማሪዎች፣ መምህራን፣ ሠራተኞች)</option>
                                    <option value="TEACHERS">Faculty / Teachers Only (ለመምህራን ብቻ)</option>
                                    <option value="STUDENTS">Students & Guardians (ለተማሪዎችና ወላጆች)</option>
                                </select>
                            </div>
                            <div class="col-12">
                                <label class="form-label small fw-bold">Message (English) *</label>
                                <textarea name="contentEn" class="form-control" rows="3" required placeholder="Full notice details..."></textarea>
                            </div>
                            <div class="col-12">
                                <label class="form-label small fw-bold">ዝርዝር መልዕክት (አማርኛ)</label>
                                <textarea name="contentAm" class="form-control" rows="3" placeholder="የማስታወቂያው ሙሉ ዝርዝር..."></textarea>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary"><i class="bi bi-broadcast me-1"></i>Publish Notice</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
