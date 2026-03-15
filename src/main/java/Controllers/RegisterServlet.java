package Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import service.AuthenticationService;

public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullName = request.getParameter("fullName").trim();
        String email = request.getParameter("email").trim();
        String phone = request.getParameter("phone").trim();
        String address = request.getParameter("address").trim();
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String dobStr = request.getParameter("dob");

        LocalDate dob = null;

        try {
            if (dobStr != null && !dobStr.isEmpty()) {
                dob = LocalDate.parse(dobStr);
            }

            AuthenticationService service = new AuthenticationService();

            service.register(fullName, email, password, confirmPassword, phone, address, dob);

            response.sendRedirect("login");

        } catch (Exception e) {

            request.setAttribute("error", e.getMessage());

            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("address", address);
            request.setAttribute("dob", dobStr);

            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
        }
    }
}
