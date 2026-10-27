<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%@ page import="model.Role" %>
<%@ page import="util.I18nUtil" %>
<%
    String currentLocale = (String) session.getAttribute("currentLocale");
    if (currentLocale == null) currentLocale = "en";
    User currentUser = (User) session.getAttribute("currentUser");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="<%= currentLocale %>">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= I18nUtil.getMessage(currentLocale, "app.name") %></title>
    <!-- Bootstrap 5.3 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <!-- Custom Application CSS with Flowing Shapes & Shiny Buttons -->
    <link href="<%= ctx %>/css/style.css" rel="stylesheet">
</head>
<body>

<!-- Flowing Animated Background Shapes -->
<div class="flowing-bg">
    <div class="shape-blob blob-1"></div>
    <div class="shape-blob blob-2"></div>
</div>

<!-- Top Navigation Bar -->
<nav class="navbar navbar-expand-lg navbar-custom">
    <div class="container-fluid">
        <a class="navbar-brand" href="<%= currentUser != null ? servlet.LoginServlet.getDashboardUrl(currentUser.getRole(), ctx) : ctx + "/index.jsp" %>">
            <div class="school-badge">AT</div>
            <span><%= I18nUtil.getMessage(currentLocale, "app.name") %></span>
        </a>

        <button class="navbar-toggler text-white border-0" type="button" data-bs-toggle="collapse" data-bs-target="#topNavbar">
            <i class="bi bi-list fs-2 text-white"></i>
        </button>

        <div class="collapse navbar-collapse" id="topNavbar">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <% if (currentUser != null) { %>
                    <li class="nav-item">
                        <a class="nav-link-custom" href="<%= servlet.LoginServlet.getDashboardUrl(currentUser.getRole(), ctx) %>">
                            <i class="bi bi-speedometer2"></i> <%= I18nUtil.getMessage(currentLocale, "nav.dashboard") %>
                        </a>
                    </li>

                    <% if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.REGISTRAR || currentUser.getRole() == Role.PRINCIPAL) { %>
                        <li class="nav-item">
                            <a class="nav-link-custom" href="<%= ctx %>/students">
                                <i class="bi bi-people"></i> <%= I18nUtil.getMessage(currentLocale, "nav.students") %>
                            </a>
                        </li>
                    <% } %>

                    <% if (currentUser.getRole() == Role.ADMIN) { %>
                        <li class="nav-item">
                            <a class="nav-link-custom" href="<%= ctx %>/admin/teachers">
                                <i class="bi bi-person-badge"></i> <%= I18nUtil.getMessage(currentLocale, "nav.teachers") %>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link-custom" href="<%= ctx %>/admin/classes">
                                <i class="bi bi-diagram-3"></i> <%= I18nUtil.getMessage(currentLocale, "nav.classes") %>
                            </a>
                        </li>
                    <% } %>

                    <% if (currentUser.getRole() == Role.TEACHER || currentUser.getRole() == Role.ATTENDANCE_OFFICER || currentUser.getRole() == Role.ADMIN) { %>
                        <li class="nav-item">
                            <a class="nav-link-custom" href="<%= ctx %>/attendance">
                                <i class="bi bi-calendar-check"></i> <%= I18nUtil.getMessage(currentLocale, "nav.attendance") %>
                            </a>
                        </li>
                    <% } %>

                    <% if (currentUser.getRole() == Role.TEACHER || currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.PRINCIPAL) { %>
                        <li class="nav-item">
                            <a class="nav-link-custom" href="<%= ctx %>/assessments">
                                <i class="bi bi-pencil-square"></i> <%= I18nUtil.getMessage(currentLocale, "nav.assessments") %>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link-custom" href="<%= ctx %>/reports">
                                <i class="bi bi-file-earmark-text"></i> <%= I18nUtil.getMessage(currentLocale, "nav.report_cards") %>
                            </a>
                        </li>
                    <% } %>

                    <li class="nav-item">
                        <a class="nav-link-custom" href="<%= ctx %>/timetable">
                            <i class="bi bi-clock-history"></i> <%= I18nUtil.getMessage(currentLocale, "nav.timetables") %>
                        </a>
                    </li>
                <% } else { %>
                    <li class="nav-item">
                        <a class="nav-link-custom" href="<%= ctx %>/index.jsp">
                            <i class="bi bi-house"></i> Home
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link-custom" href="<%= ctx %>/show_all">
                            <i class="bi bi-people"></i> <%= I18nUtil.getMessage(currentLocale, "nav.students") %>
                        </a>
                    </li>
                <% } %>
            </ul>

            <!-- Right side: Language selector & Profile/Login -->
            <div class="d-flex align-items-center gap-3">
                <!-- Bilingual Toggle -->
                <div class="lang-selector">
                    <a href="?lang=en" class="lang-btn <%= "en".equals(currentLocale) ? "active" : "" %>">EN</a>
                    <a href="?lang=am" class="lang-btn <%= "am".equals(currentLocale) ? "active" : "" %>">አማ</a>
                </div>

                <% if (currentUser != null) { %>
                    <div class="dropdown">
                        <button class="btn btn-sm btn-outline-light dropdown-toggle d-flex align-items-center gap-2" type="button" data-bs-toggle="dropdown">
                            <i class="bi bi-person-circle"></i>
                            <span><%= currentUser.getFullName() %></span>
                            <span class="badge bg-warning text-dark"><%= currentUser.getRole().getLocalizedLabel(currentLocale) %></span>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end shadow">
                            <li><h6 class="dropdown-header"><%= currentUser.getEmail() != null ? currentUser.getEmail() : currentUser.getUsername() %></h6></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" href="<%= ctx %>/logout"><i class="bi bi-box-arrow-right"></i> <%= I18nUtil.getMessage(currentLocale, "auth.logout") %></a></li>
                        </ul>
                    </div>
                <% } else { %>
                    <a href="<%= ctx %>/login" class="btn btn-sm btn-shine btn-shine-gold">
                        <i class="bi bi-box-arrow-in-right"></i> <%= I18nUtil.getMessage(currentLocale, "auth.login") %>
                    </a>
                <% } %>
            </div>
        </div>
    </div>
</nav>

<main class="app-container">
