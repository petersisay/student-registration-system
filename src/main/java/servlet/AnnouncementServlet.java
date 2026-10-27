package servlet;

import dao.SchoolOperationsDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Announcement;
import model.Role;
import model.User;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet(urlPatterns = {"/announcements", "/announcements/*"})
public class AnnouncementServlet extends HttpServlet {
    private final SchoolOperationsDAO schoolOperationsDAO = new SchoolOperationsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        String roleStr = (currentUser != null && currentUser.getRole() != null) ? currentUser.getRole().name() : "ALL";

        List<Announcement> announcements = schoolOperationsDAO.getActiveAnnouncements(roleStr);
        req.setAttribute("announcements", announcements);
        req.getRequestDispatcher("/WEB-INF/views/announcement/list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null || (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.PRINCIPAL)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized to post announcements.");
            return;
        }

        String titleEn = req.getParameter("titleEn");
        String titleAm = req.getParameter("titleAm");
        String contentEn = req.getParameter("contentEn");
        String contentAm = req.getParameter("contentAm");
        String targetRole = req.getParameter("targetRole");

        Announcement a = new Announcement();
        a.setTitleEn(titleEn);
        a.setTitleAm(titleAm);
        a.setContentEn(contentEn);
        a.setContentAm(contentAm);
        a.setTargetRole(targetRole != null ? targetRole : "ALL");
        a.setAuthorId(currentUser.getId());
        a.setPublishedDate(new Date(System.currentTimeMillis()));
        a.setActive(true);

        schoolOperationsDAO.createAnnouncement(a);
        resp.sendRedirect(req.getContextPath() + "/announcements?success=posted");
    }
}
