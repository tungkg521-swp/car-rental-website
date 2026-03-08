package Controllers;

import DALs.CustomerDAO;
import DALs.AccountDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.AccountModel;
import models.CustomerModel;
import service.AuthenticationService;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/views/login.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        AuthenticationService authService = new AuthenticationService();
        AccountModel account = authService.authenticate(email, password);

        if (account == null) {
            request.setAttribute("error", "Email hoặc mật khẩu không đúng");
            request.getRequestDispatcher("/views/login.jsp")
                    .forward(request, response);
            return;
        }if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
           request.setAttribute("error", "Tài khoản bị khóa");
            request.getRequestDispatcher("/views/login.jsp")
                    .forward(request, response);
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("ACCOUNT", account);

        CustomerDAO customerDAO = new CustomerDAO();
        CustomerModel customer = customerDAO.getByAccountId(account.getAccountId());

        session.setAttribute("CUSTOMER", customer);

        // UPDATE LAST LOGIN TIME
        AccountDAO accountDAO = new AccountDAO();
        accountDAO.updateLastLogin(account.getAccountId());

        response.sendRedirect(request.getContextPath() + "/home");
    }
}