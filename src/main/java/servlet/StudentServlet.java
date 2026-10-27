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
import java.sql.Date;
import java.util.List;

/**
 * Controller handling Student Registration, Search Directory,
 * Profile Viewing, and Historical Enrollment Records.
 */
@WebServlet("/students")
public class StudentServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();
    private final AcademicDAO academicDAO = new AcademicDAO();
    private final SchoolOperationsDAO schoolOpsDAO = new SchoolOperationsDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) action = "list";

        AcademicYear currentYear = academicDAO.getCurrentAcademicYear();
        int yearId = (currentYear != null) ? currentYear.getId() : 1;

        switch (action) {
            case "new":
            case "edit":
                request.setAttribute("gradeLevels", academicDAO.getAllGradeLevels());
                request.setAttribute("classSections", academicDAO.getClassSections(yearId, null));
                request.setAttribute("currentYear", currentYear);

                if ("edit".equals(action)) {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Student student = studentDAO.findById(id);
                    request.setAttribute("student", student);
                }
                request.getRequestDispatcher("/WEB-INF/views/student/form.jsp").forward(request, response);
                break;

            case "view":
                int id = Integer.parseInt(request.getParameter("id"));
                Student student = studentDAO.findById(id);
                List<StudentEnrollment> history = studentDAO.getEnrollmentHistory(id);
                request.setAttribute("student", student);
                request.setAttribute("enrollmentHistory", history);
                request.getRequestDispatcher("/WEB-INF/views/student/profile.jsp").forward(request, response);
                break;

            case "list":
            default:
                String search = request.getParameter("search");
                String gradeStr = request.getParameter("grade");
                Integer gradeNum = (gradeStr != null && !gradeStr.trim().isEmpty()) ? Integer.parseInt(gradeStr.trim()) : null;

                int page = 1;
                String pageStr = request.getParameter("page");
                if (pageStr != null && !pageStr.trim().isEmpty()) {
                    try { page = Math.max(1, Integer.parseInt(pageStr.trim())); } catch (NumberFormatException ignored) {}
                }
                int limit = 15;
                int offset = (page - 1) * limit;

                List<Student> list = studentDAO.findAll(search, gradeNum, offset, limit);
                int totalCount = studentDAO.countAll(search, gradeNum);
                int totalPages = (int) Math.ceil((double) totalCount / limit);

                request.setAttribute("students", list);
                request.setAttribute("totalCount", totalCount);
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", totalPages);
                request.setAttribute("search", search);
                request.setAttribute("selectedGrade", gradeNum);
                request.setAttribute("gradeLevels", academicDAO.getAllGradeLevels());

                request.getRequestDispatcher("/WEB-INF/views/student/list.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");

        String idStr = request.getParameter("id");
        String studentIdNum = request.getParameter("studentIdNumber");
        String fullNameEn = request.getParameter("fullNameEn");
        String fullNameAm = request.getParameter("fullNameAm");
        String email = request.getParameter("email");
        String dobStr = request.getParameter("dateOfBirth");
        String gender = request.getParameter("gender");
        String emergencyName = request.getParameter("emergencyContactName");
        String emergencyPhone = request.getParameter("emergencyContactPhone");
        String gradeStr = request.getParameter("gradeLevelId");
        String sectionStr = request.getParameter("classSectionId");
        String yearStr = request.getParameter("academicYearId");

        Student s = new Student();
        if (idStr != null && !idStr.trim().isEmpty()) {
            s.setId(Integer.parseInt(idStr.trim()));
        }

        s.setStudentIdNumber(studentIdNum != null && !studentIdNum.trim().isEmpty() ? studentIdNum.trim() : "STD-" + System.currentTimeMillis() % 100000);
        s.setFullNameEn(fullNameEn != null ? fullNameEn.trim() : "");
        s.setFullNameAm(fullNameAm != null ? fullNameAm.trim() : "");
        s.setName(s.getFullNameEn());
        s.setEmail(email);
        s.setGender(gender);
        s.setEmergencyContactName(emergencyName);
        s.setEmergencyContactPhone(emergencyPhone);
        s.setStatus("ACTIVE");
        s.setAdmissionDate(new Date(System.currentTimeMillis()));

        if (dobStr != null && !dobStr.trim().isEmpty()) {
            try { s.setDateOfBirth(Date.valueOf(dobStr.trim())); } catch (Exception ignored) {}
        }

        int gradeLevelId = (gradeStr != null && !gradeStr.trim().isEmpty()) ? Integer.parseInt(gradeStr.trim()) : 1;
        int sectionId = (sectionStr != null && !sectionStr.trim().isEmpty()) ? Integer.parseInt(sectionStr.trim()) : 1;
        int yearId = (yearStr != null && !yearStr.trim().isEmpty()) ? Integer.parseInt(yearStr.trim()) : 1;

        s.setYear(gradeLevelId); // For legacy compatibility

        if (s.getId() > 0) {
            studentDAO.update(s);
            studentDAO.enrollStudent(s.getId(), yearId, gradeLevelId, sectionId, "ACTIVE");
            schoolOpsDAO.logAudit(new AuditLog(currentUser.getId(), "UPDATE_STUDENT", "STUDENT", String.valueOf(s.getId()),
                    "Updated profile for student: " + s.getFullNameEn(), request.getRemoteAddr()));
        } else {
            int newId = studentDAO.create(s);
            if (newId > 0) {
                studentDAO.enrollStudent(newId, yearId, gradeLevelId, sectionId, "ACTIVE");
                schoolOpsDAO.logAudit(new AuditLog(currentUser.getId(), "REGISTER_STUDENT", "STUDENT", String.valueOf(newId),
                        "Registered new student: " + s.getFullNameEn() + " (ID: " + s.getStudentIdNumber() + ")", request.getRemoteAddr()));
            }
        }

        response.sendRedirect(request.getContextPath() + "/students?msg=saved");
    }
}
