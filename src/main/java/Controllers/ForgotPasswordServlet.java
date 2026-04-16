package Controllers;


import DALs.AccountDAO;
import Utils.MailUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

import java.time.LocalDateTime;
import java.util.Random;
import models.AccountModel;

public class ForgotPasswordServlet extends HttpServlet {

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

        String message = popForgotPasswordMessage(session);


        if (message != null) {
            request.setAttribute("message", message);
        }

        request.getRequestDispatcher("/views/forgot-password.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        HttpSession session = request.getSession();


        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Email is required.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/views/forgot-password.jsp")
                    .forward(request, response);
            return;
        }

        email = email.trim();
        AccountModel account = accountDAO.findByEmail(email);

        if (account == null) {
            request.setAttribute("error", "Email does not exist.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/views/forgot-password.jsp")
                    .forward(request, response);
            return;
        }

        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            request.setAttribute("error", "This account is not active.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/views/forgot-password.jsp")
                    .forward(request, response);
            return;
        }

        clearForgotPasswordSession(session);

        String otp = generateOtp();
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(5);

        try {
            MailUtil.sendOtpEmail(email, otp);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to send OTP email.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/views/forgot-password.jsp")
                    .forward(request, response);
            return;
        }

        session.setAttribute(FORGOT_ACCOUNT_ID, account.getAccountId());
        session.setAttribute(FORGOT_EMAIL, email);
        session.setAttribute(FORGOT_OTP, otp);
        session.setAttribute(FORGOT_OTP_EXPIRED_AT, expiredAt);
        session.setAttribute(FORGOT_OTP_VERIFIED, false);
        session.setAttribute(FORGOT_OTP_ATTEMPTS, 0);
        session.setAttribute(FORGOT_MESSAGE, "OTP has been sent to your email.");

        response.sendRedirect(request.getContextPath() + "/verify-otp");
    }

    private String popForgotPasswordMessage(HttpSession session) {
        Object message = session.getAttribute(FORGOT_MESSAGE);
        if (message == null) {
            return null;
        }
        session.removeAttribute(FORGOT_MESSAGE);
        return message.toString();
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

    private String generateOtp() {
        Random random = new Random();
        int value = 100000 + random.nextInt(900000);
        return String.valueOf(value);

    }
}