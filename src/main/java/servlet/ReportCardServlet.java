package servlet;

import dao.AcademicDAO;
import dao.ReportCardDAO;
import dao.StudentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.AcademicTerm;
import model.AcademicYear;
import model.ClassSection;
import model.ReportCard;
import model.Role;
import model.Student;
import model.StudentEnrollment;
import model.User;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/reports", "/reports/*"})
public class ReportCardServlet extends HttpServlet {
    private final ReportCardDAO reportCardDAO = new ReportCardDAO();
    private final AcademicDAO academicDAO = new AcademicDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        AcademicYear activeYear = academicDAO.getActiveAcademicYear();
        int activeYearId = (activeYear != null) ? activeYear.getId() : 1;
        AcademicTerm currentTerm = academicDAO.getCurrentTerm();
        int termId = (currentTerm != null) ? currentTerm.getId() : 1;

        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.equals("/view")) {
            int studentId = Integer.parseInt(req.getParameter("studentId"));

            // Student role check: student can only view their own report card
            if (currentUser.getRole() == Role.STUDENT) {
                Student currentStudent = studentDAO.findByUserId(currentUser.getId());
                if (currentStudent == null || currentStudent.getId() != studentId) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized: You can only view your own report card.");
                    return;
                }
            }

            ReportCard reportCard = reportCardDAO.getReportCard(studentId, termId);
            Student student = studentDAO.findById(studentId);

            req.setAttribute("reportCard", reportCard);
            req.setAttribute("student", student);
            req.setAttribute("activeYear", activeYear);
            req.setAttribute("currentTerm", currentTerm);
            req.getRequestDispatcher("/WEB-INF/views/report/view.jsp").forward(req, resp);
            return;
        }

        // List report cards for class
        String sectionIdStr = req.getParameter("sectionId");
        List<ClassSection> sections = academicDAO.getAllClassSections();
        List<ReportCard> reportCards = null;

        if (sectionIdStr != null && !sectionIdStr.isEmpty()) {
            int sectionId = Integer.parseInt(sectionIdStr);
            reportCards = reportCardDAO.getReportCardsForClass(sectionId, termId);
            ClassSection selectedSection = academicDAO.getClassSectionById(sectionId);
            req.setAttribute("selectedSection", selectedSection);
        }

        req.setAttribute("sections", sections);
        req.setAttribute("reportCards", reportCards);
        req.setAttribute("activeYear", activeYear);
        req.setAttribute("currentTerm", currentTerm);

        req.getRequestDispatcher("/WEB-INF/views/report/list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Only Admin, Principal, or Teacher can generate/publish report cards
        if (currentUser.getRole() == Role.STUDENT || currentUser.getRole() == Role.GUARDIAN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
            return;
        }

        String action = req.getParameter("action");
        if ("generate".equals(action)) {
            int studentId = Integer.parseInt(req.getParameter("studentId"));
            int sectionId = Integer.parseInt(req.getParameter("sectionId"));
            int termId = Integer.parseInt(req.getParameter("termId"));
            int academicYearId = Integer.parseInt(req.getParameter("academicYearId"));

            ClassSection cs = academicDAO.getClassSectionById(sectionId);
            int gradeId = (cs != null) ? cs.getGradeLevelId() : 1;

            List<StudentEnrollment> enrollments = studentDAO.getEnrollmentHistory(studentId);
            int enrollmentId = 1;
            if (!enrollments.isEmpty()) {
                enrollmentId = enrollments.get(0).getId();
            }

            reportCardDAO.generateOrRecalculateReportCard(studentId, enrollmentId, sectionId, gradeId, termId, academicYearId);
            resp.sendRedirect(req.getContextPath() + "/reports/view?studentId=" + studentId + "&success=generated");
        } else if ("publish".equals(action)) {
            int reportCardId = Integer.parseInt(req.getParameter("reportCardId"));
            int studentId = Integer.parseInt(req.getParameter("studentId"));
            reportCardDAO.publishReportCard(reportCardId);
            resp.sendRedirect(req.getContextPath() + "/reports/view?studentId=" + studentId + "&success=published");
        } else if ("updateComments".equals(action)) {
            int reportCardId = Integer.parseInt(req.getParameter("reportCardId"));
            int studentId = Integer.parseInt(req.getParameter("studentId"));
            String teacherRemarks = req.getParameter("teacherRemarks");
            String principalRemarks = req.getParameter("principalRemarks");
            reportCardDAO.updateComments(reportCardId, teacherRemarks, principalRemarks);
            resp.sendRedirect(req.getContextPath() + "/reports/view?studentId=" + studentId + "&success=commentsSaved");
        } else {
            resp.sendRedirect(req.getContextPath() + "/reports");
        }
    }
}
