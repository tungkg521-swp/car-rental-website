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
import service.CustomerService;

/**
 *
 * @author ADMIN
 */
public class ProfileServlet extends HttpServlet {

    private CustomerService customerService = new CustomerService();

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        viewProfile(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("update".equals(action)) {
            updateProfile(request, response);
        }
    }

    /* ================= VIEW PROFILE ================= */
    private void viewProfile(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        AccountModel account
                = (AccountModel) session.getAttribute("ACCOUNT");

        if (account == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        CustomerModel customer
                = customerService.getProfileByAccountId(
                        account.getAccountId());

        request.setAttribute("CUSTOMER_PROFILE", customer);

        request.getRequestDispatcher("/views/profile.jsp")
                .forward(request, response);
    }

    /* ================= UPDATE PROFILE ================= */
    private void updateProfile(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        AccountModel account
                = (AccountModel) session.getAttribute("ACCOUNT");

        if (account == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String fullName = request.getParameter("fullName");
        String dob = request.getParameter("dob");

        String phone = request.getParameter("phone");
        String email = request.getParameter("email");

        boolean success = customerService.updateProfile(
                account.getAccountId(),
                fullName,
                dob,
                phone,
                email
        );

        if (success) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/customer/profile?msg=success"
            );
        } else {
            response.sendRedirect(
                    request.getContextPath()
                    + "/customer/profile?msg=error"
            );
        }
    }

}
