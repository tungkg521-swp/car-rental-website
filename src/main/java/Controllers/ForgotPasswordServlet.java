package Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import service.AuthenticationService;

public class ForgotPasswordServlet extends HttpServlet {

    private final AuthenticationService authService = new AuthenticationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String message = authService.getForgotPasswordMessage(session);

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

        String result = authService.sendForgotPasswordOtp(email, session);

        if ("SUCCESS".equals(result)) {
            response.sendRedirect(request.getContextPath() + "/verify-otp");
            return;
        }

        request.setAttribute("error", result);
        request.setAttribute("email", email);
        request.getRequestDispatcher("/views/forgot-password.jsp")
                .forward(request, response);
    }
}