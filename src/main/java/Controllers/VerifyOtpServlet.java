package Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

public class VerifyOtpServlet extends HttpServlet {

    private static final String FORGOT_ACCOUNT_ID = "FORGOT_ACCOUNT_ID";
    private static final String FORGOT_EMAIL = "FORGOT_EMAIL";
    private static final String FORGOT_OTP = "FORGOT_OTP";
    private static final String FORGOT_OTP_EXPIRED_AT = "FORGOT_OTP_EXPIRED_AT";
    private static final String FORGOT_OTP_VERIFIED = "FORGOT_OTP_VERIFIED";
    private static final String FORGOT_OTP_ATTEMPTS = "FORGOT_OTP_ATTEMPTS";
    private static final String FORGOT_MESSAGE = "FORGOT_MESSAGE";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String email = (String) session.getAttribute(FORGOT_EMAIL);

        if (email == null || email.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        String message = popForgotPasswordMessage(session);
        if (message != null) {
            request.setAttribute("message", message);
        }

        request.setAttribute("email", email);
        request.getRequestDispatcher("/views/verify-otp.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String email = (String) session.getAttribute(FORGOT_EMAIL);

        if (email == null || email.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        String otp = request.getParameter("otp");
        String sessionOtp = (String) session.getAttribute(FORGOT_OTP);
        LocalDateTime expiredAt = (LocalDateTime) session.getAttribute(FORGOT_OTP_EXPIRED_AT);
        Integer attempts = (Integer) session.getAttribute(FORGOT_OTP_ATTEMPTS);

        if (attempts == null) {
            attempts = 0;
        }

        if (otp == null || !otp.matches("^\\d{6}$")) {
            request.setAttribute("error", "OTP must be 6 digits.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/views/verify-otp.jsp")
                    .forward(request, response);
            return;
        }

        if (sessionOtp == null || expiredAt == null) {
            request.setAttribute("error", "OTP session is invalid. Please try again.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/views/verify-otp.jsp")
                    .forward(request, response);
            return;
        }

        if (LocalDateTime.now().isAfter(expiredAt)) {
            request.setAttribute("error", "OTP has expired.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/views/verify-otp.jsp")
                    .forward(request, response);
            return;
        }

        attempts++;
        session.setAttribute(FORGOT_OTP_ATTEMPTS, attempts);

        if (attempts > 5) {
            request.setAttribute("error", "You have entered OTP too many times.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/views/verify-otp.jsp")
                    .forward(request, response);
            return;
        }

        if (!otp.equals(sessionOtp)) {
            request.setAttribute("error", "Invalid OTP.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/views/verify-otp.jsp")
                    .forward(request, response);
            return;
        }

        session.setAttribute(FORGOT_OTP_VERIFIED, true);
        session.removeAttribute(FORGOT_OTP);
        session.removeAttribute(FORGOT_OTP_EXPIRED_AT);
        session.removeAttribute(FORGOT_OTP_ATTEMPTS);
        session.setAttribute(FORGOT_MESSAGE, "OTP verified successfully.");

        response.sendRedirect(request.getContextPath() + "/reset-password");
    }

    private String popForgotPasswordMessage(HttpSession session) {
        Object message = session.getAttribute(FORGOT_MESSAGE);
        if (message == null) {
            return null;
        }
        session.removeAttribute(FORGOT_MESSAGE);
        return message.toString();
    }
}