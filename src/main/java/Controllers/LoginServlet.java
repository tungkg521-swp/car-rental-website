/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DALs.CustomerDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.AccountModel;
import models.CustomerModel;
import models.UserModel;
import service.AuthenticationService;

/**
 *
 * @author ADMIN
 */
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

        // LOGIN SUCCESS
        HttpSession session = request.getSession(true);
        session.setAttribute("ACCOUNT", account);

        CustomerModel customer
                = new CustomerDAO().getByAccountId(account.getAccountId());

        session.setAttribute("CUSTOMER", customer);

        response.sendRedirect(request.getContextPath() + "/home");
    }
}
