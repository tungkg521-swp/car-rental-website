package Controllers;

import DALs.BookingDAO;
import DALs.CarChangeRequestDAO;
import DALs.CarDAO;
import DALs.ContractDAO;
import models.AccountModel;
import models.CustomerModel;
import models.StaffModel;
import service.CarChangeRequestService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

import DALs.CustomerDAO;
import java.util.List;
import models.BookingModel;
import models.CarChangeRequestModel;
import models.CarModel;
import models.ContractModel;

@WebServlet(name = "CarChangeServlet", urlPatterns = {"/car-change"})
public class CarChangeServlet extends HttpServlet {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final CarDAO carDAO = new CarDAO();
    private final ContractDAO contractDAO = new ContractDAO();
    private final CarChangeRequestDAO requestDAO = new CarChangeRequestDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

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

        boolean result = createStaffRequest(bookingId, staffId, newCarId, reason);

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
        boolean result = customerRespond(requestId, customerId, accept);

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

        boolean result = markRefundCompletedInternal(bookingId);

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

        boolean result = rejectAndRefundByStaffInternal(bookingId);

        response.sendRedirect(
                request.getContextPath()
                + "/staff/bookings?action=detail&id=" + bookingId
                + "&refundStatus=" + (result ? "success" : "fail")
        );
    }

    private List<CarModel> getAvailableReplacementCars(int bookingId) {
        BookingModel booking = bookingDAO.getById(bookingId);
        if (booking == null) {
            return java.util.Collections.emptyList();
        }

        CarModel oldCar = carDAO.findById(booking.getCarId());
        if (oldCar == null) {
            return java.util.Collections.emptyList();
        }

        return carDAO.getAvailableReplacementCars(
                oldCar.getCarId(),
                oldCar.getTypeName(),
                oldCar.getPricePerDay(),
                booking.getStartDate(),
                booking.getEndDate()
        );
    }

    private boolean createStaffRequest(int bookingId, int staffId, int newCarId, String reason) {
        BookingModel booking = bookingDAO.getById(bookingId);
        if (booking == null) {
            return false;
        }

        if (!"AWAITING_PAYMENT".equalsIgnoreCase(booking.getStatus())
                && !"CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
            return false;
        }

        if (requestDAO.existsPendingRequest(bookingId)) {
            return false;
        }

        CarModel oldCar = carDAO.findById(booking.getCarId());
        if (oldCar == null) {
            return false;
        }

        List<CarModel> replacementCars = carDAO.getAvailableReplacementCars(
                oldCar.getCarId(),
                oldCar.getTypeName(),
                oldCar.getPricePerDay(),
                booking.getStartDate(),
                booking.getEndDate()
        );

        boolean found = false;
        for (CarModel car : replacementCars) {
            if (car.getCarId() == newCarId) {
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        CarChangeRequestModel request = new CarChangeRequestModel();
        request.setBookingId(bookingId);
        request.setOldCarId(oldCar.getCarId());
        request.setNewCarId(newCarId);
        request.setRequestedBy("STAFF");
        request.setStatus("PENDING");
        request.setReason(reason);

        return requestDAO.createStaffRequest(request) > 0;
    }

    private boolean customerRespond(int requestId, int customerId, boolean accept) {
        CarChangeRequestModel request = requestDAO.getById(requestId);
        if (request == null) {
            return false;
        }

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            return false;
        }

        BookingModel booking = bookingDAO.findByIdForCarChange(request.getBookingId(), customerId);
        if (booking == null) {
            return false;
        }

        if (!accept) {
            boolean requestUpdated = requestDAO.updateStatus(requestId, "REJECTED");
            if (!requestUpdated) {
                return false;
            }

            if ("CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
                ContractModel contract = contractDAO.getContractByBookingId(booking.getBookingId());

                if (contract != null && "CREATED".equalsIgnoreCase(contract.getContractStatus())) {
                    boolean contractCancelled = contractDAO.updateContractStatus(contract.getContractId(), "CANCELLED");
                    if (!contractCancelled) {
                        return false;
                    }
                }

                boolean bookingUpdated = bookingDAO.updateStatus(booking.getBookingId(), "REFUND_PENDING");
                if (!bookingUpdated) {
                    return false;
                }
            } else if ("AWAITING_PAYMENT".equalsIgnoreCase(booking.getStatus())) {
                boolean bookingUpdated = bookingDAO.updateStatus(booking.getBookingId(), "CANCELLED");
                if (!bookingUpdated) {
                    return false;
                }
            }

            return true;
        }

        boolean valid = isReplacementStillValid(booking, request.getNewCarId());
        if (!valid) {
            requestDAO.updateStatus(requestId, "CANCELLED");
            return false;
        }

        boolean applied = applyCarChange(booking, request.getNewCarId());
        if (!applied) {
            return false;
        }

        return requestDAO.updateStatus(requestId, "APPROVED");
    }

    private boolean isReplacementStillValid(BookingModel booking, int newCarId) {
        CarModel oldCar = carDAO.findById(booking.getCarId());
        if (oldCar == null) {
            return false;
        }

        List<CarModel> replacementCars = carDAO.getAvailableReplacementCars(
                oldCar.getCarId(),
                oldCar.getTypeName(),
                oldCar.getPricePerDay(),
                booking.getStartDate(),
                booking.getEndDate()
        );

        for (CarModel car : replacementCars) {
            if (car.getCarId() == newCarId) {
                return true;
            }
        }

        return false;
    }

    private boolean applyCarChange(BookingModel booking, int newCarId) {
        int oldCarId = booking.getCarId();

        boolean bookingUpdated = bookingDAO.updateCarId(booking.getBookingId(), newCarId);
        if (!bookingUpdated) {
            return false;
        }

        if ("CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
            boolean oldCarUpdated = carDAO.updateStatus(oldCarId, "AVAILABLE");
            if (!oldCarUpdated) {
                return false;
            }

            boolean newCarUpdated = carDAO.updateStatus(newCarId, "BOOKED");
            if (!newCarUpdated) {
                return false;
            }

            ContractModel contract = contractDAO.getContractByBookingId(booking.getBookingId());
            if (contract != null && "CREATED".equalsIgnoreCase(contract.getContractStatus())) {
                boolean contractUpdated = contractDAO.updateCarId(contract.getContractId(), newCarId);
                if (!contractUpdated) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean markRefundCompletedInternal(int bookingId) {
        BookingModel booking = bookingDAO.getById(bookingId);
        if (booking == null) {
            return false;
        }

        if (!"REFUND_PENDING".equalsIgnoreCase(booking.getStatus())) {
            return false;
        }

        boolean bookingUpdated = bookingDAO.updateStatus(bookingId, "REFUNDED");
        if (!bookingUpdated) {
            return false;
        }

        return carDAO.updateStatus(booking.getCarId(), "AVAILABLE");
    }

    private boolean rejectAndRefundByStaffInternal(int bookingId) {
        BookingModel booking = bookingDAO.getById(bookingId);
        if (booking == null) {
            return false;
        }

        ContractModel contract = contractDAO.getContractByBookingId(bookingId);
        if (contract != null && "CREATED".equalsIgnoreCase(contract.getContractStatus())) {
            boolean contractCancelled = contractDAO.updateContractStatus(contract.getContractId(), "CANCELLED");
            if (!contractCancelled) {
                return false;
            }
        }

        boolean bookingUpdated = bookingDAO.updateStatus(bookingId, "REFUNDED");
        if (!bookingUpdated) {
            return false;
        }

        boolean carUpdated = carDAO.updateStatus(booking.getCarId(), "AVAILABLE");
        if (!carUpdated) {
            return false;
        }

        CarChangeRequestModel pending = requestDAO.getPendingByBookingId(bookingId);
        if (pending != null) {
            requestDAO.updateStatus(pending.getRequestId(), "REJECTED");
        }

        return true;
    }
}
