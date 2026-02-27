/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.AccountModel;
import models.BookingModel;
import models.CarModel;
import models.CustomerModel;
import service.BookingService;

/**
 *
 * @author ADMIN
 */

public class BookingSuccess extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        AccountModel account =
                (AccountModel) session.getAttribute("ACCOUNT");

        Integer bookingId =
                (Integer) session.getAttribute("LAST_BOOKING");

        if (account == null || bookingId == null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        BookingService bookingService = new BookingService();
        BookingModel booking =
                bookingService.getById(bookingId);

        request.setAttribute("booking", booking);

        // cleanup
        session.removeAttribute("LAST_BOOKING");

        request.getRequestDispatcher("/views/booking-success.jsp")
               .forward(request, response);
    }
}

