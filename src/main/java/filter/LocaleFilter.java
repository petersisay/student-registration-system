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

            String activeLocale = (String) session.getAttribute("currentLocale");
            if (activeLocale == null) activeLocale = "en";
            final String lang = activeLocale;
            req.setAttribute("currentLocale", lang);
            req.setAttribute("i18n", new java.util.AbstractMap<String, String>() {
                @Override
                public java.util.Set<java.util.Map.Entry<String, String>> entrySet() {
                    return java.util.Collections.emptySet();
                }
                @Override
                public String get(Object key) {
                    return util.I18nUtil.getMessage(lang, String.valueOf(key));
                }
            });
        }

        chain.doFilter(request, response);
    }
}
