package servlet;

import dao.SchoolOperationsDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.AuditLog;
import model.Role;
import model.User;
import util.PasswordUtil;

import java.io.IOException;

/**
 * Controller handling user authentication, session fixation protection,
 * and role-specific dashboard redirection.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final SchoolOperationsDAO schoolOpsDAO = new SchoolOperationsDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            User user = (User) session.getAttribute("currentUser");
            response.sendRedirect(getDashboardUrl(user.getRole(), request.getContextPath()));
            return;
        }

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String redirect = request.getParameter("redirect");

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "auth.invalid_credentials");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        User user = userDAO.findByUsername(username.trim());
        if (user != null && PasswordUtil.verifyPassword(password, user.getPasswordHash(), user.getSalt())) {
            // Protect against Session Fixation: invalidate existing session & create fresh
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession session = request.getSession(true);
            session.setAttribute("currentUser", user);
            session.setAttribute("currentLocale", user.getPreferredLocale() != null ? user.getPreferredLocale() : "en");

            // Audit login
            schoolOpsDAO.logAudit(new AuditLog(user.getId(), "LOGIN", "USER", String.valueOf(user.getId()),
                    "Successful authentication for user: " + user.getUsername(), request.getRemoteAddr()));

            if (redirect != null && !redirect.trim().isEmpty() && !redirect.contains("/login") && !redirect.contains("/logout")) {
                response.sendRedirect(redirect);
            } else {
                response.sendRedirect(getDashboardUrl(user.getRole(), request.getContextPath()));
            }
        } else {
            // Audit failed attempt
            schoolOpsDAO.logAudit(new AuditLog(null, "LOGIN_FAILED", "USER", username,
                    "Failed login attempt for username: " + username, request.getRemoteAddr()));

            request.setAttribute("errorMessage", "auth.invalid_credentials");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    public static String getDashboardUrl(Role role, String contextPath) {
        if (role == null) return contextPath + "/login";
        switch (role) {
            case ADMIN:
                return contextPath + "/admin/dashboard";
            case PRINCIPAL:
                return contextPath + "/principal/dashboard";
            case REGISTRAR:
                return contextPath + "/registrar/dashboard";
            case TEACHER:
                return contextPath + "/teacher/dashboard";
            case ATTENDANCE_OFFICER:
                return contextPath + "/attendance/dashboard";
            case STUDENT:
                return contextPath + "/student/portal";
            default:
                return contextPath + "/index.jsp";
        }
    }
}
