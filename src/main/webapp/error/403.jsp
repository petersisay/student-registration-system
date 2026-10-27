<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ page import="util.I18nUtil" %>
<%
    String currentLocale = (String) session.getAttribute("currentLocale");
    if (currentLocale == null) currentLocale = "en";
    String ctx = request.getContextPath();
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="card-modern text-center py-5">
    <div class="mb-3">
        <i class="bi bi-shield-lock-fill text-danger" style="font-size: 4rem;"></i>
    </div>
    <h2 class="fw-bold text-navy" style="color: var(--primary-navy);"><%= I18nUtil.getMessage(currentLocale, "auth.access_denied") %> (HTTP 403)</h2>
    <p class="text-muted col-md-6 mx-auto mb-4">
        <%= I18nUtil.getMessage(currentLocale, "auth.forbidden_message") %>
    </p>
    <div>
        <a href="<%= ctx %>/login" class="btn btn-shine btn-shine-primary">
            <i class="bi bi-box-arrow-in-right"></i> <%= I18nUtil.getMessage(currentLocale, "auth.login") %>
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
