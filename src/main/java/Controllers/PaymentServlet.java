/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;


import DALs.BookingDAO;
import DALs.CarDAO;
import DALs.ContractDAO;
import DALs.CustomerDAO;
import DALs.PaymentDAO;
import DALs.VoucherDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;

import java.math.RoundingMode;
import java.sql.Timestamp;
import models.AccountModel;
import models.BookingModel;
import models.CarModel;
import models.ContractModel;
import models.CustomerModel;
import models.PaymentModel;

public class PaymentServlet extends HttpServlet {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final ContractDAO contractDAO = new ContractDAO();
    private final CarDAO carDAO = new CarDAO();
    private final VoucherDAO voucherDAO = new VoucherDAO();


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
                handlePayment(request, response, true);
                break;
            case "sandbox-fail":
                handlePayment(request, response, false);
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


        BookingModel booking = bookingDAO.findById(bookingId, customer.getCustomerId());

        if (booking == null) {
            response.sendRedirect(request.getContextPath() + "/booking?action=list");
            return;
        }

        if (!"AWAITING_PAYMENT".equalsIgnoreCase(booking.getStatus())) {
            response.sendRedirect(request.getContextPath()
                    + "/booking?action=detail&bookingId=" + bookingId + "&paymentStatus=invalid");
            return;
        }



        if (isPaymentExpired(booking)) {
            bookingDAO.updateStatus(bookingId, "CANCELLED");

            response.sendRedirect(request.getContextPath()
                    + "/booking?action=detail&bookingId=" + bookingId + "&paymentStatus=expired");
            return;
        }

        BigDecimal total = booking.getTotalEstimatedPrice();

        BigDecimal deposit = calculateDeposit(total);
        BigDecimal remaining = calculateRemaining(total);


        request.setAttribute("booking", booking);
        request.setAttribute("depositAmount", deposit);
        request.setAttribute("remainingAmount", remaining);

        request.getRequestDispatcher("/views/payment.jsp").forward(request, response);
    }

    private void handlePayment(HttpServletRequest request, HttpServletResponse response, boolean success)
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


        BookingModel booking = bookingDAO.findById(bookingId, customer.getCustomerId());

        if (booking == null) {
            response.sendRedirect(request.getContextPath() + "/booking?action=list");
            return;
        }


        if (isPaymentExpired(booking)) {
            bookingDAO.updateStatus(bookingId, "CANCELLED");

            response.sendRedirect(request.getContextPath()
                    + "/booking?action=detail&bookingId=" + bookingId + "&paymentStatus=expired");
            return;
        }

        String paymentMethod = request.getParameter("paymentMethod");
        if (paymentMethod == null || paymentMethod.isBlank()) {
            paymentMethod = "VNPAY";
        }


        boolean processed = processSandboxPayment(bookingId, paymentMethod, success);


        if (processed && success) {
            response.sendRedirect(request.getContextPath()
                    + "/booking?action=detail&bookingId=" + bookingId + "&paymentStatus=success");
            return;
        }

        if (processed) {
            response.sendRedirect(request.getContextPath()
                    + "/payment?action=create&bookingId=" + bookingId + "&result=fail");
            return;
        }

        response.sendRedirect(request.getContextPath()
                + "/payment?action=create&bookingId=" + bookingId + "&result=invalid");
    }

    private void cancelPayment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int bookingId;
        try {
            bookingId = Integer.parseInt(request.getParameter("bookingId"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/booking?action=list");
            return;
        }

        response.sendRedirect(request.getContextPath()
                + "/booking?action=detail&bookingId=" + bookingId + "&paymentStatus=cancelled");
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


    private BigDecimal calculateDeposit(BigDecimal totalAmount) {
        if (totalAmount == null) {
            return BigDecimal.ZERO;
        }
        return totalAmount.multiply(new BigDecimal("0.30"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateRemaining(BigDecimal totalAmount) {
        if (totalAmount == null) {
            return BigDecimal.ZERO;
        }
        return totalAmount.subtract(calculateDeposit(totalAmount))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private boolean isPaymentExpired(BookingModel booking) {
        if (booking == null || booking.getPaymentDeadline() == null) {
            return false;
        }
        return booking.getPaymentDeadline().before(new Timestamp(System.currentTimeMillis()));
    }

    private boolean processSandboxPayment(int bookingId, String paymentMethod, boolean success) {
        BookingModel booking = bookingDAO.getById(bookingId);

        if (booking == null) {
            return false;
        }

        if (booking.getPaymentDeadline() != null
                && booking.getPaymentDeadline().before(new Timestamp(System.currentTimeMillis()))) {
            bookingDAO.updateStatus(bookingId, "CANCELLED");
            return false;
        }

        if (!"AWAITING_PAYMENT".equalsIgnoreCase(booking.getStatus())) {
            return false;
        }

        if (paymentDAO.hasSuccessfulDepositPayment(bookingId)) {
            return false;
        }

        BigDecimal totalAmount = booking.getTotalEstimatedPrice();
        BigDecimal depositAmount = calculateDeposit(totalAmount);
        Timestamp now = new Timestamp(System.currentTimeMillis());

        PaymentModel payment = new PaymentModel();
        payment.setBookingId(bookingId);
        payment.setAmount(depositAmount);
        payment.setPaymentType("DEPOSIT");
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus(success ? "SUCCESS" : "FAILED");
        payment.setGatewayOrderRef("SBX-BOOKING-" + bookingId + "-" + System.currentTimeMillis());
        payment.setGatewayTransactionId(success ? "TXN-" + System.currentTimeMillis() : null);
        payment.setProviderResponseCode(success ? "00" : "99");
        payment.setCallbackReceivedAt(now);
        payment.setChecksumVerified(true);
        payment.setPaidAt(success ? now : null);
        payment.setRawResponse(success
                ? "{status:'SUCCESS',message:'Sandbox payment success'}"
                : "{status:'FAILED',message:'Sandbox payment failed'}");

        boolean inserted = paymentDAO.insert(payment);
        if (!inserted) {
            return false;
        }

        if (success) {
            boolean updated = bookingDAO.updateStatus(bookingId, "PENDING_APPROVAL");

            if (updated && booking.getVoucherId() != null) {
                voucherDAO.updateVoucherQuantity(booking.getVoucherId());
            }

            return updated;
        }

        return true;
    }

    private boolean confirmBookingAfterSuccessfulPayment(int bookingId) {
        BookingModel booking = bookingDAO.getById(bookingId);

        if (booking == null) {
            return false;
        }

        if (!"AWAITING_PAYMENT".equalsIgnoreCase(booking.getStatus())) {
            return false;
        }

        if (contractDAO.existsByBookingId(bookingId)) {
            return false;
        }

        CarModel car = carDAO.findById(booking.getCarId());
        if (car == null) {
            return false;
        }

        if ("MAINTENANCE".equalsIgnoreCase(car.getStatus())) {
            return false;
        }

        if (bookingDAO.hasBookingConflictExcludeBooking(
                booking.getCarId(),
                booking.getStartDate(),
                booking.getEndDate(),
                bookingId)) {
            bookingDAO.updateStatus(bookingId, "CANCELLED");
            return false;
        }

        ContractModel contract = new ContractModel();
        contract.setBookingId(booking.getBookingId());
        contract.setCustomerId(booking.getCustomerId());
        contract.setStaffId(booking.getStaffId());
        contract.setCarId(booking.getCarId());
        contract.setContractStartDate(booking.getStartDate());
        contract.setContractEndDate(booking.getEndDate());
        contract.setContractStatus("CREATED");
        contract.setDailyPrice(car.getPricePerDay().doubleValue());

        double total = booking.getTotalEstimatedPrice().doubleValue();
        double deposit = total * 0.3;

        contract.setDepositAmount(deposit);
        contract.setTotalAmount(total);
        contract.setSignedAt(null);
        contract.setNote("Contract created automatically after successful deposit payment.");

        boolean created = contractDAO.createContract(contract);
        if (!created) {
            return false;
        }

        boolean bookingUpdated = bookingDAO.updateStatus(bookingId, "CONFIRMED");
        if (!bookingUpdated) {
            return false;
        }

        boolean carUpdated = carDAO.updateStatus(booking.getCarId(), "BOOKED");
        return carUpdated;
    }

}
