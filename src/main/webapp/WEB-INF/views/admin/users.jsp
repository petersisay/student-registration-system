<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<!-- Page Title & Header Actions -->
<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h2 class="fw-bold mb-1" style="color: var(--primary-navy);">
            <i class="bi bi-people-gear text-primary me-2"></i>User Management / የተጠቃሚዎች አስተዳደር
        </h2>
        <p class="text-muted mb-0">
            Provision and manage login accounts for all teachers, administrative staff, and students.
        </p>
    </div>
    <div class="d-flex gap-2">
        <button class="btn btn-shine btn-shine-primary" data-bs-toggle="modal" data-bs-target="#newUserModal">
            <i class="bi bi-person-plus-fill me-1"></i> Register New User / አዲስ ተጠቃሚ መዝግብ
        </button>
    </div>
</div>

<!-- Alert Notifications -->
<c:if test="${param.success eq 'created'}">
    <div class="alert alert-success alert-dismissible fade show shadow-sm">
        <i class="bi bi-check-circle-fill me-2"></i>User account created successfully!
        <c:if test="${not empty param.user}">
            Login username: <strong class="font-monospace"><c:out value="${param.user}"/></strong>
        </c:if>
        — The user can now log in using this username and assigned password. / ተጠቃሚው በተሳካ ሁኔታ ተመዝግቧል።
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>
<c:if test="${param.success eq 'updated'}">
    <div class="alert alert-success alert-dismissible fade show shadow-sm">
        <i class="bi bi-check-circle-fill me-2"></i>User account information updated successfully. / የተጠቃሚው መረጃ ታድሷል።
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>
<c:if test="${param.success eq 'password_reset'}">
    <div class="alert alert-success alert-dismissible fade show shadow-sm">
        <i class="bi bi-key-fill me-2"></i>Password has been reset successfully! The user will log in with this new password next time. / የይለፍ ቃሉ በተሳካ ሁኔታ ተቀይሯል።
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>
<c:if test="${param.error eq 'username_taken'}">
    <div class="alert alert-danger alert-dismissible fade show shadow-sm">
        <i class="bi bi-exclamation-triangle-fill me-2"></i><strong>Error:</strong> This username is already registered to another user account. Please choose a different username. / ይህ የተጠቃሚ ስም አስቀድሞ ተይዟል።
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>
<c:if test="${param.error eq 'missing_fields'}">
    <div class="alert alert-warning alert-dismissible fade show shadow-sm">
        <i class="bi bi-exclamation-circle-fill me-2"></i>Please fill in all required fields (Full Name, Role, Username, Password). / እባክዎ ሁሉንም አስፈላጊ መረጃዎች ይሙሉ::
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<!-- Search and Role Filters -->
<div class="card-modern mb-4 p-3">
    <form action="${pageContext.request.contextPath}/admin/users" method="GET" class="row g-2 align-items-center">
        <div class="col-md-5">
            <div class="input-group">
                <span class="input-group-text bg-white"><i class="bi bi-search text-muted"></i></span>
                <input type="text" name="search" class="form-control" placeholder="Search by name, username, or email..." value="<c:out value="${search}"/>" />
            </div>
        </div>
        <div class="col-md-4">
            <div class="input-group">
                <span class="input-group-text bg-white"><i class="bi bi-funnel text-muted"></i></span>
                <select name="role" class="form-select">
                    <option value="ALL">All Roles (${totalUsers})</option>
                    <c:forEach var="r" items="${allRoles}">
                        <option value="${r.name()}" ${selectedRole eq r.name() ? 'selected' : ''}>
                            ${r.labelEn} (${r.labelAm})
                        </option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div class="col-md-3 d-flex gap-2">
            <button type="submit" class="btn btn-outline-primary w-100">
                <i class="bi bi-filter me-1"></i> Filter
            </button>
            <c:if test="${not empty search or (not empty selectedRole and selectedRole ne 'ALL')}">
                <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline-secondary">
                    <i class="bi bi-x-circle"></i>
                </a>
            </c:if>
        </div>
    </form>
</div>

<!-- Users Table -->
<div class="card-modern p-0">
    <div class="card-header-clean px-4 py-3 border-bottom d-flex justify-content-between align-items-center">
        <h5 class="card-title-clean mb-0">
            <i class="bi bi-person-lines-fill text-primary me-2"></i>System User Accounts (${users.size()})
        </h5>
        <span class="badge bg-light text-dark border">
            Total Accounts: <strong>${totalUsers}</strong>
        </span>
    </div>
    <div class="table-responsive">
        <table class="table table-clean table-hover align-middle mb-0">
            <thead>
                <tr>
                    <th>Full Name / ስም</th>
                    <th>Login Username</th>
                    <th>Role / ኃላፊነት</th>
                    <th>Contact</th>
                    <th>Status</th>
                    <th>Created</th>
                    <th class="text-end">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${not empty users}">
                        <c:forEach var="u" items="${users}">
                            <tr>
                                <td>
                                    <div class="fw-bold text-navy"><c:out value="${u.fullName}"/></div>
                                    <div class="text-muted small">ID: #${u.id}</div>
                                </td>
                                <td>
                                    <span class="badge bg-primary-subtle text-primary border border-primary font-monospace px-2 py-1">
                                        <i class="bi bi-person-fill me-1"></i><c:out value="${u.username}"/>
                                    </span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${u.role.name() eq 'ADMIN'}">
                                            <span class="badge bg-dark text-warning border border-warning px-2 py-1">${u.role.labelEn}</span>
                                        </c:when>
                                        <c:when test="${u.role.name() eq 'PRINCIPAL'}">
                                            <span class="badge bg-purple text-white px-2 py-1" style="background-color: #6f42c1;">${u.role.labelEn}</span>
                                        </c:when>
                                        <c:when test="${u.role.name() eq 'REGISTRAR'}">
                                            <span class="badge bg-info text-dark px-2 py-1">${u.role.labelEn}</span>
                                        </c:when>
                                        <c:when test="${u.role.name() eq 'TEACHER'}">
                                            <span class="badge bg-primary px-2 py-1">${u.role.labelEn}</span>
                                        </c:when>
                                        <c:when test="${u.role.name() eq 'ATTENDANCE_OFFICER'}">
                                            <span class="badge bg-teal text-white px-2 py-1" style="background-color: #0F9D8A;">${u.role.labelEn}</span>
                                        </c:when>
                                        <c:when test="${u.role.name() eq 'STUDENT'}">
                                            <span class="badge bg-success px-2 py-1">${u.role.labelEn}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary px-2 py-1">${u.role.labelEn}</span>
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="small text-muted mt-1">${u.role.labelAm}</div>
                                </td>
                                <td>
                                    <c:if test="${not empty u.email}">
                                        <div class="small"><i class="bi bi-envelope me-1 text-muted"></i><c:out value="${u.email}"/></div>
                                    </c:if>
                                    <c:if test="${not empty u.phone}">
                                        <div class="small text-muted"><i class="bi bi-telephone me-1"></i><c:out value="${u.phone}"/></div>
                                    </c:if>
                                    <c:if test="${empty u.email and empty u.phone}">
                                        <span class="text-muted small">&mdash;</span>
                                    </c:if>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${u.active}">
                                            <span class="badge bg-success-subtle text-success border border-success">
                                                <i class="bi bi-check-circle me-1"></i>Active
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-danger-subtle text-danger border border-danger">
                                                <i class="bi bi-dash-circle me-1"></i>Disabled
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <span class="small text-muted">
                                        ${u.createdAt != null ? u.createdAt.toString().substring(0, 10) : 'Active'}
                                    </span>
                                </td>
                                <td class="text-end">
                                    <div class="d-flex justify-content-end gap-1">
                                        <button type="button" class="btn btn-sm btn-outline-warning" data-bs-toggle="modal" data-bs-target="#resetUserModal${u.id}" title="Reset Password / የይለፍ ቃል ቀይር">
                                            <i class="bi bi-key-fill me-1"></i>Password
                                        </button>
                                        <button type="button" class="btn btn-sm btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#editUserModal${u.id}" title="Edit User Account / መረጃ አድስ">
                                            <i class="bi bi-pencil-square"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="7" class="text-center py-5 text-muted">
                                <i class="bi bi-person-x fs-1 d-block mb-2 text-secondary"></i>
                                No user accounts found matching your filter criteria.
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</div>

<!-- Modal: Register New User -->
<div class="modal fade" id="newUserModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/admin/users" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                <input type="hidden" name="action" value="createUser" />
                <div class="modal-header">
                    <h5 class="modal-title fw-bold" style="color: var(--primary-navy);">
                        <i class="bi bi-person-plus-fill text-primary me-2"></i>Register New User Account / አዲስ ተጠቃሚ መዝግብ
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">Full Name (ሙሉ ስም) *</label>
                            <input type="text" name="fullName" class="form-control" placeholder="e.g. Abebe Bikila" required />
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">User Role (ኃላፊነት) *</label>
                            <select name="role" class="form-select" required>
                                <option value="">-- Select Role / ሚና ይምረጡ --</option>
                                <c:forEach var="r" items="${allRoles}">
                                    <option value="${r.name()}">${r.labelEn} (${r.labelAm})</option>
                                </c:forEach>
                            </select>
                        </div>

                        <!-- Credentials Box -->
                        <div class="col-12">
                            <div class="p-3 bg-light rounded border">
                                <h6 class="fw-bold mb-1 text-primary">
                                    <i class="bi bi-shield-lock-fill me-1"></i>Login Credentials / የመግቢያ መረጃ
                                </h6>
                                <p class="text-muted small mb-3">
                                    Specify the username and password that this user will use to sign in every time. / ተጠቃሚው ወደ ሲስተሙ የሚገቡበትን የተጠቃሚ ስምና የይለፍ ቃል እዚህ ይስጡ።
                                </p>
                                <div class="row g-2">
                                    <div class="col-md-6">
                                        <label class="form-label small fw-bold">Username (የተጠቃሚ ስም) *</label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="bi bi-at"></i></span>
                                            <input type="text" name="username" class="form-control font-monospace" placeholder="e.g. teacher.almaz" required />
                                        </div>
                                        <small class="text-muted">Must be unique across all system users.</small>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label small fw-bold">Password (የይለፍ ቃል) *</label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="bi bi-key"></i></span>
                                            <input type="text" name="password" class="form-control font-monospace" placeholder="e.g. Pass@2026" required minlength="4" />
                                        </div>
                                        <small class="text-muted">Give this password to the user to log in.</small>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <label class="form-label small fw-bold">Email Address (ኢሜይል)</label>
                            <input type="email" name="email" class="form-control" placeholder="user@addistesfa.edu.et" />
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">Phone Number (ስልክ ቁጥር)</label>
                            <input type="text" name="phone" class="form-control" placeholder="+251-9..." />
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-bold">Preferred Language / ቋንቋ</label>
                            <select name="preferredLocale" class="form-select">
                                <option value="en">English</option>
                                <option value="am">አማርኛ (Amharic)</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary"><i class="bi bi-save me-1"></i>Create User Account</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modals: Edit User and Reset Password per user -->
<c:forEach var="u" items="${users}">
    <!-- Reset Password Modal -->
    <div class="modal fade" id="resetUserModal${u.id}" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="${pageContext.request.contextPath}/admin/users" method="POST">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                    <input type="hidden" name="action" value="resetPassword" />
                    <input type="hidden" name="userId" value="${u.id}" />
                    <div class="modal-header">
                        <h5 class="modal-title fw-bold" style="color: var(--primary-navy);">
                            <i class="bi bi-key-fill text-warning me-1"></i>Reset Password / የይለፍ ቃል ቀይር
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <p class="mb-3">
                            Assign a new login password for <strong><c:out value="${u.fullName}"/></strong> 
                            (<span class="font-monospace text-primary">@<c:out value="${u.username}"/></span>).
                        </p>
                        <div class="mb-3">
                            <label class="form-label small fw-bold">New Password (አዲስ የይለፍ ቃል) *</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-lock"></i></span>
                                <input type="password" name="newPassword" class="form-control font-monospace" placeholder="Enter new password" required minlength="4" />
                            </div>
                            <small class="text-muted">The user will use this new password next time they log in.</small>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-warning"><i class="bi bi-check-lg me-1"></i>Update Password</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Edit User Modal -->
    <div class="modal fade" id="editUserModal${u.id}" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <form action="${pageContext.request.contextPath}/admin/users" method="POST">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
                    <input type="hidden" name="action" value="updateUser" />
                    <input type="hidden" name="userId" value="${u.id}" />
                    <div class="modal-header">
                        <h5 class="modal-title fw-bold" style="color: var(--primary-navy);">
                            <i class="bi bi-pencil-square text-primary me-2"></i>Edit User Account / መረጃ አድስ
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row g-3">
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">Full Name (ሙሉ ስም) *</label>
                                <input type="text" name="fullName" class="form-control" value="<c:out value="${u.fullName}"/>" required />
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">Login Username *</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-at"></i></span>
                                    <input type="text" name="username" class="form-control font-monospace" value="<c:out value="${u.username}"/>" required />
                                </div>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">User Role *</label>
                                <select name="role" class="form-select" required>
                                    <c:forEach var="r" items="${allRoles}">
                                        <option value="${r.name()}" ${u.role == r ? 'selected' : ''}>${r.labelEn} (${r.labelAm})</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">Email Address</label>
                                <input type="email" name="email" class="form-control" value="<c:out value="${u.email}"/>" />
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">Phone Number</label>
                                <input type="text" name="phone" class="form-control" value="<c:out value="${u.phone}"/>" />
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">Account Status</label>
                                <div class="form-check form-switch mt-2">
                                    <input class="form-check-input" type="checkbox" name="isActive" value="true" id="activeSwitch${u.id}" ${u.active ? 'checked' : ''} />
                                    <label class="form-check-label" for="activeSwitch${u.id}">Active Account (ተጠቃሚው መግባት ይችላል)</label>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary"><i class="bi bi-save me-1"></i>Save Changes</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</c:forEach>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
