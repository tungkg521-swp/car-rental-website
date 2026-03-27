package service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.List;

import DALs.BookingDAO;
import DALs.CarDAO;
import models.BookingModel;
import models.CarModel;
import models.ContractModel;

public class BookingService {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final ContractService contractService = new ContractService();
    private final CarDAO carDAO = new CarDAO();

    public BigDecimal calculateTotalPrice(
            Date startDate,
            Date endDate,
            BigDecimal pricePerDay
    ) {
        long days = ChronoUnit.DAYS.between(
                startDate.toLocalDate(),
                endDate.toLocalDate()
        ) + 1;

        if (days < 1) {
            days = 1;
        }

        return pricePerDay.multiply(BigDecimal.valueOf(days));
    }

    public int createBooking(BookingModel booking) throws SQLException {
        return bookingDAO.insert(booking);
    }

    public BookingModel getById(int bookingId) {
        return bookingDAO.getById(bookingId);
    }

    public List<BookingModel> getBookingsByCustomer(int customerId) {
        return bookingDAO.findByCustomerId(customerId);
    }

    public BookingModel getBookingDetail(int bookingId, int customerId) {
        return bookingDAO.findById(bookingId, customerId);
    }

    public boolean cancelBooking(int bookingId, int customerId) {

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

    public List<BookingModel> findAllBookings() {
        return bookingDAO.findAllBookings();
    }

    public BookingModel getBookingById(int id) {
        return bookingDAO.findById(id);
    }

    public boolean approveBooking(int bookingId, int staffId) {

        BookingModel booking = bookingDAO.getById(bookingId);

        if (booking == null) {
            return false;
        }

        if (!"PENDING_APPROVAL".equalsIgnoreCase(booking.getStatus())) {
            return false;
        }

        boolean updatedStaff = bookingDAO.updateStaffId(bookingId, staffId);
        boolean updatedDeadline = bookingDAO.updatePaymentDeadline(bookingId, 24);
        boolean updatedStatus = bookingDAO.updateStatus(bookingId, "AWAITING_PAYMENT");

        return updatedStaff && updatedDeadline && updatedStatus;
    }

    public boolean rejectBooking(int bookingId) {
        BookingModel booking = bookingDAO.getById(bookingId);

        if (booking == null) {
            return false;
        }

        if (!"PENDING_APPROVAL".equalsIgnoreCase(booking.getStatus())) {
            return false;
        }

        return bookingDAO.updateStatus(bookingId, "REJECTED");
    }

    public boolean hasOverlapConfirmed(int carId, Date startDate, Date endDate) {
        return bookingDAO.hasBookingConflict(carId, startDate, endDate);
    }

    public boolean deleteCancelledBooking(int bookingId, int customerId) {
        BookingModel booking = bookingDAO.findById(bookingId, customerId);

        if (booking == null) {
            return false;
        }

        if (!"CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            return false;
        }

        return bookingDAO.deleteBooking(bookingId, customerId);
    }

    public void updateBookingStatus(int bookingId, String status) {
        bookingDAO.updateStatus(bookingId, status);
    }

    public boolean confirmBookingAfterSuccessfulPayment(int bookingId) {
        BookingModel booking = bookingDAO.getById(bookingId);

        if (booking == null) {
            return false;
        }

        if (!"AWAITING_PAYMENT".equalsIgnoreCase(booking.getStatus())) {
            return false;
        }

        if (contractService.existsByBookingId(bookingId)) {
            return false;
        }

        CarModel car = carDAO.findById(booking.getCarId());
        if (car == null) {
            return false;
        }

        // nếu xe đang maintenance thì không cho chốt
        if ("MAINTENANCE".equalsIgnoreCase(car.getStatus())) {
            return false;
        }

        // check conflict lần cuối trước khi chốt cứng xe
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

        boolean created = contractService.createContract(contract);
        if (!created) {
            return false;
        }

        boolean bookingUpdated = bookingDAO.updateStatus(bookingId, "CONFIRMED");
        if (!bookingUpdated) {
            return false;
        }

        carDAO.updateStatus(booking.getCarId(), "BOOKED");

        return true;
    }

    public List<Date[]> getBusyDateRangesByCarId(int carId) {
        return bookingDAO.getBusyDateRangesByCarId(carId);
    }

}
