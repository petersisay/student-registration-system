<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h2 class="fw-bold mb-1" style="color: var(--primary-navy);">
            <i class="bi bi-person-badge-fill me-2" style="color: var(--primary-blue);"></i>${i18n.get('nav.teachers')}
        </h2>
        <p class="text-muted mb-0">${activeYear.name}</p>
    </div>
    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#newTeacherModal">
        <i class="bi bi-person-plus-fill me-1"></i> Add Teacher / መምህር መዝግብ
    </button>
</div>

<c:if test="${param.success eq 'created'}">
    <div class="alert alert-success alert-dismissible fade show">
        <i class="bi bi-check-circle-fill me-2"></i>Teacher created successfully / መምህሩ በተሳካ ሁኔታ ተመዝግቧል።
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>
<c:if test="${param.success eq 'assigned'}">
    <div class="alert alert-success alert-dismissible fade show">
        <i class="bi bi-check-circle-fill me-2"></i>Class assigned successfully / ክፍለ ጊዜ በተሳካ ሁኔታ ተመድቧል።
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>
<c:if test="${param.success eq 'deleted'}">
    <div class="alert alert-info alert-dismissible fade show">
        <i class="bi bi-info-circle-fill me-2"></i>Assignment removed / ምደባው ተሰርዟል።
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<div class="row g-4">
    <!-- Teacher List -->
    <div class="${teacher != null ? 'col-lg-6' : 'col-12'}">
        <div class="card h-100 shadow-sm border-0">
            <div class="card-header bg-white py-3 border-0">
                <h5 class="fw-bold mb-0" style="color: var(--primary-navy);">
                    <i class="bi bi-people-fill me-2" style="color: var(--primary-blue);"></i>Faculty Directory (${teachers.size()})
                </h5>
            </div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>Code</th>
                                <th>Name / ስም</th>
                                <th>Specialization</th>
                                <th>Contact</th>
                                <th>Status</th>
                                <th class="text-end">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="t" items="${teachers}">
                                <tr class="${teacher != null && teacher.id == t.id ? 'table-primary' : ''}">
                                    <td><span class="badge bg-light text-dark border font-monospace">${t.teacherCode}</span></td>
                                    <td>
                                        <div class="fw-bold">${t.fullName}</div>
                                        <c:if test="${not empty t.nameAmharic}">
                                            <div class="text-muted small">${t.nameAmharic}</div>
                                        </c:if>
                                    </td>
                                    <td><span class="badge bg-info-subtle text-info border border-info">${t.specialization}</span></td>
                                    <td>
                                        <div class="small"><i class="bi bi-envelope me-1"></i>${t.email}</div>
                                        <div class="small text-muted"><i class="bi bi-telephone me-1"></i>${t.phone}</div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${t.active}">
                                                <span class="badge bg-success">Active</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary">Inactive</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-end">
                                        <a href="${pageContext.request.contextPath}/admin/teachers/assign?teacherId=${t.id}" class="btn btn-sm btn-outline-primary">
                                            <i class="bi bi-diagram-3-fill me-1"></i>Assign
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

    <!-- Assignments Panel for Selected Teacher -->
    <c:if test="${teacher != null}">
        <div class="col-lg-6">
            <div class="card h-100 shadow-sm border-0">
                <div class="card-header bg-white py-3 border-0 d-flex justify-content-between align-items-center">
                    <div>
                        <h5 class="fw-bold mb-0 text-primary">
                            <i class="bi bi-diagram-3-fill me-2"></i>Assignments: ${teacher.fullName}
                        </h5>
                        <small class="text-muted">${teacher.teacherCode} | ${activeYear.name}</small>
                    </div>
                    <a href="${pageContext.request.contextPath}/admin/teachers" class="btn btn-sm btn-light">
                        <i class="bi bi-x-lg"></i>
                    </a>
                </div>
                <div class="card-body">
                    <!-- New Assignment Form -->
                    <form action="${pageContext.request.contextPath}/admin/teachers" method="POST" class="p-3 bg-light rounded-3 mb-4 border">
                        <input type="hidden" name="action" value="assignClass" />
                        <input type="hidden" name="teacherId" value="${teacher.id}" />
                        <h6 class="fw-bold mb-3" style="color: var(--primary-navy);">Assign Subject & Class Section</h6>
                        <div class="row g-2">
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">Class Section / ክፍል</label>
                                <select name="classSectionId" class="form-select form-select-sm" required>
                                    <c:forEach var="s" items="${sections}">
                                        <option value="${s.id}">${s.gradeName} - Section ${s.sectionName} (${s.roomNumber})</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">Subject / የትምህርት ዓይነት</label>
                                <select name="subjectId" class="form-select form-select-sm" required>
                                    <c:forEach var="sub" items="${subjects}">
                                        <option value="${sub.id}">${sub.name} (${sub.nameAmharic})</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-12 mt-2">
                                <div class="form-check">
                                    <input class="form-check-input" type="checkbox" name="isHomeroom" id="homeroomCheck">
                                    <label class="form-check-label small" for="homeroomCheck">
                                        Homeroom Teacher (የክፍል ኃላፊ)
                                    </label>
                                </div>
                            </div>
                            <div class="col-12 mt-3">
                                <button type="submit" class="btn btn-sm btn-primary w-100">
                                    <i class="bi bi-plus-circle me-1"></i>Save Assignment / ምደባውን መዝግብ
                                </button>
                            </div>
                        </div>
                    </form>

                    <!-- Current Assignments List -->
                    <h6 class="fw-bold mb-2" style="color: var(--primary-navy);">Active Class & Subject Allocations</h6>
                    <c:choose>
                        <c:when test="${not empty assignments}">
                            <div class="list-group">
                                <c:forEach var="a" items="${assignments}">
                                    <div class="list-group-item d-flex justify-content-between align-items-center">
                                        <div>
                                            <div class="fw-bold text-dark">
                                                ${a.gradeName} - Section ${a.sectionName}
                                                <c:if test="${a.homeroom}">
                                                    <span class="badge bg-warning text-dark ms-2">Homeroom</span>
                                                </c:if>
                                            </div>
                                            <div class="text-muted small">
                                                <i class="bi bi-book me-1"></i>${a.subjectName} (${a.subjectNameAmharic})
                                            </div>
                                        </div>
                                        <form action="${pageContext.request.contextPath}/admin/teachers" method="POST" onsubmit="return confirm('Remove this assignment?');">
                                            <input type="hidden" name="action" value="deleteAssignment" />
                                            <input type="hidden" name="assignmentId" value="${a.id}" />
                                            <input type="hidden" name="teacherId" value="${teacher.id}" />
                                            <button type="submit" class="btn btn-sm btn-outline-danger">
                                                <i class="bi bi-trash"></i>
                                            </button>
                                        </form>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center py-4 text-muted">
                                <i class="bi bi-inbox fs-2 d-block mb-1"></i>
                                No active assignments for this teacher in ${activeYear.name}.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </c:if>
</div>

<!-- Modal: New Teacher -->
<div class="modal fade" id="newTeacherModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/admin/teachers" method="POST">
                <input type="hidden" name="action" value="createTeacher" />
                <div class="modal-header">
                    <h5 class="modal-title fw-bold" style="color: var(--primary-navy);">Register Teacher / መምህር መዝግብ</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">First Name (English) *</label>
                            <input type="text" name="firstName" class="form-control" required />
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">Last Name (English) *</label>
                            <input type="text" name="lastName" class="form-control" required />
                        </div>
                        <div class="col-12">
                            <label class="form-label small fw-bold">ሙሉ ስም (አማርኛ)</label>
                            <input type="text" name="nameAmharic" class="form-control" placeholder="ለምሳሌ፡ አልማዝ ታደሰ" />
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">Email *</label>
                            <input type="email" name="email" class="form-control" required />
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">Phone</label>
                            <input type="text" name="phone" class="form-control" placeholder="+251-9..." />
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">Specialization</label>
                            <input type="text" name="specialization" class="form-control" placeholder="e.g. Mathematics" />
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">Qualification</label>
                            <input type="text" name="qualification" class="form-control" placeholder="e.g. B.Ed in Mathematics" />
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary"><i class="bi bi-save me-1"></i>Save Teacher</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
