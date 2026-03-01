/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import DALs.BookingDAO;
import models.BookingModel;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import models.BookingModel;
import models.CarModel;

/**
 *
 * @author ADMIN
 */
public class BookingService {

    private BookingDAO bookingDAO = new BookingDAO();

    // ===== TÍNH TIỀN =====
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



}
