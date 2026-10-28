package servlet;

import dao.AcademicDAO;
import dao.TeacherDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AcademicYear;
import model.ClassSection;
import model.Role;
import model.Subject;
import model.Teacher;
import model.TeacherAssignment;
import model.User;
import util.PasswordUtil;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/teachers", "/admin/teachers/*"})
public class AdminTeacherServlet extends HttpServlet {
    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final AcademicDAO academicDAO = new AcademicDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        AcademicYear activeYear = academicDAO.getActiveAcademicYear();
        int activeYearId = (activeYear != null) ? activeYear.getId() : 1;

        if (pathInfo != null && pathInfo.equals("/assign")) {
            String teacherIdStr = req.getParameter("teacherId");
            if (teacherIdStr != null && !teacherIdStr.isEmpty()) {
                int teacherId = Integer.parseInt(teacherIdStr);
                Teacher teacher = teacherDAO.getTeacherById(teacherId);
                List<TeacherAssignment> assignments = teacherDAO.getTeacherAssignments(teacherId, activeYearId);
                List<ClassSection> sections = academicDAO.getAllClassSections();
                List<Subject> subjects = academicDAO.getAllSubjects();

                req.setAttribute("teacher", teacher);
                req.setAttribute("assignments", assignments);
                req.setAttribute("sections", sections);
                req.setAttribute("subjects", subjects);
                req.setAttribute("activeYear", activeYear);
            }
        }

        List<Teacher> teachers = teacherDAO.getAllTeachers();
        for (Teacher t : teachers) {
            if (t.getUserId() != null && t.getUserId() > 0) {
                User u = userDAO.findById(t.getUserId());
                if (u != null) {
                    t.setUsername(u.getUsername());
                }
            }
        }
        req.setAttribute("teachers", teachers);
        req.setAttribute("activeYear", activeYear);
        req.getRequestDispatcher("/WEB-INF/views/admin/teachers.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        AcademicYear activeYear = academicDAO.getActiveAcademicYear();
        int activeYearId = (activeYear != null) ? activeYear.getId() : 1;

        if ("createTeacher".equals(action)) {
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String nameAmharic = req.getParameter("nameAmharic");
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            String email = req.getParameter("email");
            String phone = req.getParameter("phone");
            String specialization = req.getParameter("specialization");
            String qualification = req.getParameter("qualification");

            // Derive username if not provided
            if (username == null || username.trim().isEmpty()) {
                if (email != null && !email.trim().isEmpty() && email.contains("@")) {
                    username = email.substring(0, email.indexOf('@')).toLowerCase().replaceAll("[^a-z0-9._-]", "");
                } else {
                    username = ("teacher." + (lastName != null ? lastName : firstName)).toLowerCase().replaceAll("[^a-z0-9._-]", "");
                }
            }
            username = username.trim().toLowerCase();

            // Check if username is already taken
            if (userDAO.existsByUsername(username)) {
                resp.sendRedirect(req.getContextPath() + "/admin/teachers?error=username_taken");
                return;
            }

            // Default password if empty
            if (password == null || password.trim().isEmpty()) {
                password = "Teacher@" + (System.currentTimeMillis() % 1000);
            }

            // Create User Account with Admin-assigned Credentials
            User newUser = new User();
            newUser.setUsername(username);
            String salt = PasswordUtil.generateSalt();
            String hash = PasswordUtil.hashPassword(password.trim(), salt);
            newUser.setPasswordHash(hash);
            newUser.setSalt(salt);
            newUser.setFullName(firstName.trim() + " " + (lastName != null ? lastName.trim() : ""));
            newUser.setRole(Role.TEACHER);
            newUser.setEmail(email != null && !email.trim().isEmpty() ? email.trim() : null);
            newUser.setPhone(phone != null && !phone.trim().isEmpty() ? phone.trim() : null);
            newUser.setActive(true);
            int userId = userDAO.create(newUser);

            Teacher teacher = new Teacher();
            if (userId > 0) {
                teacher.setUserId(userId);
            }
            teacher.setEmployeeId("TCH-" + (System.currentTimeMillis() % 100000));
            teacher.setFullNameEn(firstName.trim() + " " + (lastName != null ? lastName.trim() : ""));
            teacher.setFullNameAm(nameAmharic != null ? nameAmharic.trim() : "");
            teacher.setEmail(email);
            teacher.setPhone(phone);
            teacher.setSpecialization(specialization);
            teacher.setQualification(qualification);
            teacher.setStatus("ACTIVE");

            teacherDAO.saveTeacher(teacher);
            resp.sendRedirect(req.getContextPath() + "/admin/teachers?success=created&user=" + username);
        } else if ("resetPassword".equals(action)) {
            String teacherIdStr = req.getParameter("teacherId");
            String newPassword = req.getParameter("newPassword");
            if (teacherIdStr != null && newPassword != null && !newPassword.trim().isEmpty()) {
                int teacherId = Integer.parseInt(teacherIdStr);
                Teacher t = teacherDAO.getTeacherById(teacherId);
                if (t != null && t.getUserId() != null && t.getUserId() > 0) {
                    userDAO.resetUserPassword(t.getUserId(), newPassword.trim());
                    resp.sendRedirect(req.getContextPath() + "/admin/teachers?success=password_reset");
                    return;
                }
            }
            resp.sendRedirect(req.getContextPath() + "/admin/teachers?error=invalid_request");
        } else if ("assignClass".equals(action)) {
            int teacherId = Integer.parseInt(req.getParameter("teacherId"));
            int classSectionId = Integer.parseInt(req.getParameter("classSectionId"));
            int subjectId = Integer.parseInt(req.getParameter("subjectId"));
            boolean isHomeroom = req.getParameter("isHomeroom") != null;

            teacherDAO.assignTeacher(teacherId, classSectionId, subjectId, activeYearId, isHomeroom);
            resp.sendRedirect(req.getContextPath() + "/admin/teachers/assign?teacherId=" + teacherId + "&success=assigned");
        } else if ("deleteAssignment".equals(action)) {
            int assignmentId = Integer.parseInt(req.getParameter("assignmentId"));
            int teacherId = Integer.parseInt(req.getParameter("teacherId"));
            teacherDAO.deleteAssignment(assignmentId);
            resp.sendRedirect(req.getContextPath() + "/admin/teachers/assign?teacherId=" + teacherId + "&success=deleted");
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/teachers");
        }
    }
}
