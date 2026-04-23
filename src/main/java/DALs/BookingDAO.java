package DALs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import Utils.DBContext;
import java.sql.Timestamp;
import models.BookingModel;

public class BookingDAO extends DBContext {

    public int insert(BookingModel booking) throws SQLException {

        String sql = """
        INSERT INTO booking
        (customer_id, car_id, voucher_id, booking_date, start_time, end_time,
         status, note, deposit_amount, remaining_amount, payment_deadline, total_estimated_price)
        VALUES (?, ?, ?, SYSDATETIME(), ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, booking.getCustomerId());
            ps.setInt(2, booking.getCarId());

            if (booking.getVoucherId() != null) {
                ps.setInt(3, booking.getVoucherId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setTimestamp(4, booking.getStartTime());
            ps.setTimestamp(5, booking.getEndTime());
            ps.setString(6, booking.getStatus());
            ps.setString(7, booking.getNote());

            if (booking.getDepositAmount() != null) {
                ps.setBigDecimal(8, booking.getDepositAmount());
            } else {
                ps.setNull(8, Types.DECIMAL);
            }

            if (booking.getRemainingAmount() != null) {
                ps.setBigDecimal(9, booking.getRemainingAmount());
            } else {
                ps.setNull(9, Types.DECIMAL);
            }

            if (booking.getPaymentDeadline() != null) {
                ps.setTimestamp(10, booking.getPaymentDeadline());
            } else {
                ps.setNull(10, Types.TIMESTAMP);
            }

            ps.setBigDecimal(11, booking.getTotalEstimatedPrice());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int bookingId = rs.getInt(1);
                    booking.setBookingId(bookingId);
                    return bookingId;
                }
            }
        }

        throw new SQLException("Không lấy được booking_id sau khi tạo booking.");
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

                int voucherId = rs.getInt("voucher_id");
                if (!rs.wasNull()) {
                    booking.setVoucherId(voucherId);
                }

                int staffId = rs.getInt("staff_id");
                if (!rs.wasNull()) {
                    booking.setStaffId(staffId);
                }

                booking.setBookingDate(rs.getTimestamp("booking_date"));
                booking.setStartTime(rs.getTimestamp("start_time"));
                booking.setEndTime(rs.getTimestamp("end_time"));
                booking.setStatus(rs.getString("status"));
                booking.setNote(rs.getString("note"));
                booking.setTotalEstimatedPrice(rs.getBigDecimal("total_estimated_price"));
                booking.setDepositAmount(rs.getBigDecimal("deposit_amount"));
                booking.setRemainingAmount(rs.getBigDecimal("remaining_amount"));
                booking.setPaymentDeadline(rs.getTimestamp("payment_deadline"));
                booking.setCustomerCheckStatus(rs.getString("customer_check_status"));
                booking.setCustomerCheckReason(rs.getString("customer_check_reason"));
                booking.setCustomerCheckNote(rs.getString("customer_check_note"));
                booking.setCustomerCheckedAt(rs.getTimestamp("customer_checked_at"));
                return booking;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<BookingModel> findByCustomerId(int customerId) {

        List<BookingModel> list = new ArrayList<>();

        String sql = """
        SELECT
            b.booking_id,
            b.start_time,
            b.end_time,
            b.status,
            b.total_estimated_price,
            c.model_name AS car_name,
            c.image_folder AS image_folder,
            rc.contract_status
        FROM booking b
        JOIN cars c ON b.car_id = c.car_id
        LEFT JOIN rental_contract rc ON b.booking_id = rc.booking_id
        WHERE b.customer_id = ?
        ORDER BY b.booking_date DESC
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BookingModel booking = new BookingModel();

                booking.setBookingId(rs.getInt("booking_id"));
                booking.setStartTime(rs.getTimestamp("start_time"));
                booking.setEndTime(rs.getTimestamp("end_time"));
                booking.setStatus(rs.getString("status"));
                booking.setTotalEstimatedPrice(rs.getBigDecimal("total_estimated_price"));
                booking.setCarName(rs.getString("car_name"));
                booking.setImageFolder(rs.getString("image_folder"));
                booking.setContractStatus(rs.getString("contract_status"));

                list.add(booking);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public BookingModel findById(int bookingId, int customerId) {

        String sql = """
        SELECT
            b.booking_id,
            b.booking_date,
            b.start_time,
            b.end_time,
            b.status,
            b.note,
            b.total_estimated_price,
            b.deposit_amount,
            b.remaining_amount,
            b.payment_deadline,
            b.customer_check_status,
            b.customer_check_reason,
            b.customer_check_note,
            b.customer_checked_at,
            c.model_name,
            c.image_folder,

            cus.full_name AS customer_name,
            cus.email,
            cus.phone,

            rc.contract_status

        FROM booking b
        JOIN cars c ON b.car_id = c.car_id
        JOIN customer cus ON b.customer_id = cus.customer_id

        LEFT JOIN rental_contract rc
        ON b.booking_id = rc.booking_id

        WHERE b.booking_id = ?
        AND b.customer_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            ps.setInt(2, customerId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                BookingModel booking = new BookingModel();

                booking.setBookingId(rs.getInt("booking_id"));
                booking.setBookingDate(rs.getTimestamp("booking_date"));
                booking.setStartTime(rs.getTimestamp("start_time"));
                booking.setEndTime(rs.getTimestamp("end_time"));
                booking.setStatus(rs.getString("status"));
                booking.setNote(rs.getString("note"));

                booking.setTotalEstimatedPrice(rs.getBigDecimal("total_estimated_price"));

                booking.setCarName(rs.getString("model_name"));
                booking.setImageFolder(rs.getString("image_folder"));

                booking.setCustomerName(rs.getString("customer_name"));
                booking.setCustomerEmail(rs.getString("email"));
                booking.setCustomerPhone(rs.getString("phone"));

                booking.setContractStatus(rs.getString("contract_status"));
                booking.setDepositAmount(rs.getBigDecimal("deposit_amount"));
                booking.setRemainingAmount(rs.getBigDecimal("remaining_amount"));
                booking.setPaymentDeadline(rs.getTimestamp("payment_deadline"));

                booking.setCustomerCheckStatus(rs.getString("customer_check_status"));
                booking.setCustomerCheckReason(rs.getString("customer_check_reason"));
                booking.setCustomerCheckNote(rs.getString("customer_check_note"));
                booking.setCustomerCheckedAt(rs.getTimestamp("customer_checked_at"));

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

    public boolean updateStaffId(int bookingId, int staffId) {

        String sql = "UPDATE booking SET staff_id = ? WHERE booking_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updatePaymentDeadline(int bookingId, int hours) {

        String sql = "UPDATE booking SET payment_deadline = DATEADD(HOUR, ?, GETDATE()) WHERE booking_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, hours);
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
            b.start_time,
            b.end_time,
            b.total_estimated_price,
            b.status,
            ca.plate_number

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
                booking.setStartTime(rs.getTimestamp("start_time"));
                booking.setEndTime(rs.getTimestamp("end_time"));
                booking.setTotalEstimatedPrice(rs.getBigDecimal("total_estimated_price"));
                booking.setStatus(rs.getString("status"));
                booking.setPlateNumber(rs.getString("plate_number"));
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
        b.start_time,
        b.end_time,
        b.status,
        b.note,
        b.total_estimated_price,
        b.deposit_amount,
        b.remaining_amount,
        b.payment_deadline,

        c.full_name,
        c.email,
        c.phone,
        c.citizen_id,

        car.model_name,
        car.price_per_day,
        car.image_folder,
        car.plate_number

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
                booking.setStartTime(rs.getTimestamp("start_time"));
                booking.setEndTime(rs.getTimestamp("end_time"));
                booking.setStatus(rs.getString("status"));
                booking.setNote(rs.getString("note"));
                booking.setTotalEstimatedPrice(rs.getBigDecimal("total_estimated_price"));

                booking.setCustomerName(rs.getString("full_name"));
                booking.setCustomerEmail(rs.getString("email"));
                booking.setCustomerPhone(rs.getString("phone"));
                booking.setCustomercitizen_id(rs.getString("citizen_id"));

                booking.setCarName(rs.getString("model_name"));
                booking.setPricePerDay(rs.getBigDecimal("price_per_day"));
                booking.setImageFolder(rs.getString("image_folder"));
                booking.setDepositAmount(rs.getBigDecimal("deposit_amount"));
                booking.setRemainingAmount(rs.getBigDecimal("remaining_amount"));
                booking.setPaymentDeadline(rs.getTimestamp("payment_deadline"));
                booking.setPlateNumber(rs.getString("plate_number"));
                return booking;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public int getCompletedBooking(int customerId, int carId) {

        String sql = """
            SELECT booking_id
            FROM booking
            WHERE customer_id = ?
              AND car_id = ?
              AND status = 'COMPLETED'
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ps.setInt(2, carId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("booking_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public BookingModel getBookingForContract(int bookingId) {

        String sql = """
        SELECT
            b.booking_id,
            b.customer_id,
            b.car_id,
            b.start_time,
            b.end_time,
            b.total_estimated_price,
            b.status,
            car.price_per_day
        FROM booking b
        JOIN cars car ON b.car_id = car.car_id
        WHERE b.booking_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                BookingModel booking = new BookingModel();

                booking.setBookingId(rs.getInt("booking_id"));
                booking.setCustomerId(rs.getInt("customer_id"));
                booking.setCarId(rs.getInt("car_id"));
                booking.setStartTime(rs.getTimestamp("start_time"));
                booking.setEndTime(rs.getTimestamp("end_time"));
                booking.setStatus(rs.getString("status"));
                booking.setTotalEstimatedPrice(rs.getBigDecimal("total_estimated_price"));
                booking.setPricePerDay(rs.getBigDecimal("price_per_day"));

                return booking;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean hasOverlapConfirmed(int carId, Timestamp startTime, Timestamp endTime) {
        String sql = """
        SELECT 1
        FROM booking
        WHERE car_id = ?
          AND status = 'CONFIRMED'
          AND start_time < DATEADD(HOUR, 4, ?)
          AND end_time > DATEADD(HOUR, -4, ?)
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, carId);
            ps.setTimestamp(2, endTime);
            ps.setTimestamp(3, startTime);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void rejectOverlappingBookings(
            int carId,
            Timestamp startTime,
            Timestamp endTime,
            int confirmedBookingId) {

        String sql = """
        UPDATE booking
        SET status = 'REJECTED'
        WHERE car_id = ?
          AND booking_id <> ?
          AND start_time < DATEADD(HOUR, 4, ?)
          AND end_time > DATEADD(HOUR, -4, ?)
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, carId);
            ps.setInt(2, confirmedBookingId);
            ps.setTimestamp(3, endTime);
            ps.setTimestamp(4, startTime);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteBooking(int bookingId, int customerId) {
        String sql = """
            DELETE FROM booking
            WHERE booking_id = ?
              AND customer_id = ?
              AND status = 'CANCELLED'
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, customerId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasBookingConflict(int carId, Timestamp startTime, Timestamp endTime) {
        String sql = """
        SELECT 1
        FROM booking
        WHERE car_id = ?
          AND status IN ('AWAITING_PAYMENT', 'PENDING_APPROVAL', 'CONFIRMED', 'ACTIVE')
          AND start_time < DATEADD(HOUR, 4, ?)
          AND end_time > DATEADD(HOUR, -4, ?)
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, carId);
            ps.setTimestamp(2, endTime);
            ps.setTimestamp(3, startTime);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Timestamp[]> getBusyDateRangesByCarId(int carId) {
        List<Timestamp[]> ranges = new ArrayList<>();

        String sql = """
        SELECT start_time, end_time
        FROM booking
        WHERE car_id = ?
          AND status IN ('AWAITING_PAYMENT', 'PENDING_APPROVAL', 'CONFIRMED', 'WAITING_CUSTOMER_CONFIRM', 'ACTIVE')
        ORDER BY start_time
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, carId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Timestamp startTime = rs.getTimestamp("start_time");
                Timestamp endTime = rs.getTimestamp("end_time");
                ranges.add(new Timestamp[]{startTime, endTime});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ranges;
    }

    public boolean updateCarId(int bookingId, int newCarId) {
        String sql = "UPDATE booking SET car_id = ? WHERE booking_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, newCarId);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public BookingModel findByIdForCarChange(int bookingId, int customerId) {
        String sql = """
        SELECT
            b.booking_id,
            b.car_id,
            b.booking_date,
            b.start_time,
            b.end_time,
            b.status,
            b.note,
            b.total_estimated_price,
            b.deposit_amount,
            b.remaining_amount,
            b.payment_deadline,
            c.model_name,
            c.image_folder,
            cus.full_name AS customer_name,
            cus.email,
            cus.phone,
            rc.contract_status
        FROM booking b
        JOIN cars c ON b.car_id = c.car_id
        JOIN customer cus ON b.customer_id = cus.customer_id
        LEFT JOIN rental_contract rc
            ON b.booking_id = rc.booking_id
        WHERE b.booking_id = ?
          AND b.customer_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, customerId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                BookingModel booking = new BookingModel();

                booking.setBookingId(rs.getInt("booking_id"));
                booking.setCarId(rs.getInt("car_id"));
                booking.setBookingDate(rs.getTimestamp("booking_date"));
                booking.setStartTime(rs.getTimestamp("start_time"));
                booking.setEndTime(rs.getTimestamp("end_time"));
                booking.setStatus(rs.getString("status"));
                booking.setNote(rs.getString("note"));
                booking.setTotalEstimatedPrice(rs.getBigDecimal("total_estimated_price"));
                booking.setDepositAmount(rs.getBigDecimal("deposit_amount"));
                booking.setRemainingAmount(rs.getBigDecimal("remaining_amount"));
                booking.setPaymentDeadline(rs.getTimestamp("payment_deadline"));

                booking.setCarName(rs.getString("model_name"));
                booking.setImageFolder(rs.getString("image_folder"));

                booking.setCustomerName(rs.getString("customer_name"));
                booking.setCustomerEmail(rs.getString("email"));
                booking.setCustomerPhone(rs.getString("phone"));

                booking.setContractStatus(rs.getString("contract_status"));

                return booking;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public boolean hasBookingConflictExcludeBooking(int carId, Timestamp startTime, Timestamp endTime, int excludeBookingId) {
        String sql = """
        SELECT 1
        FROM booking
        WHERE car_id = ?
          AND booking_id <> ?
          AND status IN ('AWAITING_PAYMENT', 'PENDING_APPROVAL', 'CONFIRMED', 'ACTIVE')
          AND start_time < DATEADD(HOUR, 4, ?)
          AND end_time > DATEADD(HOUR, -4, ?)
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, carId);
            ps.setInt(2, excludeBookingId);
            ps.setTimestamp(3, endTime);
            ps.setTimestamp(4, startTime);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateCustomerCheck(
            int bookingId,
            int customerId,
            String status,
            String reason,
            String note) {

        String sql = """
        UPDATE booking
        SET customer_check_status = ?,
            customer_check_reason = ?,
            customer_check_note = ?,
            customer_checked_at = SYSDATETIME()
        WHERE booking_id = ?
          AND customer_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, reason);
            ps.setString(3, note);
            ps.setInt(4, bookingId);
            ps.setInt(5, customerId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}