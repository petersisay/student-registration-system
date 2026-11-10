<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="util.I18nUtil" %>
<%
    String currentLocale = (String) session.getAttribute("currentLocale");
    if (currentLocale == null) currentLocale = "en";
    String ctx = request.getContextPath();
    String errorKey = (String) request.getAttribute("errorMessage");
    String csrfToken = (String) session.getAttribute("csrfToken");
%>
<!DOCTYPE html>
<html lang="<%= currentLocale %>">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= I18nUtil.getMessage(currentLocale, "auth.login_title") %> - <%= I18nUtil.getMessage(currentLocale, "app.name") %></title>
    <!-- Bootstrap 5.3 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <!-- Custom Design System -->
    <link href="<%= ctx %>/css/style.css?v=3.2" rel="stylesheet">
    <style>
        .login-wrapper {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 2rem 1rem;
        }
        .login-card {
            max-width: 480px;
            width: 100%;
            background: rgba(10, 14, 35, 0.85);
            backdrop-filter: blur(16px);
            border-radius: 20px;
            box-shadow: 0 20px 50px rgba(0, 0, 0, 0.6), 0 0 30px rgba(0, 242, 254, 0.15);
            padding: 2.5rem;
            border: 1px solid rgba(0, 242, 254, 0.25);
            position: relative;
            color: #FFFFFF;
        }
        .login-card h3 {
            color: #FFFFFF !important;
        }
        .login-card .text-muted {
            color: #94A3B8 !important;
        }
        .login-card .form-control {
            background: rgba(255, 255, 255, 0.08);
            border-color: rgba(255, 255, 255, 0.2);
            color: #FFFFFF;
        }
        .login-card .form-control:focus {
            background: rgba(255, 255, 255, 0.14);
            border-color: #00F2FE;
            color: #FFFFFF;
            box-shadow: 0 0 10px rgba(0, 242, 254, 0.35);
        }
        .login-card .form-control::placeholder {
            color: #94A3B8;
        }
    </style>
</head>
<body>

<!-- Futuristic Full-Page Movable Wave Background -->
<div class="futuristic-wave-wrapper">
    <div class="futuristic-wave-img"></div>
    <div class="futuristic-wave-overlay"></div>
</div>

<div class="login-wrapper">
    <div class="login-card">
        <!-- Bilingual selector -->
        <div class="d-flex justify-content-end mb-3">
            <div class="lang-selector" style="background: rgba(255, 255, 255, 0.12); border-color: rgba(0, 242, 254, 0.35);">
                <a href="?lang=en" class="lang-btn <%= "en".equals(currentLocale) ? "active" : "" %>" style="color: #E2E8F0;">EN</a>
                <a href="?lang=am" class="lang-btn <%= "am".equals(currentLocale) ? "active" : "" %>" style="color: #E2E8F0;">አማ</a>
            </div>
        </div>

        <div class="text-center">
            <img src="<%= ctx %>/images/bule-hora-logo.png" alt="Bule Hora Logo" class="school-logo-lg mx-auto d-block">
            <h3 class="fw-bold text-white neon-glow-cyan mb-1"><%= I18nUtil.getMessage(currentLocale, "app.name") %></h3>
            <p class="text-muted small mb-4"><%= I18nUtil.getMessage(currentLocale, "auth.login_subtitle") %></p>
        </div>

        <% 
            String customError = (String) request.getAttribute("errorMessageText");
            String displayError = customError != null ? customError : (errorKey != null ? I18nUtil.getMessage(currentLocale, errorKey) : null);
            if (displayError != null) { 
        %>
            <div class="alert alert-danger py-2 small d-flex align-items-center gap-2 mb-3" role="alert">
                <i class="bi bi-exclamation-triangle-fill"></i>
                <span><%= displayError %></span>
            </div>
        <% } %>

        <% if ("true".equals(request.getParameter("logout"))) { %>
            <div class="alert alert-success py-2 small d-flex align-items-center gap-2 mb-3" role="alert">
                <i class="bi bi-check-circle-fill"></i>
                <span>You have been signed out successfully.</span>
            </div>
        <% } %>

        <form action="<%= ctx %>/login" method="post" id="loginForm">
            <input type="hidden" name="csrfToken" value="<%= csrfToken != null ? csrfToken : "" %>">
            <% if (request.getParameter("redirect") != null) { %>
                <input type="hidden" name="redirect" value="<%= request.getParameter("redirect") %>">
            <% } %>

            <div class="mb-3">
                <label class="form-label fw-semibold small text-muted"><%= I18nUtil.getMessage(currentLocale, "auth.username") %></label>
                <div class="input-group">
                    <span class="input-group-text bg-light text-muted border-end-0"><i class="bi bi-person"></i></span>
                    <input type="text" name="username" id="usernameInput" class="form-control border-start-0" placeholder="Username / የተጠቃሚ ስም" required autofocus>
                </div>
            </div>

            <div class="mb-4">
                <label class="form-label fw-semibold small text-muted"><%= I18nUtil.getMessage(currentLocale, "auth.password") %></label>
                <div class="input-group">
                    <span class="input-group-text bg-light text-muted border-end-0"><i class="bi bi-lock"></i></span>
                    <input type="password" name="password" id="passwordInput" class="form-control border-start-0 border-end-0" placeholder="••••••••" required>
                    <button class="btn btn-outline-secondary border-start-0" type="button" id="togglePasswordBtn" onclick="togglePasswordVisibility()" title="Show/Hide Password" style="background: rgba(255,255,255,0.08); border-color: rgba(255,255,255,0.2); color: #94A3B8;">
                        <i class="bi bi-eye" id="togglePasswordIcon"></i>
                    </button>
                </div>
            </div>

            <button type="submit" class="btn btn-shine btn-shine-primary w-100 py-2 fs-6">
                <i class="bi bi-box-arrow-in-right"></i> <%= I18nUtil.getMessage(currentLocale, "auth.login") %>
            </button>
        </form>

        <!-- 1-Click Quick Demo Credentials Pill -->
        <div class="mt-4 p-3 rounded" style="background: rgba(255, 255, 255, 0.05); border: 1px dashed rgba(0, 242, 254, 0.35);">
            <div class="d-flex align-items-center justify-content-between mb-2">
                <span class="fw-bold small" style="color: #00F2FE;"><i class="bi bi-key-fill text-warning me-1"></i> Quick Demo Login (Click to Fill):</span>
                <span class="badge bg-info text-dark" style="font-size: 0.65rem;">1-Click</span>
            </div>
            <div class="d-flex flex-wrap gap-2">
                <button type="button" class="btn btn-sm btn-outline-light text-nowrap" style="font-size: 0.75rem; border-color: rgba(0,242,254,0.4);" onclick="fillCredentials('admin', 'admin123')">
                    <i class="bi bi-shield-lock-fill text-warning"></i> Admin
                </button>
                <button type="button" class="btn btn-sm btn-outline-light text-nowrap" style="font-size: 0.75rem; border-color: rgba(0,242,254,0.4);" onclick="fillCredentials('teacher.abebe', 'teacher123')">
                    <i class="bi bi-person-workspace text-info"></i> Teacher
                </button>
                <button type="button" class="btn btn-sm btn-outline-light text-nowrap" style="font-size: 0.75rem; border-color: rgba(0,242,254,0.4);" onclick="fillCredentials('principal', 'principal123')">
                    <i class="bi bi-award-fill text-success"></i> Principal
                </button>
                <button type="button" class="btn btn-sm btn-outline-light text-nowrap" style="font-size: 0.75rem; border-color: rgba(0,242,254,0.4);" onclick="fillCredentials('registrar', 'registrar123')">
                    <i class="bi bi-card-checklist text-primary"></i> Registrar
                </button>
                <button type="button" class="btn btn-sm btn-outline-light text-nowrap" style="font-size: 0.75rem; border-color: rgba(0,242,254,0.4);" onclick="fillCredentials('student.dawit', 'student123')">
                    <i class="bi bi-backpack-fill text-warning"></i> Student
                </button>
            </div>
        </div>

        <div class="text-center mt-3">
            <a href="<%= ctx %>/index.jsp" class="text-decoration-none small text-muted">
                <i class="bi bi-arrow-left"></i> Return to Public Registration
            </a>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/js/app.js"></script>
<script>
function fillCredentials(u, p) {
    var uInput = document.getElementById('usernameInput');
    var pInput = document.getElementById('passwordInput');
    if (uInput && pInput) {
        uInput.value = u;
        pInput.value = p;
        uInput.focus();
    }
}

function togglePasswordVisibility() {
    var pInput = document.getElementById('passwordInput');
    var icon = document.getElementById('togglePasswordIcon');
    if (!pInput || !icon) return;
    if (pInput.type === 'password') {
        pInput.type = 'text';
        icon.classList.remove('bi-eye');
        icon.classList.add('bi-eye-slash');
    } else {
        pInput.type = 'password';
        icon.classList.remove('bi-eye-slash');
        icon.classList.add('bi-eye');
    }
}
</script>
</body>
</html>
