package servlet;

import dao.AcademicDAO;
import dao.AttendanceDAO;
import dao.SchoolOperationsDAO;
import dao.StudentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AcademicYear;
import model.Student;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * Controller serving the School Administrator Dashboard.
 * Displays real-time database-driven school statistics, grade distributions,
 * daily attendance breakdown, recent registrations, and audit logs.
 */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final SchoolOperationsDAO schoolOpsDAO = new SchoolOperationsDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final AcademicDAO academicDAO = new AcademicDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        AcademicYear currentYear = academicDAO.getCurrentAcademicYear();
        int yearId = (currentYear != null) ? currentYear.getId() : 1;

        Map<String, Object> stats = schoolOpsDAO.getSchoolStats();
        Date today = new Date(System.currentTimeMillis());
        Map<String, Integer> todayAttendance = attendanceDAO.getTodayClassOverview(yearId, today);

        List<Student> recentStudents = studentDAO.findAll(null, null, 0, 5);

        request.setAttribute("currentYear", currentYear);
        request.setAttribute("stats", stats);
        request.setAttribute("todayAttendance", todayAttendance);
        request.setAttribute("recentStudents", recentStudents);
        request.setAttribute("auditLogs", schoolOpsDAO.getRecentAuditLogs(6));
        request.setAttribute("announcements", schoolOpsDAO.getActiveAnnouncements("ALL"));

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }
}
