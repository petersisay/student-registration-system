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
    <link href="<%= ctx %>/css/style.css?v=3.2" rel="stylesheet">
</head>
<body>

<!-- Flowing Animated Background Shapes -->
<div class="flowing-bg">
    <div class="shape-blob blob-1"></div>
    <div class="shape-blob blob-2"></div>
</div>

<!-- Top Navigation Bar -->
<nav class="navbar navbar-expand-xl navbar-custom">
    <div class="container-fluid">
        <a class="navbar-brand text-truncate" href="<%= currentUser != null ? servlet.LoginServlet.getDashboardUrl(currentUser.getRole(), ctx) : ctx + "/index.jsp" %>">
            <img src="<%= ctx %>/images/bule-hora-logo.png" alt="Bule Hora Logo" class="school-logo-img flex-shrink-0">
            <span class="d-none d-xxl-inline"><%= I18nUtil.getMessage(currentLocale, "app.name") %></span>
            <span class="d-none d-sm-inline d-xxl-none">Bule Hora Elementary</span>
            <span class="d-inline d-sm-none">Bule Hora</span>
        </a>

        <button class="navbar-toggler text-white border-0" type="button" data-bs-toggle="collapse" data-bs-target="#topNavbar">
            <i class="bi bi-list fs-2 text-white"></i>
        </button>

        <div class="collapse navbar-collapse" id="topNavbar">
            <ul class="navbar-nav me-auto mb-2 mb-xl-0 align-items-xl-center">
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

                    <% if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.PRINCIPAL || currentUser.getRole() == Role.TEACHER) { %>
                        <li class="nav-item dropdown">
                            <a class="nav-link-custom dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="bi bi-journal-bookmark-fill"></i> <%= I18nUtil.getMessage(currentLocale, "nav.academics") %>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-dark shadow">
                                <li>
                                    <a class="dropdown-item" href="<%= ctx %>/attendance">
                                        <i class="bi bi-calendar-check me-2 text-info"></i> <%= I18nUtil.getMessage(currentLocale, "nav.attendance") %>
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item" href="<%= ctx %>/assessments">
                                        <i class="bi bi-pencil-square me-2 text-warning"></i> <%= I18nUtil.getMessage(currentLocale, "nav.assessments") %>
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item" href="<%= ctx %>/reports">
                                        <i class="bi bi-file-earmark-text me-2 text-success"></i> <%= I18nUtil.getMessage(currentLocale, "nav.report_cards") %>
                                    </a>
                                </li>
                                <li><hr class="dropdown-divider border-secondary"></li>
                                <li>
                                    <a class="dropdown-item" href="<%= ctx %>/timetable">
                                        <i class="bi bi-clock-history me-2 text-primary"></i> <%= I18nUtil.getMessage(currentLocale, "nav.timetables") %>
                                    </a>
                                </li>
                            </ul>
                        </li>
                    <% } else if (currentUser.getRole() == Role.ATTENDANCE_OFFICER) { %>
                        <li class="nav-item">
                            <a class="nav-link-custom" href="<%= ctx %>/attendance">
                                <i class="bi bi-calendar-check"></i> <%= I18nUtil.getMessage(currentLocale, "nav.attendance") %>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link-custom" href="<%= ctx %>/timetable">
                                <i class="bi bi-clock-history"></i> <%= I18nUtil.getMessage(currentLocale, "nav.timetables") %>
                            </a>
                        </li>
                    <% } else { %>
                        <li class="nav-item">
                            <a class="nav-link-custom" href="<%= ctx %>/timetable">
                                <i class="bi bi-clock-history"></i> <%= I18nUtil.getMessage(currentLocale, "nav.timetables") %>
                            </a>
                        </li>
                    <% } %>
                <% } else { %>
                    <li class="nav-item">
                        <a class="nav-link-custom" href="<%= ctx %>/index.jsp">
                            <i class="bi bi-house"></i> Home
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link-custom" href="<%= ctx %>/students">
                            <i class="bi bi-people"></i> <%= I18nUtil.getMessage(currentLocale, "nav.students") %>
                        </a>
                    </li>
                <% } %>
            </ul>

            <!-- Right side: Language selector & Profile/Login -->
            <div class="d-flex align-items-center gap-2 gap-md-3 ms-auto ms-xl-2">
                <!-- Bilingual Toggle -->
                <div class="lang-selector">
                    <a href="?lang=en" class="lang-btn <%= "en".equals(currentLocale) ? "active" : "" %>">EN</a>
                    <a href="?lang=am" class="lang-btn <%= "am".equals(currentLocale) ? "active" : "" %>">አማ</a>
                </div>

                <% if (currentUser != null) { %>
                    <div class="dropdown">
                        <button class="btn btn-sm btn-outline-light dropdown-toggle d-flex align-items-center gap-1 gap-md-2" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="bi bi-person-circle flex-shrink-0"></i>
                            <span class="user-name-truncated text-truncate"><%= currentUser.getUsername() %></span>
                            <span class="badge bg-warning text-dark flex-shrink-0"><%= currentUser.getRole().name() %></span>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-dark dropdown-menu-end shadow">
                            <li><h6 class="dropdown-header text-white fw-bold"><%= currentUser.getFullName() %></h6></li>
                            <li><div class="px-3 py-1 small text-white-50"><%= currentUser.getEmail() != null ? currentUser.getEmail() : currentUser.getUsername() %> &bull; <span class="badge bg-warning text-dark"><%= currentUser.getRole().getLocalizedLabel(currentLocale) %></span></div></li>
                            <li><hr class="dropdown-divider border-secondary"></li>
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
