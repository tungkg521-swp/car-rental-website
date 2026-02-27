/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import service.CustomerService;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import models.CustomerModel;

/**
 *
 * @author ADMIN
 */
@WebServlet("/staff/users")
public class StaffCustomerController extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        CustomerService service = new CustomerService();

        // ===== LIST =====
        if (action == null || action.equals("list")) {

            List<CustomerModel> list = service.findAllCustomers();
            request.setAttribute("customerList", list);

            request.getRequestDispatcher("/views/staff-users.jsp")
                   .forward(request, response);
        }

        // ===== DETAIL =====
        else if (action.equals("detail")) {

            int id = Integer.parseInt(request.getParameter("id"));

            CustomerModel customer = service.getCustomerById(id);

            if (customer == null) {
                response.sendRedirect(request.getContextPath() + "/staff/users");
                return;
            }

            request.setAttribute("customer", customer);

            request.getRequestDispatcher("/views/staff-user-detail.jsp")
                   .forward(request, response);
        }
    }
}
