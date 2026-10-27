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
    <link href="<%= ctx %>/css/style.css" rel="stylesheet">
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
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(12px);
            border-radius: 20px;
            box-shadow: 0 15px 35px rgba(23, 50, 77, 0.18);
            padding: 2.5rem;
            border: 1px solid rgba(255, 255, 255, 0.6);
            position: relative;
        }
        .credentials-pill {
            font-size: 0.8rem;
            background: #F8FAFC;
            border: 1px dashed var(--border-color);
            border-radius: 8px;
            padding: 0.75rem;
            margin-top: 1.5rem;
        }
    </style>
</head>
<body>

<!-- Movable and Animated School Landscape Background -->
<div class="movable-bg-wrapper">
    <div class="movable-bg-img"></div>
    <div class="movable-bg-overlay"></div>
</div>

<div class="login-wrapper">
    <div class="login-card">
        <!-- Bilingual selector -->
        <div class="d-flex justify-content-end mb-3">
            <div class="lang-selector" style="background: rgba(23, 50, 77, 0.08); border-color: #CBD5E1;">
                <a href="?lang=en" class="lang-btn <%= "en".equals(currentLocale) ? "active" : "" %>" style="<%= !"en".equals(currentLocale) ? "color: var(--primary-navy);" : "" %>">EN</a>
                <a href="?lang=am" class="lang-btn <%= "am".equals(currentLocale) ? "active" : "" %>" style="<%= !"am".equals(currentLocale) ? "color: var(--primary-navy);" : "" %>">አማ</a>
            </div>
        </div>

        <div class="text-center">
            <img src="<%= ctx %>/images/bule-hora-logo.png" alt="Bule Hora Logo" class="school-logo-lg mx-auto d-block">
            <h3 class="fw-bold text-navy mb-1" style="color: var(--primary-navy);"><%= I18nUtil.getMessage(currentLocale, "app.name") %></h3>
            <p class="text-muted small mb-4"><%= I18nUtil.getMessage(currentLocale, "auth.login_subtitle") %></p>
        </div>

        <% if (errorKey != null) { %>
            <div class="alert alert-danger py-2 small d-flex align-items-center gap-2 mb-3" role="alert">
                <i class="bi bi-exclamation-triangle-fill"></i>
                <span><%= I18nUtil.getMessage(currentLocale, errorKey) %></span>
            </div>
        <% } %>

        <% if ("true".equals(request.getParameter("logout"))) { %>
            <div class="alert alert-success py-2 small d-flex align-items-center gap-2 mb-3" role="alert">
                <i class="bi bi-check-circle-fill"></i>
                <span>You have been signed out successfully.</span>
            </div>
        <% } %>

        <form action="<%= ctx %>/login" method="post">
            <input type="hidden" name="csrfToken" value="<%= csrfToken != null ? csrfToken : "" %>">
            <% if (request.getParameter("redirect") != null) { %>
                <input type="hidden" name="redirect" value="<%= request.getParameter("redirect") %>">
            <% } %>

            <div class="mb-3">
                <label class="form-label fw-semibold small text-muted"><%= I18nUtil.getMessage(currentLocale, "auth.username") %></label>
                <div class="input-group">
                    <span class="input-group-text bg-light text-muted border-end-0"><i class="bi bi-person"></i></span>
                    <input type="text" name="username" class="form-control border-start-0" placeholder="e.g. admin, teacher.abebe, student.dawit" required autofocus>
                </div>
            </div>

            <div class="mb-4">
                <label class="form-label fw-semibold small text-muted"><%= I18nUtil.getMessage(currentLocale, "auth.password") %></label>
                <div class="input-group">
                    <span class="input-group-text bg-light text-muted border-end-0"><i class="bi bi-lock"></i></span>
                    <input type="password" name="password" class="form-control border-start-0" placeholder="••••••••" required>
                </div>
            </div>

            <button type="submit" class="btn btn-shine btn-shine-primary w-100 py-2 fs-6">
                <i class="bi bi-box-arrow-in-right"></i> <%= I18nUtil.getMessage(currentLocale, "auth.login") %>
            </button>
        </form>

        <!-- Quick Demo Credentials Guide for Testing -->
        <div class="credentials-pill">
            <div class="fw-bold text-dark small mb-1"><i class="bi bi-key-fill text-warning"></i> Demo Accounts (Grades 1-8):</div>
            <div class="d-flex flex-wrap gap-2 text-secondary" style="font-size: 0.75rem;">
                <span class="badge bg-light text-dark border">Admin: <code>admin</code> / <code>admin123</code></span>
                <span class="badge bg-light text-dark border">Teacher: <code>teacher.abebe</code> / <code>teacher123</code></span>
                <span class="badge bg-light text-dark border">Registrar: <code>registrar</code> / <code>registrar123</code></span>
                <span class="badge bg-light text-dark border">Principal: <code>principal</code> / <code>principal123</code></span>
                <span class="badge bg-light text-dark border">Student: <code>student.dawit</code> / <code>student123</code></span>
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
</body>
</html>
