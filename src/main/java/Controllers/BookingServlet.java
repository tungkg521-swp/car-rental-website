package Controllers;

import DALs.BookingDAO;
import DALs.CarChangeRequestDAO;
import DALs.CarCheckDAO;
import DALs.CarDAO;
import DALs.ContractDAO;
import DALs.CustomerDAO;
import DALs.VoucherDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import models.AccountModel;
import models.BookingModel;
import models.CarChangeRequestModel;
import models.CarCheckModel;
import models.CarModel;
import models.ContractModel;
import models.CustomerModel;
import models.VoucherModel;

public class BookingServlet extends HttpServlet {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final CarDAO carDAO = new CarDAO();
    private final VoucherDAO voucherDAO = new VoucherDAO();
    private final CarChangeRequestDAO carChangeRequestDAO = new CarChangeRequestDAO();
    private final ContractDAO contractDAO = new ContractDAO();
    private final CarCheckDAO carCheckDAO = new CarCheckDAO();

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
            case "customerCheck":
                submitCustomerCheck(request, response);
                break;
            case "confirmHandover":
                confirmHandover(request, response);
                break;
            case "rejectHandover":
                rejectHandover(request, response);
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

        CarModel car = carDAO.findById(carId);
        if (car == null) {
            response.sendRedirect(request.getContextPath() + "/cars");
            return;
        }

        if (!customer.isLicenseVerified()) {
            request.setAttribute("LICENSE_REQUIRED", true);
            request.setAttribute("car", car);
            request.setAttribute("startDate", startDateRaw);
            request.setAttribute("endDate", endDateRaw);
            request.getRequestDispatcher("/views/car-detail.jsp").forward(request, response);
            return;
        }

        Timestamp startTime = resolveStartTime(request);
        Timestamp endTime = resolveEndTime(request);

        if (startTime == null || endTime == null) {
            response.sendRedirect(request.getContextPath() + "/car-detail?carId=" + carId);
            return;
        }

        String normalizedStartValue = buildDateTimeLocalValue(startTime);
        String normalizedEndValue = buildDateTimeLocalValue(endTime);

        Timestamp now = new Timestamp(System.currentTimeMillis());

        if (startTime.before(now) || !endTime.after(startTime)) {
            response.sendRedirect(request.getContextPath() + "/car-detail?carId=" + carId);
            return;
        }

        if (bookingDAO.hasBookingConflict(carId, startTime, endTime)) {

            request.setAttribute("BOOKING_ERROR", "Xe đang bận trong khoảng thời gian bạn chọn. Vui lòng chọn lịch khác.");
            request.setAttribute("car", car);
            request.setAttribute("startDate", startDateRaw);
            request.setAttribute("endDate", endDateRaw);

            List<Timestamp[]> busyRanges = bookingDAO.getBusyDateRangesByCarId(carId);

            List<String> busyDates = new ArrayList<>();
            StringBuilder busyTimeRangesJson = new StringBuilder("[");

            for (int i = 0; i < busyRanges.size(); i++) {
                Timestamp[] range = busyRanges.get(i);

                LocalDateTime start = range[0].toLocalDateTime();
                LocalDateTime end = range[1].toLocalDateTime();

                LocalDateTime current = start;
                while (!current.toLocalDate().isAfter(end.toLocalDate())) {
                    busyDates.add(current.toLocalDate().toString());
                    current = current.plusDays(1);
                }

                busyTimeRangesJson.append("{")
                        .append("\"start\":\"").append(start.toString().replace(" ", "T")).append("\",")
                        .append("\"end\":\"").append(end.toString().replace(" ", "T")).append("\"")
                        .append("}");

                if (i < busyRanges.size() - 1) {
                    busyTimeRangesJson.append(",");
                }
            }
            busyTimeRangesJson.append("]");

            StringBuilder busyDatesJson = new StringBuilder("[");
            for (int i = 0; i < busyDates.size(); i++) {
                busyDatesJson.append("\"").append(busyDates.get(i)).append("\"");
                if (i < busyDates.size() - 1) {
                    busyDatesJson.append(",");
                }
            }
            busyDatesJson.append("]");

            request.setAttribute("busyDatesJson", busyDatesJson.toString());
            request.setAttribute("busyTimeRangesJson", busyTimeRangesJson.toString());
            request.getRequestDispatcher("/views/car-detail.jsp").forward(request, response);
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
        request.setAttribute("startDate", normalizedStartValue);
        request.setAttribute("endDate", normalizedEndValue);

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

        Timestamp startTime = resolveStartTime(request);
        Timestamp endTime = resolveEndTime(request);

        if (startTime == null || endTime == null) {
            response.sendRedirect(request.getContextPath() + "/cars");
            return;
        }

        String normalizedStartValue = buildDateTimeLocalValue(startTime);
        String normalizedEndValue = buildDateTimeLocalValue(endTime);

        Timestamp now = new Timestamp(System.currentTimeMillis());

        CarModel car = carDAO.findById(carId);
        if (car == null) {
            response.sendRedirect(request.getContextPath() + "/cars");
            return;
        }

        if (startTime.before(now)) {
            request.setAttribute("errorMessage", "Không thể đặt xe ở thời điểm quá khứ");
            request.setAttribute("car", car);
            request.setAttribute("customer", customer);
            request.setAttribute("vouchers", voucherDAO.getActiveVouchers());
            request.setAttribute("startDate", normalizedStartValue);
            request.setAttribute("endDate", normalizedEndValue);
            request.getRequestDispatcher("/views/booking.jsp").forward(request, response);
            return;
        }

        if (!endTime.after(startTime)) {
            request.setAttribute("errorMessage", "Thời gian trả xe phải sau thời gian nhận xe");
            request.setAttribute("car", car);
            request.setAttribute("customer", customer);
            request.setAttribute("vouchers", voucherDAO.getActiveVouchers());
            request.setAttribute("startDate", normalizedStartValue);
            request.setAttribute("endDate", normalizedEndValue);
            request.getRequestDispatcher("/views/booking.jsp").forward(request, response);
            return;
        }

        long rentalMinutes = ChronoUnit.MINUTES.between(
                startTime.toLocalDateTime(),
                endTime.toLocalDateTime()
        );

        if (rentalMinutes < 60) {
            request.setAttribute("errorMessage", "Thời gian thuê tối thiểu là 1 giờ");
            request.setAttribute("car", car);
            request.setAttribute("customer", customer);
            request.setAttribute("vouchers", voucherDAO.getActiveVouchers());
            request.setAttribute("startDate", normalizedStartValue);
            request.setAttribute("endDate", normalizedEndValue);
            request.getRequestDispatcher("/views/booking.jsp").forward(request, response);
            return;
        }

        if ("MAINTENANCE".equalsIgnoreCase(car.getStatus())) {
            response.sendRedirect(request.getContextPath() + "/cars");
            return;
        }

        if (bookingDAO.hasBookingConflict(carId, startTime, endTime)) {
            request.setAttribute("errorMessage", "Xe không khả dụng trong khoảng thời gian đã chọn.");
            request.setAttribute("car", car);
            request.setAttribute("customer", customer);
            request.setAttribute("vouchers", voucherDAO.getActiveVouchers());
            request.setAttribute("startDate", normalizedStartValue);
            request.setAttribute("endDate", normalizedEndValue);
            request.getRequestDispatcher("/views/booking.jsp").forward(request, response);
            return;
        }

        BigDecimal totalPrice = calculateTotalPrice(startTime, endTime, car.getPricePerDay());

        if (voucherId != null) {
            VoucherModel voucher = voucherDAO.findById(voucherId);

            if (voucher == null || !"ACTIVE".equalsIgnoreCase(voucher.getStatus())) {
                request.setAttribute("errorMessage", "Voucher không hợp lệ.");
                request.setAttribute("car", car);
                request.setAttribute("customer", customer);
                request.setAttribute("vouchers", voucherDAO.getActiveVouchers());
                request.setAttribute("startDate", normalizedStartValue);
                request.setAttribute("endDate", normalizedEndValue);
                request.getRequestDispatcher("/views/booking.jsp").forward(request, response);
                return;
            }

            if (voucher.getMinBookingAmount() != null
                    && totalPrice.compareTo(voucher.getMinBookingAmount()) < 0) {
                request.setAttribute("errorMessage", "Đơn thuê chưa đủ điều kiện áp dụng voucher.");
                request.setAttribute("car", car);
                request.setAttribute("customer", customer);
                request.setAttribute("vouchers", voucherDAO.getActiveVouchers());
                request.setAttribute("startDate", normalizedStartValue);
                request.setAttribute("endDate", normalizedEndValue);
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

        BigDecimal depositAmount = BigDecimal.valueOf(10_000_000L);
        

        BigDecimal remainingAmount = totalPrice
                .subtract(depositAmount)
                .setScale(2, RoundingMode.HALF_UP);

        BookingModel booking = new BookingModel();
        booking.setCustomerId(customer.getCustomerId());
        booking.setCarId(carId);
        booking.setVoucherId(voucherId);
        booking.setBookingDate(new Timestamp(System.currentTimeMillis()));
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setStatus("AWAITING_PAYMENT");
        booking.setNote(note);
        booking.setDepositAmount(depositAmount);
        booking.setRemainingAmount(remainingAmount);
        booking.setPaymentDeadline(null);
        booking.setTotalEstimatedPrice(totalPrice);

        try {
            int bookingId = bookingDAO.insert(booking);
            session.setAttribute("LAST_BOOKING", bookingId);

            response.sendRedirect(
                    request.getContextPath() + "/payment?action=create&bookingId=" + bookingId + "&created=1"
            );
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(
                    request.getContextPath()
                    + "/booking?action=create&carId=" + carId
                    + "&startDate=" + normalizedStartValue
                    + "&endDate=" + normalizedEndValue
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

        ContractModel contract = contractDAO.getContractByBookingId(bookingId);

        CarCheckModel handoverCheck = null;
        boolean canCustomerConfirm = false;
        String rentalDurationText = "";

        if (contract != null) {
            rentalDurationText = buildRentalDurationText(
                    contract.getContractStartTime(),
                    contract.getContractEndTime()
            );

            if (contract.getHandoverCheckId() != null) {
                handoverCheck = carCheckDAO.getCheckById(contract.getHandoverCheckId());
            }

            if ("WAITING_CUSTOMER_CONFIRM".equalsIgnoreCase(contract.getContractStatus())
                    && contract.getCustomerConfirmed() == null
                    && handoverCheck != null) {
                canCustomerConfirm = true;
            }
        }

        request.setAttribute("contract", contract);
        request.setAttribute("handoverCheck", handoverCheck);
        request.setAttribute("canCustomerConfirm", canCustomerConfirm);
        request.setAttribute("rentalDurationText", rentalDurationText);

        CarChangeRequestModel pendingRequest = carChangeRequestDAO.getPendingByBookingId(bookingId);

        request.setAttribute("pendingCarChangeRequest", pendingRequest);

        if (pendingRequest != null) {
            CarModel oldCar = carDAO.findById(pendingRequest.getOldCarId());
            CarModel newCar = carDAO.findById(pendingRequest.getNewCarId());

            request.setAttribute("oldCarChangeCar", oldCar);
            request.setAttribute("newCarChangeCar", newCar);
        }

        String cancelStatus = request.getParameter("cancelStatus");
        request.setAttribute("cancelStatus", cancelStatus);
        request.setAttribute("booking", booking);
        
        session.removeAttribute("handoverStatus");

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

    private void submitCustomerCheck(HttpServletRequest request,
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
        String decision = request.getParameter("decision");
        String[] reasons = request.getParameterValues("reason");
        String note = request.getParameter("note");

        int bookingId;
        try {
            bookingId = Integer.parseInt(bookingIdRaw);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/booking?action=list");
            return;
        }

        BookingModel booking = bookingDAO.findById(bookingId, customer.getCustomerId());
        if (booking == null) {
            session.setAttribute("error", "Booking not found.");
            response.sendRedirect(request.getContextPath() + "/booking?action=list");
            return;
        }

        if (!"CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
            session.setAttribute("error", "This booking is not ready for customer check.");
            response.sendRedirect(request.getContextPath() + "/booking?action=detail&bookingId=" + bookingId);
            return;
        }

        if (booking.getCustomerCheckStatus() != null && !booking.getCustomerCheckStatus().trim().isEmpty()) {
            session.setAttribute("error", "You have already submitted your vehicle check.");
            response.sendRedirect(request.getContextPath() + "/booking?action=detail&bookingId=" + bookingId);
            return;
        }

        ContractModel contract = contractDAO.getContractByBookingId(bookingId);
        if (contract == null) {
            session.setAttribute("error", "Contract not found.");
            response.sendRedirect(request.getContextPath() + "/booking?action=detail&bookingId=" + bookingId);
            return;
        }

        CarCheckModel latestCarCheck = carCheckDAO.getLatestCheckByContractId(contract.getContractId());
        if (latestCarCheck == null || !"OK".equalsIgnoreCase(latestCarCheck.getCheckResult())) {
            session.setAttribute("error", "Staff has not completed a valid pre-check yet.");
            response.sendRedirect(request.getContextPath() + "/booking?action=detail&bookingId=" + bookingId);
            return;
        }

        if (decision == null || decision.trim().isEmpty()) {
            session.setAttribute("error", "Please choose your vehicle check decision.");
            response.sendRedirect(request.getContextPath() + "/booking?action=detail&bookingId=" + bookingId);
            return;
        }

        if (!"ACCEPTED".equalsIgnoreCase(decision)
                && !"REJECTED".equalsIgnoreCase(decision)
                && !"NEED_SUPPORT".equalsIgnoreCase(decision)) {
            session.setAttribute("error", "Invalid vehicle check decision.");
            response.sendRedirect(request.getContextPath() + "/booking?action=detail&bookingId=" + bookingId);
            return;
        }

        String reasonText = null;
        if (reasons != null && reasons.length > 0) {
            reasonText = String.join(",", reasons);
        }

        boolean success = bookingDAO.updateCustomerCheck(
                bookingId,
                customer.getCustomerId(),
                decision,
                reasonText,
                note
        );

        if (success) {
            session.setAttribute("message", "Your vehicle check has been submitted successfully.");
        } else {
            session.setAttribute("error", "Failed to submit your vehicle check.");
        }

        response.sendRedirect(request.getContextPath() + "/booking?action=detail&bookingId=" + bookingId);
    }

    private void confirmHandover(HttpServletRequest request,
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
        String contractIdRaw = request.getParameter("contractId");
        String customerNote = request.getParameter("customerNote");

        int bookingId;
        int contractId;

        try {
            bookingId = Integer.parseInt(bookingIdRaw);
            contractId = Integer.parseInt(contractIdRaw);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/booking?action=list");
            return;
        }

        BookingModel booking = bookingDAO.findById(bookingId, customer.getCustomerId());
        if (booking == null) {
            session.setAttribute("error", "Booking not found.");
            response.sendRedirect(request.getContextPath() + "/booking?action=list");
            return;
        }

        ContractModel contract = contractDAO.getContractById(contractId);
        if (contract == null || contract.getCustomerId() != customer.getCustomerId()) {
            session.setAttribute("error", "Contract not found.");
            response.sendRedirect(request.getContextPath() + "/booking?action=detail&bookingId=" + bookingId);
            return;
        }

        if (!"WAITING_CUSTOMER_CONFIRM".equalsIgnoreCase(contract.getContractStatus())) {
            session.setAttribute("error", "This contract is not waiting for customer confirmation.");
            response.sendRedirect(request.getContextPath() + "/booking?action=detail&bookingId=" + bookingId);
            return;
        }

        if (contract.getHandoverCheckId() == null) {
            session.setAttribute("error", "No handover check found for this contract.");
            response.sendRedirect(request.getContextPath() + "/booking?action=detail&bookingId=" + bookingId);
            return;
        }

        boolean success = contractDAO.confirmCustomerHandover(contractId, customerNote);

        if (success) {
            session.setAttribute("handoverStatus", "confirm_success");
        } else {
            session.setAttribute("handoverStatus", "confirm_fail");
        }

        response.sendRedirect(request.getContextPath() + "/booking?action=detail&bookingId=" + bookingId);
    }

    private void rejectHandover(HttpServletRequest request,
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
        String contractIdRaw = request.getParameter("contractId");
        String customerNote = request.getParameter("customerNote");

        int bookingId;
        int contractId;

        try {
            bookingId = Integer.parseInt(bookingIdRaw);
            contractId = Integer.parseInt(contractIdRaw);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/booking?action=list");
            return;
        }

        BookingModel booking = bookingDAO.findById(bookingId, customer.getCustomerId());
        if (booking == null) {
            session.setAttribute("error", "Booking not found.");
            response.sendRedirect(request.getContextPath() + "/booking?action=list");
            return;
        }

        ContractModel contract = contractDAO.getContractById(contractId);
        if (contract == null || contract.getCustomerId() != customer.getCustomerId()) {
            session.setAttribute("error", "Contract not found.");
            response.sendRedirect(request.getContextPath() + "/booking?action=detail&bookingId=" + bookingId);
            return;
        }

        if (!"WAITING_CUSTOMER_CONFIRM".equalsIgnoreCase(contract.getContractStatus())) {
            session.setAttribute("error", "This contract is not waiting for customer confirmation.");
            response.sendRedirect(request.getContextPath() + "/booking?action=detail&bookingId=" + bookingId);
            return;
        }

        boolean success = contractDAO.rejectCustomerHandover(contractId, customerNote);

        if (success) {
            bookingDAO.updateStatus(bookingId, "CANCELLED");
            carDAO.updateStatus(contract.getCarId(), "AVAILABLE");
            session.setAttribute("handoverStatus", "reject_success");
        } else {
            session.setAttribute("handoverStatus", "reject_fail");
        }

        response.sendRedirect(request.getContextPath() + "/booking?action=detail&bookingId=" + bookingId);
    }

    private Timestamp parseDateTimeLocal(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }

        try {
            String normalized = raw.trim().replace("T", " ");
            if (normalized.length() == 16) {
                normalized += ":00";
            }
            return Timestamp.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private BigDecimal calculateTotalPrice(Timestamp startTime, Timestamp endTime, BigDecimal pricePerDay) {
        long minutes = ChronoUnit.MINUTES.between(
                startTime.toLocalDateTime(),
                endTime.toLocalDateTime()
        );

        if (minutes <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal pricePerHour = pricePerDay.divide(BigDecimal.valueOf(24), 2, RoundingMode.HALF_UP);
        BigDecimal halfDayPrice = pricePerDay.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        long fullDays = minutes / 1440;
        long remainMinutes = minutes % 1440;

        BigDecimal total = pricePerDay.multiply(BigDecimal.valueOf(fullDays));

        if (remainMinutes > 0) {
            if (remainMinutes <= 360) {
                BigDecimal remainHours = BigDecimal.valueOf(remainMinutes)
                        .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
                total = total.add(pricePerHour.multiply(remainHours));
            } else if (remainMinutes <= 720) {
                total = total.add(halfDayPrice);
            } else {
                total = total.add(pricePerDay);
            }
        }

        return total.setScale(2, RoundingMode.HALF_UP);
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

    private Timestamp parseDateTimeFromRequest(HttpServletRequest request, String dateParam, String timeParam) {
        String dateValue = request.getParameter(dateParam);
        String timeValue = request.getParameter(timeParam);

        if (dateValue == null || dateValue.trim().isEmpty()
                || timeValue == null || timeValue.trim().isEmpty()) {
            return null;
        }

        try {
            String normalized = dateValue.trim() + " " + timeValue.trim() + ":00";
            return Timestamp.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Timestamp resolveStartTime(HttpServletRequest request) {
        Timestamp direct = parseDateTimeLocal(request.getParameter("startDate"));
        if (direct != null) {
            return direct;
        }
        return parseDateTimeFromRequest(request, "startDate", "startHour");
    }

    private Timestamp resolveEndTime(HttpServletRequest request) {
        Timestamp direct = parseDateTimeLocal(request.getParameter("endDate"));
        if (direct != null) {
            return direct;
        }
        return parseDateTimeFromRequest(request, "endDate", "endHour");
    }

    private String buildDateTimeLocalValue(Timestamp ts) {
        if (ts == null) {
            return "";
        }
        LocalDateTime ldt = ts.toLocalDateTime();
        String year = String.valueOf(ldt.getYear());
        String month = String.format("%02d", ldt.getMonthValue());
        String day = String.format("%02d", ldt.getDayOfMonth());
        String hour = String.format("%02d", ldt.getHour());
        String minute = String.format("%02d", ldt.getMinute());
        return year + "-" + month + "-" + day + "T" + hour + ":" + minute;
    }

    private String buildRentalDurationText(Timestamp startTime, Timestamp endTime) {
        if (startTime == null || endTime == null) {
            return "";
        }

        Duration duration = Duration.between(
                startTime.toLocalDateTime(),
                endTime.toLocalDateTime()
        );

        long totalMinutes = duration.toMinutes();
        long totalDays = totalMinutes / (24 * 60);
        long remainingHours = (totalMinutes % (24 * 60)) / 60;
        long remainingMinutes = totalMinutes % 60;

        String rentalDurationText = "";

        if (totalDays > 0) {
            rentalDurationText += totalDays + " day" + (totalDays > 1 ? "s" : "");
        }

        if (remainingHours > 0) {
            if (!rentalDurationText.isEmpty()) {
                rentalDurationText += " ";
            }
            rentalDurationText += remainingHours + " hour" + (remainingHours > 1 ? "s" : "");
        }

        if (remainingMinutes > 0) {
            if (!rentalDurationText.isEmpty()) {
                rentalDurationText += " ";
            }
            rentalDurationText += remainingMinutes + " minute" + (remainingMinutes > 1 ? "s" : "");
        }

        if (rentalDurationText.isEmpty()) {
            rentalDurationText = "0 minute";
        }

        return rentalDurationText;
    }

}
