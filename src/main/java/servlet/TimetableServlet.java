package servlet;

import dao.AcademicDAO;
import dao.SchoolOperationsDAO;
import dao.StudentDAO;
import dao.TeacherDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.AcademicYear;
import model.ClassSection;
import model.Role;
import model.Student;
import model.StudentEnrollment;
import model.Subject;
import model.Teacher;
import model.TimetableEntry;
import model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/timetable", "/timetable/*"})
public class TimetableServlet extends HttpServlet {
    private final SchoolOperationsDAO schoolOperationsDAO = new SchoolOperationsDAO();
    private final AcademicDAO academicDAO = new AcademicDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();
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
        List<TimetableEntry> entries = new ArrayList<>();
        List<ClassSection> sections = academicDAO.getAllClassSections();
        List<Teacher> teachers = teacherDAO.getAllTeachers();
        List<Subject> subjects = academicDAO.getAllSubjects();

        String sectionIdStr = req.getParameter("sectionId");
        int selectedSectionId = 0;

        if (currentUser.getRole() == Role.STUDENT) {
            Student student = studentDAO.findByUserId(currentUser.getId());
            if (student != null) {
                List<StudentEnrollment> enrollments = studentDAO.getEnrollmentHistory(student.getId());
                if (!enrollments.isEmpty()) {
                    selectedSectionId = enrollments.get(0).getClassSectionId();
                    entries = schoolOperationsDAO.getTimetableForClass(selectedSectionId);
                }
            }
        } else if (currentUser.getRole() == Role.TEACHER) {
            Teacher teacher = teacherDAO.findByUserId(currentUser.getId());
            if (teacher != null) {
                entries = schoolOperationsDAO.getTimetableForTeacher(teacher.getId());
            }
        } else {
            // Admin, Principal, Registrar
            if (sectionIdStr != null && !sectionIdStr.isEmpty()) {
                selectedSectionId = Integer.parseInt(sectionIdStr);
                entries = schoolOperationsDAO.getTimetableForClass(selectedSectionId);
            } else if (!sections.isEmpty()) {
                selectedSectionId = sections.get(0).getId();
                entries = schoolOperationsDAO.getTimetableForClass(selectedSectionId);
            }
        }

        req.setAttribute("entries", entries);
        req.setAttribute("sections", sections);
        req.setAttribute("teachers", teachers);
        req.setAttribute("subjects", subjects);
        req.setAttribute("selectedSectionId", selectedSectionId);
        req.setAttribute("activeYear", activeYear);

        req.getRequestDispatcher("/WEB-INF/views/timetable/view.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only administrators can schedule timetable entries.");
            return;
        }

        int sectionId = Integer.parseInt(req.getParameter("classSectionId"));
        int subjectId = Integer.parseInt(req.getParameter("subjectId"));
        int teacherId = Integer.parseInt(req.getParameter("teacherId"));
        String day = req.getParameter("dayOfWeek");
        int period = Integer.parseInt(req.getParameter("periodNumber"));
        String startTime = req.getParameter("startTime");
        String endTime = req.getParameter("endTime");

        TimetableEntry entry = new TimetableEntry();
        entry.setClassSectionId(sectionId);
        entry.setSubjectId(subjectId);
        entry.setTeacherId(teacherId);
        entry.setDayOfWeek(day);
        entry.setPeriodNumber(period);
        entry.setStartTime(startTime != null && !startTime.isEmpty() ? startTime : "08:30");
        entry.setEndTime(endTime != null && !endTime.isEmpty() ? endTime : "09:15");

        schoolOperationsDAO.saveTimetableEntry(entry);
        resp.sendRedirect(req.getContextPath() + "/timetable?sectionId=" + sectionId + "&success=saved");
    }
}
