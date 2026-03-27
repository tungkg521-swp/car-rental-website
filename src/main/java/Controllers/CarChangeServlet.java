package Controllers;

import models.AccountModel;
import models.CustomerModel;
import models.StaffModel;
import service.CarChangeRequestService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

import DALs.CustomerDAO;

@WebServlet(name = "CarChangeServlet", urlPatterns = {"/car-change"})
public class CarChangeServlet extends HttpServlet {

    private CarChangeRequestService carChangeService;

    @Override
    public void init() {
        carChangeService = new CarChangeRequestService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("create".equals(action)) {
            createRequest(request, response);
        } else if ("respond".equals(action)) {
            respondRequest(request, response);
        } else if ("refund".equals(action)) {
            markRefundCompleted(request, response);
        } else if ("staffRejectRefund".equals(action)) {
            rejectAndRefundByStaff(request, response);
        }
    }

    private void createRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        StaffModel staff = (StaffModel) session.getAttribute("STAFF");
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        int staffId = staff.getStaffId();

        int bookingId;
        int newCarId;

        try {
            bookingId = Integer.parseInt(request.getParameter("bookingId"));
            newCarId = Integer.parseInt(request.getParameter("newCarId"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/staff/bookings");
            return;
        }

        String reason = request.getParameter("reason");

        boolean result = carChangeService.createStaffRequest(bookingId, staffId, newCarId, reason);

        response.sendRedirect(
                request.getContextPath()
                + "/staff/bookings?action=detail&id=" + bookingId
                + "&changeRequest=" + (result ? "success" : "fail")
        );
    }

    private void respondRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        AccountModel account = (AccountModel) request.getSession().getAttribute("ACCOUNT");
        if (account == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        HttpSession session = request.getSession(false);
        account = (AccountModel) session.getAttribute("ACCOUNT");
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

        int customerId = customer.getCustomerId();
        String requestIdRaw = request.getParameter("requestId");
        int requestId;

        try {
            requestId = Integer.parseInt(requestIdRaw);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/customer/bookings?action=list");
            return;
        }
        String decision = request.getParameter("decision");

        boolean accept = "accept".equalsIgnoreCase(decision);
        boolean result = carChangeService.customerRespond(requestId, customerId, accept);

        String bookingId = request.getParameter("bookingId");

        response.sendRedirect(
                request.getContextPath()
                + "/customer/bookings?action=detail&bookingId=" + bookingId
                + "&changeResponse=" + (result ? "success" : "fail")
        );
    }
    
    private void markRefundCompleted(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("STAFF") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    int bookingId;
    try {
        bookingId = Integer.parseInt(request.getParameter("bookingId"));
    } catch (NumberFormatException e) {
        response.sendRedirect(request.getContextPath() + "/staff/bookings");
        return;
    }

    boolean result = carChangeService.markRefundCompleted(bookingId);

    response.sendRedirect(
            request.getContextPath()
            + "/staff/bookings?action=detail&id=" + bookingId
            + "&refundStatus=" + (result ? "success" : "fail")
    );
}
    
    private void rejectAndRefundByStaff(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("STAFF") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    int bookingId;
    try {
        bookingId = Integer.parseInt(request.getParameter("bookingId"));
    } catch (NumberFormatException e) {
        response.sendRedirect(request.getContextPath() + "/staff/bookings");
        return;
    }

    boolean result = carChangeService.rejectAndRefundByStaff(bookingId);

    response.sendRedirect(
            request.getContextPath()
            + "/staff/bookings?action=detail&id=" + bookingId
            + "&refundStatus=" + (result ? "success" : "fail")
    );
}
}
