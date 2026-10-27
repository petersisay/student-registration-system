package servlet;

import dao.SchoolOperationsDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.AuditLog;
import model.User;

import java.io.IOException;

/**
 * Controller handling user logout and session termination.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private final SchoolOperationsDAO schoolOpsDAO = new SchoolOperationsDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("currentUser");
            if (user != null) {
                schoolOpsDAO.logAudit(new AuditLog(user.getId(), "LOGOUT", "USER", String.valueOf(user.getId()),
                        "User logged out: " + user.getUsername(), request.getRemoteAddr()));
            }
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login?logout=true");
    }
}
