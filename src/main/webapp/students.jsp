<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // Backwards-compatibility redirect to the modern Ethiopian Elementary Student Directory
    response.sendRedirect(request.getContextPath() + "/students");
%>
