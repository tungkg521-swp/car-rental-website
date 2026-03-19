package Controllers;

import DALs.AccountDAO;
import DALs.CustomerDAO;
import Utils.RoleConstants;
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
        }

        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            request.setAttribute("error", "Tài khoản đã bị khóa");
            request.getRequestDispatcher("/views/login.jsp")
                    .forward(request, response);
            return;
        }

        // Chỉ cho CUSTOMER login ở trang này
        if (account.getRoleId() != RoleConstants.CUSTOMER) {
            request.setAttribute("error", "Tài khoản Staff/Admin vui lòng đăng nhập tại Dashboard Login");
            request.getRequestDispatcher("/views/login.jsp")
                    .forward(request, response);
            return;
        }

        CustomerDAO customerDAO = new CustomerDAO();
        CustomerModel customer = customerDAO.getByAccountId(account.getAccountId());

        if (customer == null) {
            request.setAttribute("error", "Không tìm thấy hồ sơ khách hàng");
            request.getRequestDispatcher("/views/login.jsp")
                    .forward(request, response);
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("ACCOUNT", account);
        session.setAttribute("CUSTOMER", customer);

        customerDAO.updateStatus(customer.getCustomerId(), "ACTIVE");

        AccountDAO accountDAO = new AccountDAO();
        accountDAO.updateLastLogin(account.getAccountId());

        response.sendRedirect(request.getContextPath() + "/home");
    }
}