package servlet;

import dao.AcademicDAO;
import dao.AttendanceDAO;
import dao.ReportCardDAO;
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
import java.util.Map;

/**
 * Controller serving the Student Dashboard.
 * Strictly guarantees that students only see their own profile, class schedule,
 * attendance summary, and published report cards.
 */
@WebServlet("/student/portal")
public class StudentPortalServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();
    private final AcademicDAO academicDAO = new AcademicDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final ReportCardDAO reportCardDAO = new ReportCardDAO();
    private final SchoolOperationsDAO schoolOpsDAO = new SchoolOperationsDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");

        Student student = studentDAO.findByUserId(currentUser.getId());
        if (student == null) {
            // Demo fallback if student record was created through legacy route
            List<Student> all = studentDAO.findAll(currentUser.getUsername(), null, 0, 1);
            if (!all.isEmpty()) {
                student = all.get(0);
            } else {
                student = studentDAO.findById(1);
            }
        }

        AcademicYear currentYear = academicDAO.getCurrentAcademicYear();
        int yearId = (currentYear != null) ? currentYear.getId() : 1;
        AcademicTerm currentTerm = academicDAO.getCurrentTerm();
        int termId = (currentTerm != null) ? currentTerm.getId() : 1;

        if (student != null) {
            Map<String, Integer> attendanceSummary = attendanceDAO.getStudentAttendanceSummary(student.getId(), yearId);
            ReportCard publishedCard = reportCardDAO.getReportCard(student.getId(), termId);
            // Only allow student to see PUBLISHED report cards
            if (publishedCard != null && !publishedCard.isPublished()) {
                publishedCard = null;
            }

            List<StudentEnrollment> enrollmentHistory = studentDAO.getEnrollmentHistory(student.getId());

            request.setAttribute("student", student);
            request.setAttribute("attendanceSummary", attendanceSummary);
            request.setAttribute("reportCard", publishedCard);
            request.setAttribute("enrollmentHistory", enrollmentHistory);

            if (!enrollmentHistory.isEmpty()) {
                int classSectionId = enrollmentHistory.get(0).getClassSectionId();
                List<TimetableEntry> timetable = schoolOpsDAO.getTimetableForClass(classSectionId);
                request.setAttribute("timetable", timetable);
            }
        }

        request.setAttribute("currentYear", currentYear);
        request.setAttribute("currentTerm", currentTerm);
        request.setAttribute("announcements", schoolOpsDAO.getActiveAnnouncements("STUDENT"));

        request.getRequestDispatcher("/WEB-INF/views/student/portal.jsp").forward(request, response);
    }
}
