package Controllers;

import DALs.BookingDAO;
import DALs.CarChangeRequestDAO;
import DALs.CarDAO;
import DALs.CustomerDAO;
import DALs.VoucherDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import models.AccountModel;
import models.BookingModel;
import models.CarModel;
import models.CustomerModel;
import models.VoucherModel;
import service.BookingService;
import service.CarService;
import service.VoucherService;
import java.math.RoundingMode;
import java.util.ArrayList;
import models.CarChangeRequestModel;
import service.CarChangeRequestService;

public class BookingServlet extends HttpServlet {
    

    private final BookingDAO bookingDAO = new BookingDAO();
    private final CarDAO carDAO = new CarDAO();
    private final VoucherDAO voucherDAO = new VoucherDAO();
    private final CarChangeRequestDAO carChangeRequestDAO = new CarChangeRequestDAO();

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
            case "list":
                viewBookingList(request, response);
                break;
            case "detail":
                viewBookingDetail(request, response);
                break;
            case "success":
                showBookingSuccess(request, response);
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
            case "delete":
                deleteBooking(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/cars");
                break;
        }
    }

    private void showCreateBooking(HttpServletRequest request, HttpServletResponse response)
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

        String startDateRaw = request.getParameter("startDate");
        String endDateRaw = request.getParameter("endDate");
        if (startDateRaw == null || endDateRaw == null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
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

        CarDAO carDAO = new CarDAO();
        CarModel car = carDAO.findById(carId);

        if (!customer.isLicenseVerified()) {
            request.setAttribute("LICENSE_REQUIRED", true);
            request.setAttribute("car", car);
            request.setAttribute("startDate", startDateRaw);
            request.setAttribute("endDate", endDateRaw);
            request.getRequestDispatcher("/views/car-detail.jsp").forward(request, response);
            return;
        }

        Date startDate;
        Date endDate;
        try {
            startDate = Date.valueOf(startDateRaw);
            endDate = Date.valueOf(endDateRaw);
        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/car-detail?carId=" + carId);
            return;
        }
        if (bookingDAO.hasBookingConflict(carId, startDate, endDate)) {
            request.setAttribute("BOOKING_ERROR", "Xe đang bận trong khoảng thời gian bạn chọn. Vui lòng chọn lịch khác.");
            request.setAttribute("car", car);
            request.setAttribute("startDate", startDateRaw);
            request.setAttribute("endDate", endDateRaw);

            List<Date[]> busyRanges = bookingDAO.getBusyDateRangesByCarId(carId);
            List<String> busyDates = new ArrayList<>();
            for (Date[] range : busyRanges) {
                LocalDate d = range[0].toLocalDate();
                LocalDate end = range[1].toLocalDate();
                while (!d.isAfter(end)) {
                    busyDates.add(d.toString());
                    d = d.plusDays(1);
                }
            }

            StringBuilder busyDatesJson = new StringBuilder("[");
            for (int i = 0; i < busyDates.size(); i++) {
                busyDatesJson.append("\"").append(busyDates.get(i)).append("\"");
                if (i < busyDates.size() - 1) {
                    busyDatesJson.append(",");
                }
            }
            busyDatesJson.append("]");

            request.setAttribute("busyDatesJson", busyDatesJson.toString());
            request.getRequestDispatcher("/views/car-detail.jsp").forward(request, response);
            return;
        }

        if (car == null) {
            response.sendRedirect(request.getContextPath() + "/cars");
            return;
        }

        if (!"AVAILABLE".equalsIgnoreCase(car.getStatus())) {
            response.sendRedirect(request.getContextPath() + "/car-detail?carId=" + carId);
            return;
        }

        List<VoucherModel> validVouchers = voucherDAO.getActiveVouchers();

        request.setAttribute("account", account);
        request.setAttribute("customer", customer);
        request.setAttribute("car", car);
        request.setAttribute("vouchers", validVouchers);
        request.setAttribute("startDate", startDateRaw);
        request.setAttribute("endDate", endDateRaw);

        request.getRequestDispatcher("/views/booking.jsp").forward(request, response);
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

        if (!customer.isLicenseVerified()) {
            response.sendRedirect(request.getContextPath() + "/cars");
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

        Integer voucherId = null;
        String voucherIdRaw = request.getParameter("voucherId");
        if (voucherIdRaw != null && !voucherIdRaw.trim().isEmpty()) {
            try {
                voucherId = Integer.parseInt(voucherIdRaw);
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/cars");
                return;
            }
        }

        Date startDate;
        Date endDate;

        try {
            startDate = Date.valueOf(startDateRaw);
            endDate = Date.valueOf(endDateRaw);
        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/cars");
            return;
        }

        Date today = Date.valueOf(LocalDate.now());

        CarDAO carDAO = new CarDAO();
        CarModel car = carDAO.findById(carId);

        if (car == null) {
            response.sendRedirect(request.getContextPath() + "/cars");
            return;
        }

        if (startDate.before(today)) {
            request.setAttribute("errorMessage", "Không thể đặt xe trong ngày quá khứ");
            request.setAttribute("car", car);
            request.setAttribute("customer", customer);
            request.setAttribute("vouchers", voucherDAO.getActiveVouchers());
            request.setAttribute("startDate", startDateRaw);
            request.setAttribute("endDate", endDateRaw);
            request.getRequestDispatcher("/views/booking.jsp").forward(request, response);
            return;
        }

        if (!endDate.after(startDate)) {
            request.setAttribute("errorMessage", "Ngày trả xe phải sau ngày nhận xe");
            request.setAttribute("car", car);
            request.setAttribute("customer", customer);
            request.setAttribute("vouchers", voucherDAO.getActiveVouchers());
            request.setAttribute("startDate", startDateRaw);
            request.setAttribute("endDate", endDateRaw);
            request.getRequestDispatcher("/views/booking.jsp").forward(request, response);
            return;
        }

        if ("MAINTENANCE".equalsIgnoreCase(car.getStatus())) {
            response.sendRedirect(request.getContextPath() + "/cars");
            return;
        }

        if (bookingDAO.hasBookingConflict(carId, startDate, endDate)) {
            request.setAttribute("errorMessage", "Xe không khả dụng trong khoảng thời gian đã chọn.");
            request.setAttribute("car", car);
            request.setAttribute("customer", customer);
            request.setAttribute("vouchers", voucherDAO.getActiveVouchers());
            request.setAttribute("startDate", startDateRaw);
            request.setAttribute("endDate", endDateRaw);
            request.getRequestDispatcher("/views/booking.jsp").forward(request, response);
            return;
        }

        BigDecimal totalPrice = calculateTotalPrice(startDate, endDate, car.getPricePerDay());

        if (voucherId != null) {
            VoucherModel voucher = voucherDAO.findById(voucherId);

            if (voucher == null || !"ACTIVE".equalsIgnoreCase(voucher.getStatus())) {
                request.setAttribute("errorMessage", "Voucher không hợp lệ.");
                request.setAttribute("car", car);
                request.setAttribute("customer", customer);
                request.setAttribute("vouchers", voucherDAO.getActiveVouchers());
                request.setAttribute("startDate", startDateRaw);
                request.setAttribute("endDate", endDateRaw);
                request.getRequestDispatcher("/views/booking.jsp").forward(request, response);
                return;
            }

            if (voucher.getMinBookingAmount() != null
                    && totalPrice.compareTo(voucher.getMinBookingAmount()) < 0) {
                request.setAttribute("errorMessage", "Đơn thuê chưa đủ điều kiện áp dụng voucher.");
                request.setAttribute("car", car);
                request.setAttribute("customer", customer);
                request.setAttribute("vouchers", voucherDAO.getActiveVouchers());
                request.setAttribute("startDate", startDateRaw);
                request.setAttribute("endDate", endDateRaw);
                request.getRequestDispatcher("/views/booking.jsp").forward(request, response);
                return;
            }

            if ("PERCENT".equalsIgnoreCase(voucher.getType())) {
                BigDecimal discount = totalPrice
                        .multiply(voucher.getDiscount())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                totalPrice = totalPrice.subtract(discount);
            } else {
                totalPrice = totalPrice.subtract(voucher.getDiscount());
            }

            if (totalPrice.compareTo(BigDecimal.ZERO) < 0) {
                totalPrice = BigDecimal.ZERO;
            }
        }

        BigDecimal depositAmount = totalPrice
                .multiply(new BigDecimal("0.30"))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal remainingAmount = totalPrice
                .subtract(depositAmount)
                .setScale(2, RoundingMode.HALF_UP);

        BookingModel booking = new BookingModel();
        booking.setCustomerId(customer.getCustomerId());
        booking.setCarId(carId);
        booking.setVoucherId(voucherId);
        booking.setBookingDate(new Timestamp(System.currentTimeMillis()));
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setStatus("PENDING_APPROVAL");

        booking.setNote(note);
        booking.setDepositAmount(depositAmount);
        booking.setRemainingAmount(remainingAmount);
        booking.setPaymentDeadline(null);
        booking.setTotalEstimatedPrice(totalPrice);

        try {
            int bookingId = bookingDAO.insert(booking);
            session.setAttribute("LAST_BOOKING", bookingId);

            response.sendRedirect(
                    request.getContextPath() + "/booking?action=detail&bookingId=" + bookingId + "&created=1"
            );
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(
                    request.getContextPath()
                    + "/booking?action=create&carId=" + carId
                    + "&startDate=" + startDateRaw
                    + "&endDate=" + endDateRaw
            );
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

        AccountModel account = (AccountModel) session.getAttribute("ACCOUNT");
        if (account == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        CustomerModel customer = new CustomerDAO().getByAccountId(account.getAccountId());
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<BookingModel> bookings = bookingDAO.findByCustomerId(customer.getCustomerId());

        request.setAttribute("BOOKINGS", bookings);
        request.getRequestDispatcher("/views/booking-list.jsp").forward(request, response);
    }

    private void viewBookingDetail(HttpServletRequest request,
            HttpServletResponse response)
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

        CustomerModel customer = new CustomerDAO().getByAccountId(account.getAccountId());
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

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

        BookingModel booking = bookingDAO.findById(bookingId, customer.getCustomerId());

        if (booking == null) {
            request.setAttribute("error", "Booking not found");
            request.getRequestDispatcher("/views/error.jsp").forward(request, response);
            return;
        }

        CarChangeRequestModel pendingRequest
                = carChangeRequestDAO.getPendingByBookingId(bookingId);

        request.setAttribute("pendingCarChangeRequest", pendingRequest);

        String cancelStatus = request.getParameter("cancelStatus");
        request.setAttribute("cancelStatus", cancelStatus);
        request.setAttribute("booking", booking);

        request.getRequestDispatcher("/views/booking-detail.jsp").forward(request, response);
    }

    private void cancelBooking(HttpServletRequest request,
            HttpServletResponse response)
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

        CustomerModel customer = new CustomerDAO().getByAccountId(account.getAccountId());
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String bookingIdRaw = request.getParameter("bookingId");
        if (bookingIdRaw == null) {
            response.sendRedirect(request.getContextPath() + "/booking?action=list");
            return;
        }

        int bookingId;
        try {
            bookingId = Integer.parseInt(bookingIdRaw);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/booking?action=list");
            return;
        }

        boolean success = cancelBookingByCustomer(bookingId, customer.getCustomerId());

        if (success) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/booking?action=detail&bookingId=" + bookingId
                    + "&cancelStatus=success"
            );
        } else {
            response.sendRedirect(
                    request.getContextPath()
                    + "/booking?action=detail&bookingId=" + bookingId
                    + "&cancelStatus=fail"
            );
        }
    }

    private void showBookingSuccess(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        session.removeAttribute("LAST_BOOKING");
        request.getRequestDispatcher("/views/booking-success.jsp").forward(request, response);
    }

    private void deleteBooking(HttpServletRequest request,
            HttpServletResponse response)
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

        CustomerModel customer = new CustomerDAO().getByAccountId(account.getAccountId());
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String bookingIdRaw = request.getParameter("bookingId");
        if (bookingIdRaw == null) {
            response.sendRedirect(request.getContextPath() + "/booking?action=list");
            return;
        }

        int bookingId;
        try {
            bookingId = Integer.parseInt(bookingIdRaw);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/booking?action=list");
            return;
        }

        boolean success = deleteCancelledBookingByCustomer(bookingId, customer.getCustomerId());

        if (success) {
            response.sendRedirect(request.getContextPath() + "/booking?action=list");
        } else {
            response.sendRedirect(
                    request.getContextPath()
                    + "/booking?action=detail&bookingId=" + bookingId
            );
        }
    }

    private BigDecimal calculateTotalPrice(Date startDate, Date endDate, BigDecimal pricePerDay) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(
                startDate.toLocalDate(),
                endDate.toLocalDate()
        );

        if (days < 1) {
            days = 1;
        }

        return pricePerDay.multiply(BigDecimal.valueOf(days));
    }

    private boolean cancelBookingByCustomer(int bookingId, int customerId) {
        BookingModel booking = bookingDAO.findById(bookingId, customerId);

        if (booking == null) {
            return false;
        }

        if (!"PENDING_APPROVAL".equalsIgnoreCase(booking.getStatus())
                && !"AWAITING_PAYMENT".equalsIgnoreCase(booking.getStatus())) {
            return false;
        }

        return bookingDAO.updateStatus(bookingId, "CANCELLED");
    }
    
    
    private boolean deleteCancelledBookingByCustomer(int bookingId, int customerId) {
    BookingModel booking = bookingDAO.findById(bookingId, customerId);

    if (booking == null) {
        return false;
    }

    if (!"CANCELLED".equalsIgnoreCase(booking.getStatus())) {
        return false;
    }

    return bookingDAO.deleteBooking(bookingId, customerId);
}
}
