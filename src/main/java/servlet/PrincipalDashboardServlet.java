package servlet;

import dao.AcademicDAO;
import dao.AttendanceDAO;
import dao.ReportCardDAO;
import dao.SchoolOperationsDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AcademicTerm;
import model.AcademicYear;
import model.ClassSection;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * Controller serving Principal, Registrar, and Attendance Officer dashboards.
 */
@WebServlet({"/principal/dashboard", "/registrar/dashboard", "/attendance/dashboard"})
public class PrincipalDashboardServlet extends HttpServlet {

    private final SchoolOperationsDAO schoolOpsDAO = new SchoolOperationsDAO();
    private final AcademicDAO academicDAO = new AcademicDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final ReportCardDAO reportCardDAO = new ReportCardDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        AcademicYear currentYear = academicDAO.getCurrentAcademicYear();
        int yearId = (currentYear != null) ? currentYear.getId() : 1;
        AcademicTerm currentTerm = academicDAO.getCurrentTerm();
        int termId = (currentTerm != null) ? currentTerm.getId() : 1;

        Map<String, Object> stats = schoolOpsDAO.getSchoolStats();
        Date today = new Date(System.currentTimeMillis());
        Map<String, Integer> todayAttendance = attendanceDAO.getTodayClassOverview(yearId, today);
        List<ClassSection> sections = academicDAO.getClassSections(yearId, null);

        request.setAttribute("currentYear", currentYear);
        request.setAttribute("currentTerm", currentTerm);
        request.setAttribute("stats", stats);
        request.setAttribute("todayAttendance", todayAttendance);
        request.setAttribute("sections", sections);
        request.setAttribute("announcements", schoolOpsDAO.getActiveAnnouncements("STAFF"));

        if (path.contains("registrar")) {
            request.getRequestDispatcher("/WEB-INF/views/registrar/dashboard.jsp").forward(request, response);
        } else if (path.contains("attendance")) {
            request.getRequestDispatcher("/WEB-INF/views/attendance/dashboard.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/WEB-INF/views/principal/dashboard.jsp").forward(request, response);
        }
    }
}
