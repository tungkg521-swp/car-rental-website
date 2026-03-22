/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DALs.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import models.AccountModel;
import models.BookingModel;
import models.CustomerModel;
import service.BookingService;
import service.PaymentService;

public class PaymentServlet extends HttpServlet {

    private final BookingService bookingService = new BookingService();
    private final PaymentService paymentService = new PaymentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("=== PaymentServlet doGet ===");
        System.out.println("URI: " + request.getRequestURI());
        System.out.println("action: " + request.getParameter("action"));
        System.out.println("bookingId: " + request.getParameter("bookingId"));
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        switch (action) {
            case "create":
                showPaymentPage(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/home");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        switch (action) {
            case "sandbox-success":
                handleSandboxPayment(request, response, true);
                break;
            case "sandbox-fail":
                handleSandboxPayment(request, response, false);
                break;
            case "cancel-payment":
                cancelPayment(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/home");
                break;
        }
    }

    private void showPaymentPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        CustomerModel customer = getCurrentCustomer(request, response);
        if (customer == null) {
            return;
        }

        int bookingId;
        try {
            bookingId = Integer.parseInt(request.getParameter("bookingId"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/booking?action=list");
            return;
        }

        BookingModel booking = bookingService.getBookingDetail(bookingId, customer.getCustomerId());
        if (booking == null) {
            response.sendRedirect(request.getContextPath() + "/booking?action=list");
            return;
        }

        BigDecimal total = booking.getTotalEstimatedPrice();
        BigDecimal deposit = paymentService.calculateDeposit(total);
        BigDecimal remaining = paymentService.calculateRemaining(total);

        request.setAttribute("booking", booking);
        request.setAttribute("depositAmount", deposit);
        request.setAttribute("remainingAmount", remaining);

        request.getRequestDispatcher("/views/payment.jsp").forward(request, response);
    }

    private void handleSandboxPayment(HttpServletRequest request, HttpServletResponse response, boolean success)
            throws IOException {

        CustomerModel customer = getCurrentCustomer(request, response);
        if (customer == null) {
            return;
        }

        int bookingId;
        try {
            bookingId = Integer.parseInt(request.getParameter("bookingId"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/booking?action=list");
            return;
        }

        BookingModel booking = bookingService.getBookingDetail(bookingId, customer.getCustomerId());
        if (booking == null) {
            response.sendRedirect(request.getContextPath() + "/booking?action=list");
            return;
        }

        String paymentMethod = request.getParameter("paymentMethod");
        if (paymentMethod == null || paymentMethod.isBlank()) {
            paymentMethod = "VNPAY";
        }

        boolean processed = paymentService.processSandboxPayment(bookingId, paymentMethod, success);

        if (processed && success) {
            response.sendRedirect(request.getContextPath()
                    + "/payment?action=create&bookingId=" + bookingId + "&result=success");
            return;
        }

        if (processed) {
            response.sendRedirect(request.getContextPath()
                    + "/booking?action=detail&bookingId=" + bookingId + "&cancelStatus=payment_fail");
            return;
        }

        response.sendRedirect(request.getContextPath()
                + "/payment?action=create&bookingId=" + bookingId + "&result=invalid");
    }

    private void cancelPayment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        CustomerModel customer = getCurrentCustomer(request, response);
        if (customer == null) {
            return;
        }

        int bookingId;
        try {
            bookingId = Integer.parseInt(request.getParameter("bookingId"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/booking?action=list");
            return;
        }

        boolean cancelled = bookingService.cancelBooking(bookingId, customer.getCustomerId());

        if (cancelled) {
            response.sendRedirect(request.getContextPath()
                    + "/booking?action=detail&bookingId=" + bookingId + "&cancelStatus=success");
        } else {
            response.sendRedirect(request.getContextPath()
                    + "/payment?action=create&bookingId=" + bookingId + "&result=invalid");
        }
    }

    private CustomerModel getCurrentCustomer(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }

        AccountModel account = (AccountModel) session.getAttribute("ACCOUNT");
        if (account == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }

        CustomerDAO customerDAO = new CustomerDAO();
        CustomerModel customer = customerDAO.getByAccountId(account.getAccountId());

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }

        return customer;
    }
}
