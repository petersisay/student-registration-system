package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Filter providing Cross-Site Request Forgery (CSRF) protection.
 * Issues and validates unique session tokens for state-changing HTTP requests.
 */
@WebFilter("/*")
public class CsrfFilter implements Filter {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;
            HttpSession session = req.getSession(true);

            // Ensure CSRF token exists on the session
            String csrfToken = (String) session.getAttribute("csrfToken");
            if (csrfToken == null) {
                byte[] bytes = new byte[24];
                RANDOM.nextBytes(bytes);
                csrfToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
                session.setAttribute("csrfToken", csrfToken);
            }

            String method = req.getMethod();
            String path = req.getServletPath();

            // Validate POST/PUT/DELETE requests, except login page
            if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
                if (!isExemptPath(path)) {
                    String paramToken = req.getParameter("csrfToken");
                    String headerToken = req.getHeader("X-CSRF-Token");

                    if ((paramToken == null || !paramToken.equals(csrfToken)) &&
                        (headerToken == null || !headerToken.equals(csrfToken))) {
                        res.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token");
                        return;
                    }
                }
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isExemptPath(String path) {
        if (path == null) return false;
        // Allow public login post without requiring pre-session token if user is unauthenticated
        return path.equals("/login") || path.equals("/register");
    }
}
