package Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import service.AuthenticationService;

public class VerifyOtpServlet extends HttpServlet {

    private final AuthenticationService authService = new AuthenticationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (!authService.hasForgotPasswordEmail(session)) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        String message = authService.getForgotPasswordMessage(session);
        if (message != null) {
            request.setAttribute("message", message);
        }

        request.setAttribute("email", authService.getForgotPasswordEmail(session));
        request.getRequestDispatcher("/views/verify-otp.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (!authService.hasForgotPasswordEmail(session)) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        String otp = request.getParameter("otp");
        String result = authService.verifyForgotPasswordOtp(otp, session);

        if ("SUCCESS".equals(result)) {
            response.sendRedirect(request.getContextPath() + "/reset-password");
            return;
        }

        request.setAttribute("error", result);
        request.setAttribute("email", authService.getForgotPasswordEmail(session));
        request.getRequestDispatcher("/views/verify-otp.jsp")
                .forward(request, response);
    }
}