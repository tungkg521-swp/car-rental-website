/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.List;

import DALs.BookingDAO;
import DALs.CarDAO;
import models.BookingModel;
import models.ContractModel;

/**
 *
 * @author ADMIN
 */
public class BookingService {

    private BookingDAO bookingDAO = new BookingDAO();
    private final ContractService contractService = new ContractService();
    private final CarDAO carDAO = new CarDAO();

    // ===== TÍNH TIỀN =====
    public BigDecimal calculateTotalPrice(
            Date startDate,
            Date endDate,
            BigDecimal pricePerDay
    ) {
        long days = ChronoUnit.DAYS.between(
                startDate.toLocalDate(),
                endDate.toLocalDate()

        );


        if (days < 1) {
            days = 1;
        }

        return pricePerDay.multiply(BigDecimal.valueOf(days));
    }

    // ===== TẠO BOOKING =====
    public void createBooking(BookingModel booking) throws SQLException {
        bookingDAO.insert(booking);
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

        // Chỉ cho cancel khi PENDING
        if (!"PENDING".equalsIgnoreCase(booking.getStatus())) {
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

    public void approveBooking(int bookingId, int staffId) {

        BookingModel booking = bookingDAO.getBookingForContract(bookingId);

        if (booking == null) {
            return;
        }

        if (!"PENDING".equalsIgnoreCase(booking.getStatus())) {
            return;
        }

        if (contractService.existsByBookingId(bookingId)) {
            return;
        }

        if (bookingDAO.hasOverlapConfirmed(
                booking.getCarId(),
                booking.getStartDate(),
                booking.getEndDate())) {

            bookingDAO.updateStatus(bookingId, "REJECTED");
            return;
        }

        bookingDAO.updateStatus(bookingId, "CONFIRMED");

        bookingDAO.rejectOverlappingBookings(
                booking.getCarId(),
                booking.getStartDate(),
                booking.getEndDate(),
                bookingId
        );

        ContractModel contract = new ContractModel();
        contract.setBookingId(booking.getBookingId());
        contract.setCustomerId(booking.getCustomerId());
        contract.setStaffId(staffId);
        contract.setCarId(booking.getCarId());
        contract.setContractStartDate(booking.getStartDate());
        contract.setContractEndDate(booking.getEndDate());
        contract.setContractStatus("CREATED");
        contract.setDailyPrice(booking.getPricePerDay().doubleValue());

        double total = booking.getTotalEstimatedPrice().doubleValue();
        double deposit = total * 0.3;

        contract.setDepositAmount(deposit);
        contract.setTotalAmount(total);
        contract.setSignedAt(null);
        contract.setNote("Contract created automatically after staff approved booking.");


        contractService.createContract(contract);

        carDAO.updateStatus(booking.getCarId(), "BOOKED");


    }

    public void rejectBooking(int bookingId) {
        BookingModel booking = bookingDAO.getById(bookingId);

        if (booking == null) {
            return;
        }

        if (!"PENDING".equalsIgnoreCase(booking.getStatus())) {
            return;
        }

        bookingDAO.updateStatus(bookingId, "REJECTED");
    }

    public boolean hasOverlapConfirmed(int carId, Date startDate, Date endDate) {
    return bookingDAO.hasOverlapConfirmed(carId, startDate, endDate);
}
}
