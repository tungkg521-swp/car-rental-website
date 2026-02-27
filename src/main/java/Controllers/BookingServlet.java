
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
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import models.AccountModel;
import models.BookingModel;
import models.CarModel;
import models.CustomerModel;
import models.UserModel;
import service.BookingService;
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
            case "create":
                showCreateBooking(request, response);
                break;

            case "list":   // ⭐ THÊM DÒNG NÀY
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
            case "create":
                createBooking(request, response);
                break;
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

    private void showCreateBooking(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Check session
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 2. Lấy account từ session (KHÔNG DÙNG UserModel)
        AccountModel account
                = (AccountModel) session.getAttribute("ACCOUNT");

        if (account == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 3. Lấy customer theo accountId
        CustomerDAO customerDAO = new CustomerDAO();
        CustomerModel customer
                = customerDAO.getByAccountId(account.getAccountId());

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 4. Lấy carId
        String carIdRaw = request.getParameter("carId");
        if (carIdRaw == null) {
            response.sendRedirect(request.getContextPath() + "/cars");
            return;
        }

        int carId;
        try {
            carId = Integer.parseInt(carIdRaw);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/cars");
            return;
        }

        // 5. Load xe
        CarService carService = new CarService();
        CarModel car = carService.getCarById(carId);

        if (car == null) {
            response.sendRedirect(request.getContextPath() + "/cars");
            return;
        }

        if (!"AVAILABLE".equalsIgnoreCase(car.getStatus())) {
            response.sendRedirect(
                    request.getContextPath() + "/car-detail?carId=" + carId
            );
            return;
        }

        // 6. Set data cho JSP
        request.setAttribute("account", account);   // nếu cần role sau này
        request.setAttribute("customer", customer); // HIỂN THỊ
        request.setAttribute("car", car);

        // 7. Forward
        request.getRequestDispatcher("/views/booking.jsp")
                .forward(request, response);
    }

    private void createBooking(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        AccountModel account = (AccountModel) session.getAttribute("ACCOUNT");

        if (account == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        CustomerDAO customerDAO = new CustomerDAO();
        CustomerModel customer = customerDAO.getByAccountId(account.getAccountId());

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String carIdRaw = request.getParameter("carId");
        String startDateRaw = request.getParameter("startDate");
        String endDateRaw = request.getParameter("endDate");
        String note = request.getParameter("note");

        if (carIdRaw == null || startDateRaw == null || endDateRaw == null) {
            response.sendRedirect(request.getContextPath() + "/cars");
            return;
        }

        int carId;
        try {
            carId = Integer.parseInt(carIdRaw);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/cars");
            return;
        }

        Date startDate;
        Date endDate;
        try {
            startDate = Date.valueOf(startDateRaw);
            endDate = Date.valueOf(endDateRaw);
        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/booking?carId=" + carId);
            return;
        }

        if (endDate.before(startDate)) {
            response.sendRedirect(request.getContextPath() + "/booking?carId=" + carId);
            return;
        }

        CarService carService = new CarService();
        CarModel car = carService.getCarById(carId);

        if (car == null || !"AVAILABLE".equalsIgnoreCase(car.getStatus())) {
            response.sendRedirect(request.getContextPath() + "/cars");
            return;
        }

        BookingService bookingService = new BookingService();
        BigDecimal totalPrice = bookingService.calculateTotalPrice(
                startDate,
                endDate,
                car.getPricePerDay()
        );

        BookingModel booking = new BookingModel();
        booking.setCustomerId(customer.getCustomerId());
        booking.setCarId(carId);
        booking.setBookingDate(new Timestamp(System.currentTimeMillis()));
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setStatus("PENDING");
        booking.setNote(note);
        booking.setTotalEstimatedPrice(totalPrice);

        try {
            bookingService.createBooking(booking);

            session.setAttribute("LAST_BOOKING", booking.getBookingId());
            response.sendRedirect(request.getContextPath() + "/booking-success");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/booking?carId=" + carId);
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
