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

        String cleanUsername = username.trim();
        String cleanPassword = password.trim();

        User user = null;
        boolean dbError = false;
        try {
            user = userDAO.findByUsername(cleanUsername);
        } catch (Exception e) {
            dbError = true;
            System.err.println("[LoginServlet] Database exception during login for '" + cleanUsername + "': " + e.getMessage());
            e.printStackTrace();
        }

        if (user == null && !dbError) {
            // Check if DB is reachable
            try (java.sql.Connection conn = util.DBConnection.getConnection()) {
                // Connection alive, user genuinely not found
            } catch (Exception ex) {
                dbError = true;
                System.err.println("[LoginServlet] Database is unreachable: " + ex.getMessage());
            }
        }

        if (dbError) {
            request.setAttribute("errorMessageText", "Database connection is currently unavailable. Please ensure your cloud database is running and try again.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        boolean passwordMatch = (user != null) && PasswordUtil.verifyPassword(cleanPassword, user.getPasswordHash(), user.getSalt());
        System.out.println("[LoginServlet] Authentication attempt for '" + cleanUsername + "' -> User found: " + (user != null) + ", Password match: " + passwordMatch);

        if (passwordMatch) {
            // Protect against Session Fixation: invalidate existing session & create fresh
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession session = request.getSession(true);
            session.setAttribute("currentUser", user);
            session.setAttribute("currentLocale", user.getPreferredLocale() != null ? user.getPreferredLocale() : "en");

            // Audit login
            try {
                schoolOpsDAO.logAudit(new AuditLog(user.getId(), "LOGIN", "USER", String.valueOf(user.getId()),
                        "Successful authentication for user: " + user.getUsername(), request.getRemoteAddr()));
            } catch (Exception ignored) {}

            if (redirect != null && !redirect.trim().isEmpty() && !redirect.contains("/login") && !redirect.contains("/logout")) {
                response.sendRedirect(redirect);
            } else {
                response.sendRedirect(getDashboardUrl(user.getRole(), request.getContextPath()));
            }
        } else {
            // Audit failed attempt
            try {
                schoolOpsDAO.logAudit(new AuditLog(null, "LOGIN_FAILED", "USER", cleanUsername,
                        "Failed login attempt for username: " + cleanUsername, request.getRemoteAddr()));
            } catch (Exception ignored) {}

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
