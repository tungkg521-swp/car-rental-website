
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DALs.CustomerDAO;
import service.BookingService;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import models.AccountModel;
import models.BookingModel;
import models.CarModel;
import models.CustomerModel;
import models.UserModel;
import service.CarService;

/**
 *
 * @author ADMIN
 */
public class BookingServlet extends HttpServlet {

    private BookingService bookingService = new BookingService();

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        switch (action) {

            case "list":  
                viewBookingList(request, response);
                break;

            case "detail":
                viewBookingDetail(request, response);
                break;

            default:
                response.sendRedirect(request.getContextPath() + "/home");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/cars");
            return;
        }

        switch (action) {
            case "list":
                viewBookingList(request, response);
                break;
            case "cancel":
                cancelBooking(request, response);
                break;
            

            default:
                response.sendRedirect(request.getContextPath() + "/cars");
                break;
        }
    }

    
    private void viewBookingList(HttpServletRequest request,
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
                = new CustomerDAO()
                        .getByAccountId(account.getAccountId());

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<BookingModel> bookings
                = bookingService.getBookingsByCustomer(
                        customer.getCustomerId());

        request.setAttribute("BOOKINGS", bookings);
        System.out.println("BOOKINGS SIZE = " + bookings.size());

        request.getRequestDispatcher("/views/booking-list.jsp")
                .forward(request, response);
    }

    private void viewBookingDetail(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        AccountModel account
                = (AccountModel) session.getAttribute("ACCOUNT");

        if (account == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        CustomerModel customer
                = new CustomerDAO().getByAccountId(account.getAccountId());

        String bookingIdRaw = request.getParameter("bookingId");

        if (bookingIdRaw == null) {
            response.sendRedirect(request.getContextPath() + "/customer/bookings?action=list");
            return;
        }

        int bookingId;

        try {
            bookingId = Integer.parseInt(bookingIdRaw);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/customer/bookings?action=list");
            return;
        }

        BookingModel booking
                = bookingService.getBookingDetail(
                        bookingId,
                        customer.getCustomerId());

        if (booking == null) {
            request.setAttribute("error", "Booking not found");
            request.getRequestDispatcher("/views/error.jsp")
                    .forward(request, response);
            return;
        }

        request.setAttribute("booking", booking);
        request.getRequestDispatcher("/views/booking-detail.jsp")
                .forward(request, response);
    }

    private void cancelBooking(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        AccountModel account = (AccountModel) session.getAttribute("ACCOUNT");

        if (account == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        CustomerModel customer
                = new CustomerDAO().getByAccountId(account.getAccountId());
        
        System.out.println("Customer login ID = " + customer.getCustomerId());

        String bookingIdRaw = request.getParameter("bookingId");

        if (bookingIdRaw == null) {
            response.sendRedirect(request.getContextPath() + "/booking?action=list");
            return;
        }

        int bookingId = Integer.parseInt(bookingIdRaw);

        boolean success
                = bookingService.cancelBooking(
                        bookingId,
                        customer.getCustomerId()
                );

        if (!success) {
            request.setAttribute("error", "Cannot cancel this booking");
            viewBookingDetail(request, response);
            return;
        }

        response.sendRedirect(
                request.getContextPath()
                + "/booking?action=detail&bookingId=" + bookingId
        );
    }

}
