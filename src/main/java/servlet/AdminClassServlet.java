package servlet;

import dao.AcademicDAO;
import dao.StudentDAO;
import dao.TeacherDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AcademicYear;
import model.ClassSection;
import model.GradeLevel;
import model.Student;
import model.TeacherAssignment;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/classes", "/admin/classes/*"})
public class AdminClassServlet extends HttpServlet {
    private final AcademicDAO academicDAO = new AcademicDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        AcademicYear activeYear = academicDAO.getActiveAcademicYear();
        int activeYearId = (activeYear != null) ? activeYear.getId() : 1;

        if (pathInfo != null && pathInfo.startsWith("/roster")) {
            String sectionIdStr = req.getParameter("sectionId");
            if (sectionIdStr != null && !sectionIdStr.isEmpty()) {
                int sectionId = Integer.parseInt(sectionIdStr);
                ClassSection section = academicDAO.getClassSectionById(sectionId);
                List<Student> roster = studentDAO.getStudentsByClass(sectionId, activeYearId);
                List<TeacherAssignment> assignments = teacherDAO.getAssignmentsForClass(sectionId, activeYearId);

                req.setAttribute("section", section);
                req.setAttribute("roster", roster);
                req.setAttribute("assignments", assignments);
                req.setAttribute("activeYear", activeYear);
            }
        }

        List<ClassSection> sections = academicDAO.getAllClassSections();
        List<GradeLevel> grades = academicDAO.getAllGradeLevels();
        req.setAttribute("sections", sections);
        req.setAttribute("grades", grades);
        req.setAttribute("activeYear", activeYear);

        req.getRequestDispatcher("/WEB-INF/views/admin/classes.jsp").forward(req, resp);
    }
}
