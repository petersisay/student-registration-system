<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="util.I18nUtil" %>
<%
    String currentLocale = (String) session.getAttribute("currentLocale");
    if (currentLocale == null) currentLocale = "en";
    String ctx = request.getContextPath();
%>
</main>

<footer class="text-center py-4 text-muted border-top mt-5 no-print" style="background: #FFFFFF;">
    <div class="container">
        <p class="mb-1 fw-medium"><%= I18nUtil.getMessage(currentLocale, "app.name") %></p>
        <small class="text-secondary"><%= I18nUtil.getMessage(currentLocale, "app.footer") %></small>
    </div>
</footer>

<!-- Bootstrap 5.3 Bundle JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<!-- Application Micro-animations & Interactive Scripts -->
<script src="<%= ctx %>/js/app.js?v=3.2"></script>
</body>
</html>
