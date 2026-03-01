/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DALs;

import models.BookingModel;
import Utils.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.BookingModel;

/**
 *
 * @author ADMIN
 */
public class BookingDAO extends DBContext {

    public void insert(BookingModel booking) throws SQLException {

        String sql = """
            INSERT INTO booking
            (customer_id, car_id, booking_date, start_date, end_date, status, note, total_estimated_price)
            VALUES (?, ?, GETDATE(), ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, booking.getCustomerId());
            ps.setInt(2, booking.getCarId());
            ps.setDate(3, booking.getStartDate());
            ps.setDate(4, booking.getEndDate());
            ps.setString(5, booking.getStatus());
            ps.setString(6, booking.getNote());
            ps.setBigDecimal(7, booking.getTotalEstimatedPrice());

            ps.executeUpdate();
        }
    }

    public BookingModel getById(int bookingId) {
        String sql = """
            SELECT *
            FROM booking
            WHERE booking_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                BookingModel booking = new BookingModel();

                booking.setBookingId(rs.getInt("booking_id"));
                booking.setCustomerId(rs.getInt("customer_id"));
                booking.setCarId(rs.getInt("car_id"));
                booking.setBookingDate(rs.getTimestamp("booking_date"));
                booking.setStartDate(rs.getDate("start_date"));
                booking.setEndDate(rs.getDate("end_date"));
                booking.setStatus(rs.getString("status"));
                booking.setNote(rs.getString("note"));
                booking.setTotalEstimatedPrice(
                        rs.getBigDecimal("total_estimated_price")
                );

                return booking;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<BookingModel> findByCustomerId(int customerId) {

        List<BookingModel> list = new ArrayList<>();

        String sql
                = "SELECT b.booking_id, b.start_date, b.end_date, b.status, "
                + "b.total_estimated_price, "
                + "c.model_name AS car_name, "
                + "c.image_folder AS image_folder "
                + "FROM booking b "
                + "JOIN cars c ON b.car_id = c.car_id "
                + "WHERE b.customer_id = ? "
                + "ORDER BY b.booking_date DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BookingModel booking = new BookingModel();

                booking.setBookingId(rs.getInt("booking_id"));
                booking.setStartDate(rs.getDate("start_date"));
                booking.setEndDate(rs.getDate("end_date"));
                booking.setStatus(rs.getString("status"));
                booking.setTotalEstimatedPrice(
                        rs.getBigDecimal("total_estimated_price"));

                // nếu BookingModel CÓ field này thì set
                booking.setCarName(rs.getString("car_name"));
                booking.setImageFolder(rs.getString("image_folder"));

                list.add(booking);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public BookingModel findById(int bookingId, int customerId) {

        String sql
                = "SELECT b.booking_id, b.booking_date, b.start_date, b.end_date, "
                + "b.status, b.note, b.total_estimated_price, "
                + "c.model_name, c.image_folder, "
                + "cus.full_name AS customer_name, cus.email, cus.phone "
                + // ❌ BỎ DẤU PHẨY CUỐI
                "FROM booking b "
                + "JOIN cars c ON b.car_id = c.car_id "
                + "JOIN customer cus ON b.customer_id = cus.customer_id "
                + "WHERE b.booking_id = ? AND b.customer_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            ps.setInt(2, customerId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                BookingModel booking = new BookingModel();

                booking.setBookingId(rs.getInt("booking_id"));
                booking.setBookingDate(rs.getTimestamp("booking_date"));
                booking.setStartDate(rs.getDate("start_date"));
                booking.setEndDate(rs.getDate("end_date"));
                booking.setStatus(rs.getString("status"));
                booking.setNote(rs.getString("note"));
                booking.setTotalEstimatedPrice(
                        rs.getBigDecimal("total_estimated_price"));

                booking.setCarName(rs.getString("model_name"));
                booking.setImageFolder(rs.getString("image_folder"));

                booking.setCustomerName(rs.getString("customer_name"));
                booking.setCustomerEmail(rs.getString("email"));
                booking.setCustomerPhone(rs.getString("phone"));

                return booking;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateStatus(int bookingId, String status) {

        String sql = "UPDATE booking SET status = ? WHERE booking_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, bookingId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<BookingModel> findAllBookings() {

        List<BookingModel> list = new ArrayList<>();

        String sql = """
        SELECT 
            b.booking_id,
            c.full_name,
            ca.model_name,
            b.start_date,
            b.end_date,
            b.total_estimated_price,
            b.status
        FROM booking b
        JOIN customer c ON b.customer_id = c.customer_id
        JOIN cars ca ON b.car_id = ca.car_id
        ORDER BY b.booking_date DESC
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                BookingModel booking = new BookingModel();

                booking.setBookingId(rs.getInt("booking_id"));
                booking.setCustomerName(rs.getString("full_name"));
                booking.setCarName(rs.getString("model_name"));
                booking.setStartDate(rs.getDate("start_date"));
                booking.setEndDate(rs.getDate("end_date"));
                booking.setTotalEstimatedPrice(rs.getBigDecimal("total_estimated_price"));
                booking.setStatus(rs.getString("status"));

                list.add(booking);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public BookingModel findById(int bookingId) {

        String sql = """
        SELECT 
            b.booking_id,
            b.booking_date,
            b.start_date,
            b.end_date,
            b.status,
            b.note,
            b.total_estimated_price,

            c.full_name,
            c.email,
            c.phone,

            car.model_name,
            car.price_per_day

        FROM booking b
        JOIN customer c ON b.customer_id = c.customer_id
        JOIN cars car ON b.car_id = car.car_id
        WHERE b.booking_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                BookingModel booking = new BookingModel();

                booking.setBookingId(rs.getInt("booking_id"));
                booking.setBookingDate(rs.getTimestamp("booking_date"));
                booking.setStartDate(rs.getDate("start_date"));
                booking.setEndDate(rs.getDate("end_date"));
                booking.setStatus(rs.getString("status"));
                booking.setNote(rs.getString("note"));
                booking.setTotalEstimatedPrice(rs.getBigDecimal("total_estimated_price"));

                booking.setCustomerName(rs.getString("full_name"));
                booking.setCustomerEmail(rs.getString("email"));
                booking.setCustomerPhone(rs.getString("phone"));

                booking.setCarName(rs.getString("model_name"));
                booking.setPricePerDay(rs.getBigDecimal("price_per_day"));


                return booking;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
