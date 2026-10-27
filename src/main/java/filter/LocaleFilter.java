package filter;

import dao.UserDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;

/**
 * Filter managing bilingual locale switching between English ('en') and Amharic ('am').
 * Intercepts ?lang= parameter and persists in session and database user profile.
 */
@WebFilter("/*")
public class LocaleFilter implements Filter {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpSession session = req.getSession(true);

            String langParam = req.getParameter("lang");
            if (langParam != null && (langParam.equalsIgnoreCase("am") || langParam.equalsIgnoreCase("en"))) {
                String chosenLocale = langParam.toLowerCase();
                session.setAttribute("currentLocale", chosenLocale);

                User user = (User) session.getAttribute("currentUser");
                if (user != null && !chosenLocale.equals(user.getPreferredLocale())) {
                    user.setPreferredLocale(chosenLocale);
                    userDAO.updateProfile(user.getId(), user.getFullName(), user.getEmail(), user.getPhone(), chosenLocale);
                }
            } else if (session.getAttribute("currentLocale") == null) {
                // Default to English
                session.setAttribute("currentLocale", "en");
            }
        }

        chain.doFilter(request, response);
    }
}
