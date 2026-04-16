package Controllers;


import DALs.AccountDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;


public class ResetPasswordServlet extends HttpServlet {

    private static final String FORGOT_ACCOUNT_ID = "FORGOT_ACCOUNT_ID";
    private static final String FORGOT_EMAIL = "FORGOT_EMAIL";
    private static final String FORGOT_OTP = "FORGOT_OTP";
    private static final String FORGOT_OTP_EXPIRED_AT = "FORGOT_OTP_EXPIRED_AT";
    private static final String FORGOT_OTP_VERIFIED = "FORGOT_OTP_VERIFIED";
    private static final String FORGOT_OTP_ATTEMPTS = "FORGOT_OTP_ATTEMPTS";
    private static final String FORGOT_MESSAGE = "FORGOT_MESSAGE";

    private final AccountDAO accountDAO = new AccountDAO();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        Boolean verified = (Boolean) session.getAttribute(FORGOT_OTP_VERIFIED);

        if (verified == null || !verified) {

            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }


        request.setAttribute("email", session.getAttribute(FORGOT_EMAIL));

        request.getRequestDispatcher("/views/reset-password.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        Boolean verified = (Boolean) session.getAttribute(FORGOT_OTP_VERIFIED);

        if (verified == null || !verified) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        String email = (String) session.getAttribute(FORGOT_EMAIL);
        Integer accountId = (Integer) session.getAttribute(FORGOT_ACCOUNT_ID);

        if (newPassword == null || newPassword.trim().isEmpty()
                || confirmPassword == null || confirmPassword.trim().isEmpty()) {
            request.setAttribute("error", "Please enter all required fields.");
            request.setAttribute("email", email);

            request.getRequestDispatcher("/views/reset-password.jsp")
                    .forward(request, response);
            return;
        }


        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Confirm password does not match.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/views/reset-password.jsp")
                    .forward(request, response);
            return;
        }

        String passwordError = validatePassword(newPassword);
        if (passwordError != null) {
            request.setAttribute("error", passwordError);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/views/reset-password.jsp")
                    .forward(request, response);
            return;
        }

        if (accountId == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        int updatedRows = accountDAO.updatePasswordByAccountId(accountId, newPassword);
        if (updatedRows <= 0) {
            request.setAttribute("error", "Reset password failed.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/views/reset-password.jsp")
                    .forward(request, response);
            return;
        }

        clearForgotPasswordSession(session);

        request.setAttribute("message", "Password reset successfully. Please log in again.");
        request.getRequestDispatcher("/views/reset-password.jsp")
                .forward(request, response);
    }

    private String validatePassword(String password) {
        if (password == null || password.length() < 6) {
            return "Password must have at least 6 characters.";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter.";
        }
        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one number.";
        }
        return null;
    }

    private void clearForgotPasswordSession(HttpSession session) {
        session.removeAttribute(FORGOT_ACCOUNT_ID);
        session.removeAttribute(FORGOT_EMAIL);
        session.removeAttribute(FORGOT_OTP);
        session.removeAttribute(FORGOT_OTP_EXPIRED_AT);
        session.removeAttribute(FORGOT_OTP_VERIFIED);
        session.removeAttribute(FORGOT_OTP_ATTEMPTS);
        session.removeAttribute(FORGOT_MESSAGE);
    }
}

