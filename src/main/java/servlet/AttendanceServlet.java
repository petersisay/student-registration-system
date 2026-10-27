package servlet;

import dao.AcademicDAO;
import dao.AttendanceDAO;
import dao.StudentDAO;
import dao.TeacherDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.AcademicYear;
import model.Attendance;
import model.ClassSection;
import model.Role;
import model.Student;
import model.Teacher;
import model.TeacherAssignment;
import model.User;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/attendance", "/attendance/*"})
public class AttendanceServlet extends HttpServlet {
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final AcademicDAO academicDAO = new AcademicDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();

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

        List<ClassSection> allowedSections = new ArrayList<>();
        Teacher teacher = null;

        if (currentUser.getRole() == Role.TEACHER) {
            teacher = teacherDAO.findByUserId(currentUser.getId());
            if (teacher != null) {
                List<TeacherAssignment> assignments = teacherDAO.getTeacherAssignments(teacher.getId(), activeYearId);
                Map<Integer, Boolean> seen = new HashMap<>();
                for (TeacherAssignment a : assignments) {
                    if (!seen.containsKey(a.getClassSectionId())) {
                        ClassSection cs = academicDAO.getClassSectionById(a.getClassSectionId());
                        if (cs != null) {
                            allowedSections.add(cs);
                            seen.put(a.getClassSectionId(), true);
                        }
                    }
                }
            }
        } else {
            allowedSections = academicDAO.getAllClassSections();
        }

        String sectionIdStr = req.getParameter("sectionId");
        String dateStr = req.getParameter("date");
        Date attendanceDate = (dateStr != null && !dateStr.isEmpty()) ? Date.valueOf(dateStr) : Date.valueOf(LocalDate.now());

        if (sectionIdStr != null && !sectionIdStr.isEmpty()) {
            int sectionId = Integer.parseInt(sectionIdStr);

            // Server-side authorization check: verify teacher has access
            if (currentUser.getRole() == Role.TEACHER) {
                if (teacher == null || !teacherDAO.isTeacherAssignedToClass(teacher.getId(), sectionId)) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized: You are not assigned to this class section.");
                    return;
                }
            }

            ClassSection selectedSection = academicDAO.getClassSectionById(sectionId);
            List<Student> roster = studentDAO.getStudentsByClass(sectionId, activeYearId);
            List<Attendance> existing = attendanceDAO.getAttendanceForClassAndDate(sectionId, attendanceDate);
            Map<Integer, String> statusMap = new HashMap<>();
            for (Attendance a : existing) {
                statusMap.put(a.getStudentId(), a.getStatus());
            }

            req.setAttribute("selectedSection", selectedSection);
            req.setAttribute("roster", roster);
            req.setAttribute("statusMap", statusMap);
            req.setAttribute("existingCount", existing.size());
        }

        req.setAttribute("allowedSections", allowedSections);
        req.setAttribute("attendanceDate", attendanceDate.toString());
        req.setAttribute("activeYear", activeYear);

        req.getRequestDispatcher("/WEB-INF/views/attendance/record.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        AcademicYear activeYear = academicDAO.getActiveAcademicYear();
        int activeYearId = (activeYear != null) ? activeYear.getId() : 1;

        int sectionId = Integer.parseInt(req.getParameter("sectionId"));
        String dateStr = req.getParameter("date");
        Date attendanceDate = (dateStr != null && !dateStr.isEmpty()) ? Date.valueOf(dateStr) : Date.valueOf(LocalDate.now());

        // Server-side authorization check for teacher
        if (currentUser.getRole() == Role.TEACHER) {
            Teacher teacher = teacherDAO.findByUserId(currentUser.getId());
            if (teacher == null || !teacherDAO.isTeacherAssignedToClass(teacher.getId(), sectionId)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized: You are not assigned to this class section.");
                return;
            }
        }

        List<Student> roster = studentDAO.getStudentsByClass(sectionId, activeYearId);
        List<Attendance> attendances = new ArrayList<>();

        for (Student s : roster) {
            String status = req.getParameter("status_" + s.getId());
            if (status != null && !status.trim().isEmpty()) {
                Attendance a = new Attendance();
                a.setStudentId(s.getId());
                a.setClassSectionId(sectionId);
                a.setAcademicYearId(activeYearId);
                a.setAttendanceDate(attendanceDate);
                a.setStatus(status.trim().toUpperCase());
                a.setRecordedByUserId(currentUser.getId());
                a.setNotes(req.getParameter("notes_" + s.getId()));
                attendances.add(a);
            }
        }

        attendanceDAO.batchRecord(attendances);
        resp.sendRedirect(req.getContextPath() + "/attendance?sectionId=" + sectionId + "&date=" + attendanceDate + "&success=saved");
    }
}
