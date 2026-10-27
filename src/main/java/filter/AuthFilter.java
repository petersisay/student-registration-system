package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Role;
import model.User;

import java.io.IOException;

/**
 * Server-Side Role-Based Access Control (RBAC) Filter.
 * Strictly verifies authentication status and role permissions before granting access
 * to protected servlet controllers and views.
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getServletPath();

        // 1. Allow public static assets and public landing/login pages
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Check authenticated session
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            // Not authenticated -> redirect to login with return URL
            res.sendRedirect(req.getContextPath() + "/login?redirect=" + req.getRequestURI());
            return;
        }

        Role role = currentUser.getRole();
        if (role == null) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "User has no assigned authorization role");
            return;
        }

        // 3. Strict Server-Side RBAC Enforcement
        if (path.equals("/admin") || path.startsWith("/admin/")) {
            if (role != Role.ADMIN) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Administrator privileges required");
                return;
            }
        } else if (path.equals("/principal") || path.startsWith("/principal/")) {
            if (role != Role.PRINCIPAL && role != Role.ADMIN) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Principal privileges required");
                return;
            }
        } else if (path.equals("/registrar") || path.startsWith("/registrar/")) {
            if (role != Role.REGISTRAR && role != Role.ADMIN) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Registrar privileges required");
                return;
            }
        } else if (path.equals("/teacher") || path.startsWith("/teacher/")) {
            if (role != Role.TEACHER && role != Role.ADMIN) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Teacher privileges required");
                return;
            }
        } else if (path.equals("/attendance") || path.startsWith("/attendance/")) {
            if (role != Role.ATTENDANCE_OFFICER && role != Role.TEACHER && role != Role.ADMIN && role != Role.PRINCIPAL) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Staff attendance privileges required");
                return;
            }
        } else if (path.equals("/assessments") || path.startsWith("/assessments/")) {
            if (role != Role.TEACHER && role != Role.ADMIN && role != Role.PRINCIPAL) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Faculty grading privileges required");
                return;
            }
        } else if (path.equals("/reports") || path.startsWith("/reports/")) {
            if (role != Role.TEACHER && role != Role.ADMIN && role != Role.PRINCIPAL) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Report card privileges required");
                return;
            }
        } else if (path.equals("/students") || path.startsWith("/students/")) {
            if (role == Role.STUDENT) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Student directory is restricted to staff");
                return;
            }
        } else if (path.equals("/student") || path.startsWith("/student/")) {
            if (role != Role.STUDENT && role != Role.ADMIN) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Student access only");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        if (path == null || path.isEmpty() || path.equals("/")) return true;
        return path.equals("/login") ||
               path.equals("/logout") ||
               path.equals("/register") || // Legacy public register
               path.equals("/show_all") || // Legacy student table
               path.equals("/index.jsp") ||
               path.equals("/students.jsp") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/") ||
               path.startsWith("/fonts/") ||
               path.equals("/error/403.jsp") ||
               path.equals("/error/404.jsp") ||
               path.equals("/error/500.jsp");
    }
}
