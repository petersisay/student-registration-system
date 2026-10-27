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
            String email = req.getParameter("email");
            String phone = req.getParameter("phone");
            String specialization = req.getParameter("specialization");
            String qualification = req.getParameter("qualification");

            // Create user account for teacher if email is provided
            Integer userId = null;
            if (email != null && !email.trim().isEmpty()) {
                String username = email.trim().toLowerCase();
                User existing = userDAO.findByUsername(username);
                if (existing == null) {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setEmail(email.trim());
                    newUser.setRole(Role.TEACHER);
                    newUser.setFullName(firstName + " " + lastName);
                    String salt = PasswordUtil.generateSalt();
                    String hash = PasswordUtil.hashPassword("Teacher@123", salt);
                    newUser.setPasswordHash(hash);
                    newUser.setSalt(salt);
                    newUser.setActive(true);
                    int newId = userDAO.create(newUser);
                    if (newId > 0) userId = newId;
                } else {
                    userId = existing.getId();
                }
            }

            Teacher teacher = new Teacher();
            teacher.setUserId(userId);
            teacher.setEmployeeId("TCH-" + System.currentTimeMillis() % 10000);
            teacher.setFullNameEn(firstName + " " + lastName);
            teacher.setFullNameAm(nameAmharic);
            teacher.setEmail(email);
            teacher.setPhone(phone);
            teacher.setSpecialization(specialization);
            teacher.setQualification(qualification);
            teacher.setStatus("ACTIVE");

            teacherDAO.saveTeacher(teacher);
            resp.sendRedirect(req.getContextPath() + "/admin/teachers?success=created");
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
