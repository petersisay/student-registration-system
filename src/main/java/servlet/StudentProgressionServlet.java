package servlet;

import dao.AcademicDAO;
import dao.SchoolOperationsDAO;
import dao.StudentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.*;

import java.io.IOException;
import java.util.List;

/**
 * Controller managing academic grade progression across school years.
 * Strictly preserves historical enrollment, marks, and attendance without overwriting.
 */
@WebServlet("/students/progress")
public class StudentProgressionServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();
    private final AcademicDAO academicDAO = new AcademicDAO();
    private final SchoolOperationsDAO schoolOpsDAO = new SchoolOperationsDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<AcademicYear> allYears = academicDAO.getAllAcademicYears();
        List<GradeLevel> gradeLevels = academicDAO.getAllGradeLevels();

        String classIdStr = request.getParameter("classId");
        if (classIdStr != null && !classIdStr.trim().isEmpty()) {
            int classId = Integer.parseInt(classIdStr.trim());
            ClassSection section = academicDAO.getClassSectionById(classId);
            List<Student> students = studentDAO.findEnrolledByClass(classId);

            request.setAttribute("selectedSection", section);
            request.setAttribute("enrolledStudents", students);
        }

        AcademicYear currentYear = academicDAO.getCurrentAcademicYear();
        int yearId = (currentYear != null) ? currentYear.getId() : 1;
        List<ClassSection> sections = academicDAO.getClassSections(yearId, null);

        request.setAttribute("academicYears", allYears);
        request.setAttribute("gradeLevels", gradeLevels);
        request.setAttribute("classSections", sections);
        request.setAttribute("currentYear", currentYear);

        request.getRequestDispatcher("/WEB-INF/views/student/progression.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");

        String studentIdStr = request.getParameter("studentId");
        String currentYearStr = request.getParameter("currentYearId");
        String nextYearStr = request.getParameter("nextYearId");
        String nextGradeStr = request.getParameter("nextGradeId");
        String nextSectionStr = request.getParameter("nextSectionId");

        if (studentIdStr != null && currentYearStr != null && nextYearStr != null && nextGradeStr != null && nextSectionStr != null) {
            int studentId = Integer.parseInt(studentIdStr.trim());
            int currentYearId = Integer.parseInt(currentYearStr.trim());
            int nextYearId = Integer.parseInt(nextYearStr.trim());
            int nextGradeId = Integer.parseInt(nextGradeStr.trim());
            int nextSectionId = Integer.parseInt(nextSectionStr.trim());

            boolean success = studentDAO.promoteStudent(studentId, currentYearId, nextYearId, nextGradeId, nextSectionId);
            if (success) {
                schoolOpsDAO.logAudit(new AuditLog(currentUser.getId(), "PROMOTED_STUDENT", "STUDENT", String.valueOf(studentId),
                        "Promoted student " + studentId + " to Grade " + nextGradeId + " for academic year " + nextYearId, request.getRemoteAddr()));
            }
        }

        response.sendRedirect(request.getContextPath() + "/students/progress?msg=promoted");
    }
}
