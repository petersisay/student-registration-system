package servlet;

import dao.AcademicDAO;
import dao.AssessmentDAO;
import dao.StudentDAO;
import dao.TeacherDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.AcademicTerm;
import model.AcademicYear;
import model.Assessment;
import model.AssessmentScore;
import model.ClassSection;
import model.Role;
import model.Student;
import model.Subject;
import model.Teacher;
import model.TeacherAssignment;
import model.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/assessments", "/assessments/*"})
public class AssessmentServlet extends HttpServlet {
    private final AssessmentDAO assessmentDAO = new AssessmentDAO();
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
        int activeYearId = (activeYear != null) ? activeYear.getId() : 1;
        AcademicTerm currentTerm = academicDAO.getCurrentTerm();
        int termId = (currentTerm != null) ? currentTerm.getId() : 1;

        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.equals("/scores")) {
            // View / enter scores for an assessment
            int assessmentId = Integer.parseInt(req.getParameter("id"));
            Assessment assessment = assessmentDAO.getAssessmentById(assessmentId);
            if (assessment == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Assessment not found");
                return;
            }

            // Teacher permission check
            if (currentUser.getRole() == Role.TEACHER) {
                Teacher teacher = teacherDAO.findByUserId(currentUser.getId());
                if (teacher == null || !teacherDAO.isTeacherAssignedToSubject(teacher.getId(), assessment.getClassSectionId(), assessment.getSubjectId())) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized: You are not assigned to this subject/class.");
                    return;
                }
            }

            List<Student> roster = studentDAO.getStudentsByClass(assessment.getClassSectionId(), activeYearId);
            List<AssessmentScore> scores = assessmentDAO.getScoresForAssessment(assessmentId);

            req.setAttribute("assessment", assessment);
            req.setAttribute("roster", roster);
            req.setAttribute("scores", scores);
            req.setAttribute("activeYear", activeYear);
            req.setAttribute("currentTerm", currentTerm);
            req.getRequestDispatcher("/WEB-INF/views/assessment/scores.jsp").forward(req, resp);
            return;
        }

        // List assessments
        Teacher teacher = null;
        List<TeacherAssignment> assignments = new ArrayList<>();
        List<ClassSection> sections = new ArrayList<>();
        List<Subject> subjects = new ArrayList<>();

        if (currentUser.getRole() == Role.TEACHER) {
            teacher = teacherDAO.findByUserId(currentUser.getId());
            if (teacher != null) {
                assignments = teacherDAO.getTeacherAssignments(teacher.getId(), activeYearId);
            }
        } else {
            sections = academicDAO.getAllClassSections();
            subjects = academicDAO.getAllSubjects();
        }

        String sectionIdStr = req.getParameter("sectionId");
        String subjectIdStr = req.getParameter("subjectId");
        List<Assessment> assessments = new ArrayList<>();

        if (sectionIdStr != null && !sectionIdStr.isEmpty() && subjectIdStr != null && !subjectIdStr.isEmpty()) {
            int sectionId = Integer.parseInt(sectionIdStr);
            int subjectId = Integer.parseInt(subjectIdStr);
            assessments = assessmentDAO.getAssessmentsForClassAndSubject(sectionId, subjectId, termId);
            req.setAttribute("selectedSectionId", sectionId);
            req.setAttribute("selectedSubjectId", subjectId);
        }

        req.setAttribute("teacher", teacher);
        req.setAttribute("assignments", assignments);
        req.setAttribute("sections", sections);
        req.setAttribute("subjects", subjects);
        req.setAttribute("assessments", assessments);
        req.setAttribute("activeYear", activeYear);
        req.setAttribute("currentTerm", currentTerm);

        req.getRequestDispatcher("/WEB-INF/views/assessment/list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        if ("create".equals(action)) {
            int sectionId = Integer.parseInt(req.getParameter("classSectionId"));
            int subjectId = Integer.parseInt(req.getParameter("subjectId"));
            int termId = Integer.parseInt(req.getParameter("termId"));
            String title = req.getParameter("title");
            String type = req.getParameter("assessmentType");
            BigDecimal maxScore = new BigDecimal(req.getParameter("maxScore"));
            BigDecimal weight = new BigDecimal(req.getParameter("weightPercent"));
            Date date = Date.valueOf(req.getParameter("assessmentDate"));

            int teacherId = 1;
            if (currentUser.getRole() == Role.TEACHER) {
                Teacher teacher = teacherDAO.findByUserId(currentUser.getId());
                if (teacher == null || !teacherDAO.isTeacherAssignedToSubject(teacher.getId(), sectionId, subjectId)) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized: You are not assigned to this class and subject.");
                    return;
                }
                teacherId = teacher.getId();
            }

            Assessment a = new Assessment();
            a.setClassSectionId(sectionId);
            a.setSubjectId(subjectId);
            a.setTermId(termId);
            a.setTeacherId(teacherId);
            a.setTitle(title);
            a.setAssessmentType(type);
            a.setMaxScore(maxScore);
            a.setWeightPercent(weight);
            a.setAssessmentDate(date);
            a.setStatus("PUBLISHED");

            int id = assessmentDAO.createAssessment(a);
            resp.sendRedirect(req.getContextPath() + "/assessments/scores?id=" + id + "&success=created");
        } else if ("saveScores".equals(action)) {
            int assessmentId = Integer.parseInt(req.getParameter("assessmentId"));
            Assessment assessment = assessmentDAO.getAssessmentById(assessmentId);
            if (assessment == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Assessment not found");
                return;
            }

            if (currentUser.getRole() == Role.TEACHER) {
                Teacher teacher = teacherDAO.findByUserId(currentUser.getId());
                if (teacher == null || !teacherDAO.isTeacherAssignedToSubject(teacher.getId(), assessment.getClassSectionId(), assessment.getSubjectId())) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized: You cannot enter scores for this subject.");
                    return;
                }
            }

            AcademicYear activeYear = academicDAO.getActiveAcademicYear();
            int activeYearId = (activeYear != null) ? activeYear.getId() : 1;
            List<Student> roster = studentDAO.getStudentsByClass(assessment.getClassSectionId(), activeYearId);
            List<AssessmentScore> scores = new ArrayList<>();

            for (Student s : roster) {
                String scoreStr = req.getParameter("score_" + s.getId());
                if (scoreStr != null && !scoreStr.trim().isEmpty()) {
                    try {
                        BigDecimal scoreVal = new BigDecimal(scoreStr.trim());
                        // Enforce range check: 0 <= score <= max_score
                        if (scoreVal.compareTo(BigDecimal.ZERO) < 0 || scoreVal.compareTo(assessment.getMaxScore()) > 0) {
                            resp.sendRedirect(req.getContextPath() + "/assessments/scores?id=" + assessmentId + "&error=range");
                            return;
                        }
                        AssessmentScore sc = new AssessmentScore();
                        sc.setAssessmentId(assessmentId);
                        sc.setStudentId(s.getId());
                        sc.setScore(scoreVal);
                        sc.setComments(req.getParameter("comment_" + s.getId()));
                        scores.add(sc);
                    } catch (NumberFormatException e) {
                        // ignore invalid entry
                    }
                }
            }

            assessmentDAO.recordScores(assessmentId, scores);
            resp.sendRedirect(req.getContextPath() + "/assessments/scores?id=" + assessmentId + "&success=saved");
        } else {
            resp.sendRedirect(req.getContextPath() + "/assessments");
        }
    }
}
