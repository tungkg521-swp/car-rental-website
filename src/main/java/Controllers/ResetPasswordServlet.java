package Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import service.AuthenticationService;

public class ResetPasswordServlet extends HttpServlet {

    private final AuthenticationService authService = new AuthenticationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        if (!authService.isForgotOtpVerified(session)) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        request.setAttribute("email", authService.getForgotPasswordEmail(session));
        request.getRequestDispatcher("/views/reset-password.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        String result = authService.resetPasswordAfterOtp(
                session,
                newPassword,
                confirmPassword
        );

        if ("SUCCESS".equals(result)) {
            request.setAttribute("message", "Password reset successfully. Please log in again.");
            request.getRequestDispatcher("/views/reset-password.jsp")
                    .forward(request, response);
            return;
        }

        request.setAttribute("error", result);
        request.setAttribute("email", authService.getForgotPasswordEmail(session));
        request.getRequestDispatcher("/views/reset-password.jsp")
                .forward(request, response);
    }
}