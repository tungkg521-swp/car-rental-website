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
import java.util.List;
import models.BookingModel;
import service.BookingService;

/**
 *
 * @author ADMIN
 */
@WebServlet("/staff/bookings")
public class StaffBookingController extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        BookingService service = new BookingService();

        // ===== 1. VIEW LIST =====
        if (action == null || action.equals("list")) {

            List<BookingModel> list = service.findAllBookings();
            request.setAttribute("bookingList", list);

            request.getRequestDispatcher("/views/staff-booking.jsp")
                    .forward(request, response);
        }

        // ===== 2. VIEW DETAIL =====
        else if (action.equals("detail")) {

            try {
                int id = Integer.parseInt(request.getParameter("id"));

                BookingModel booking = service.getBookingById(id);

                if (booking == null) {
                    response.sendRedirect(
                        request.getContextPath() + "/staff/bookings");
                    return;
                }

                request.setAttribute("booking", booking);

                request.getRequestDispatcher("/views/staff-booking-detail.jsp")
                        .forward(request, response);

            } catch (Exception e) {
                response.sendRedirect(
                    request.getContextPath() + "/staff/bookings");
            }
        }

        // ===== DEFAULT FALLBACK =====
        else {
            response.sendRedirect(
                request.getContextPath() + "/staff/bookings");
        }
    }
}
