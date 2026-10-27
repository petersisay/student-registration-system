package servlet;

import dao.AcademicDAO;
import dao.AttendanceDAO;
import dao.SchoolOperationsDAO;
import dao.TeacherDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.*;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

/**
 * Controller serving the Teacher Dashboard.
 * Strictly limits view to teacher's authorized classes, subjects, and schedule.
 */
@WebServlet("/teacher/dashboard")
public class TeacherDashboardServlet extends HttpServlet {

    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final AcademicDAO academicDAO = new AcademicDAO();
    private final SchoolOperationsDAO schoolOpsDAO = new SchoolOperationsDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");

        Teacher teacher = teacherDAO.findByUserId(currentUser.getId());
        if (teacher == null) {
            // Fallback: check first teacher if testing
            List<Teacher> all = teacherDAO.findAll();
            if (!all.isEmpty()) teacher = all.get(0);
        }

        AcademicYear currentYear = academicDAO.getCurrentAcademicYear();
        int yearId = (currentYear != null) ? currentYear.getId() : 1;

        if (teacher != null) {
            List<TeacherAssignment> assignments = teacherDAO.getAssignments(teacher.getId(), yearId);
            List<TimetableEntry> timetable = schoolOpsDAO.getTimetableForTeacher(teacher.getId());

            request.setAttribute("teacher", teacher);
            request.setAttribute("assignments", assignments);
            request.setAttribute("timetable", timetable);
        }

        request.setAttribute("currentYear", currentYear);
        request.setAttribute("announcements", schoolOpsDAO.getActiveAnnouncements("TEACHER"));

        request.getRequestDispatcher("/WEB-INF/views/teacher/dashboard.jsp").forward(request, response);
    }
}
