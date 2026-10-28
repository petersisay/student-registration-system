package servlet;

import dao.TeacherDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Role;
import model.Teacher;
import model.User;
import util.PasswordUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Controller for comprehensive User Account Management by System Administrator.
 * Allows provisioning, editing, and resetting passwords for all roles:
 * Teachers, Registrars, Principals, Attendance Officers, Students, and Administrators.
 */
@WebServlet(urlPatterns = {"/admin/users", "/admin/users/*"})
public class AdminUserServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
            resp.sendRedirect(req.getContextPath() + "/login?error=unauthorized");
            return;
        }

        String search = req.getParameter("search");
        String roleFilter = req.getParameter("role");

        List<User> users = userDAO.findAllUsers(search, roleFilter);
        int totalCount = userDAO.countAllUsers();

        req.setAttribute("users", users);
        req.setAttribute("totalUsers", totalCount);
        req.setAttribute("allRoles", Role.values());
        req.setAttribute("search", search);
        req.setAttribute("selectedRole", roleFilter);

        req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
            resp.sendRedirect(req.getContextPath() + "/login?error=unauthorized");
            return;
        }

        String action = req.getParameter("action");

        if ("createUser".equals(action)) {
            String fullName = req.getParameter("fullName");
            String roleStr = req.getParameter("role");
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            String email = req.getParameter("email");
            String phone = req.getParameter("phone");
            String preferredLocale = req.getParameter("preferredLocale");

            if (fullName == null || fullName.trim().isEmpty() ||
                roleStr == null || roleStr.trim().isEmpty() ||
                username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/admin/users?error=missing_fields");
                return;
            }

            username = username.trim().toLowerCase();
            if (userDAO.existsByUsername(username)) {
                resp.sendRedirect(req.getContextPath() + "/admin/users?error=username_taken");
                return;
            }

            Role role = Role.fromString(roleStr);
            if (role == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/users?error=invalid_role");
                return;
            }

            String salt = PasswordUtil.generateSalt();
            String hash = PasswordUtil.hashPassword(password.trim(), salt);

            User user = new User();
            user.setFullName(fullName.trim());
            user.setUsername(username);
            user.setRole(role);
            user.setPasswordHash(hash);
            user.setSalt(salt);
            user.setEmail(email != null && !email.trim().isEmpty() ? email.trim() : null);
            user.setPhone(phone != null && !phone.trim().isEmpty() ? phone.trim() : null);
            user.setPreferredLocale(preferredLocale != null && !preferredLocale.trim().isEmpty() ? preferredLocale : "en");
            user.setActive(true);

            int userId = userDAO.create(user);

            // If a Teacher user is created, ensure a corresponding teacher record exists for assignment
            if (userId > 0 && role == Role.TEACHER) {
                Teacher existingTeacher = teacherDAO.findByUserId(userId);
                if (existingTeacher == null) {
                    Teacher t = new Teacher();
                    t.setUserId(userId);
                    t.setEmployeeId("TCH-" + (System.currentTimeMillis() % 100000));
                    t.setFullNameEn(fullName.trim());
                    t.setEmail(user.getEmail());
                    t.setPhone(user.getPhone());
                    t.setStatus("ACTIVE");
                    teacherDAO.saveTeacher(t);
                }
            }

            resp.sendRedirect(req.getContextPath() + "/admin/users?success=created&user=" +
                    URLEncoder.encode(username, StandardCharsets.UTF_8));

        } else if ("updateUser".equals(action)) {
            String userIdStr = req.getParameter("userId");
            String fullName = req.getParameter("fullName");
            String roleStr = req.getParameter("role");
            String username = req.getParameter("username");
            String email = req.getParameter("email");
            String phone = req.getParameter("phone");
            boolean isActive = "true".equalsIgnoreCase(req.getParameter("isActive"));

            if (userIdStr == null || username == null || fullName == null || roleStr == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/users?error=invalid_request");
                return;
            }

            int userId = Integer.parseInt(userIdStr.trim());
            username = username.trim().toLowerCase();

            if (userDAO.existsByUsernameExcept(username, userId)) {
                resp.sendRedirect(req.getContextPath() + "/admin/users?error=username_taken");
                return;
            }

            Role role = Role.fromString(roleStr);
            if (role == null) role = Role.TEACHER;

            userDAO.updateUserAdmin(userId, username, fullName.trim(), role, email, phone, isActive);
            resp.sendRedirect(req.getContextPath() + "/admin/users?success=updated");

        } else if ("resetPassword".equals(action)) {
            String userIdStr = req.getParameter("userId");
            String newPassword = req.getParameter("newPassword");

            if (userIdStr != null && newPassword != null && !newPassword.trim().isEmpty()) {
                int userId = Integer.parseInt(userIdStr.trim());
                userDAO.resetUserPassword(userId, newPassword.trim());
                resp.sendRedirect(req.getContextPath() + "/admin/users?success=password_reset");
                return;
            }

            resp.sendRedirect(req.getContextPath() + "/admin/users?error=invalid_request");

        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
        }
    }
}
