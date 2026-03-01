/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DALs.AccountDAO;
import DALs.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import models.AccountModel;
import models.CustomerModel;
import service.AuthenticationService;

public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Mở trang đăng ký
        request.getRequestDispatcher("/views/register.jsp").forward(request, response);
    }

  @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    String fullName = request.getParameter("fullName");
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    String confirmPassword = request.getParameter("confirmPassword");
    String phone = request.getParameter("phone");
    String address = request.getParameter("address");
    String dobStr = request.getParameter("dob");

    java.time.LocalDate dob = null;
    if (dobStr != null && !dobStr.isEmpty()) {
        dob = java.time.LocalDate.parse(dobStr);
    }

    AuthenticationService service = new AuthenticationService();

    try {

        service.register(fullName, email, password, confirmPassword, phone, address, dob);

        response.sendRedirect("login");

    } catch (Exception e) {

        request.setAttribute("error", e.getMessage());
        request.getRequestDispatcher("/views/register.jsp").forward(request, response);
    }
}
}
