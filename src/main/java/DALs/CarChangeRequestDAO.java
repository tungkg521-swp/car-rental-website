/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DALs;

import Utils.DBContext;
import models.CarChangeRequestModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import models.CarChangeRequestModel;

/**
 *
 * @author ADMIN
 */
public class CarChangeRequestDAO extends DBContext{
    public boolean existsPendingRequest(int bookingId) {
    String sql = """
        SELECT 1
        FROM car_change_request
        WHERE booking_id = ?
          AND status = 'PENDING'
    """;

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setInt(1, bookingId);
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
    
    public int createStaffRequest(CarChangeRequestModel request) {
    String sql = """
        INSERT INTO car_change_request
        (booking_id, old_car_id, new_car_id, requested_by, status, reason)
        VALUES (?, ?, ?, 'STAFF', 'PENDING', ?)
    """;

    try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
        ps.setInt(1, request.getBookingId());
        ps.setInt(2, request.getOldCarId());
        ps.setInt(3, request.getNewCarId());
        ps.setString(4, request.getReason());

        int affectedRows = ps.executeUpdate();
        if (affectedRows > 0) {
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return -1;
}

public CarChangeRequestModel getPendingByBookingId(int bookingId) {
    String sql = """
        SELECT *
        FROM car_change_request
        WHERE booking_id = ?
          AND status = 'PENDING'
    """;

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setInt(1, bookingId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                CarChangeRequestModel request = new CarChangeRequestModel();
                request.setRequestId(rs.getInt("request_id"));
                request.setBookingId(rs.getInt("booking_id"));
                request.setOldCarId(rs.getInt("old_car_id"));
                request.setNewCarId(rs.getInt("new_car_id"));
                request.setRequestedBy(rs.getString("requested_by"));
                request.setStatus(rs.getString("status"));
                request.setReason(rs.getString("reason"));
                request.setCreatedAt(rs.getTimestamp("created_at"));
                request.setResolvedAt(rs.getTimestamp("resolved_at"));
                return request;
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}


public CarChangeRequestModel getById(int requestId) {
    String sql = """
        SELECT *
        FROM car_change_request
        WHERE request_id = ?
    """;

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setInt(1, requestId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                CarChangeRequestModel request = new CarChangeRequestModel();
                request.setRequestId(rs.getInt("request_id"));
                request.setBookingId(rs.getInt("booking_id"));
                request.setOldCarId(rs.getInt("old_car_id"));
                request.setNewCarId(rs.getInt("new_car_id"));
                request.setRequestedBy(rs.getString("requested_by"));
                request.setStatus(rs.getString("status"));
                request.setReason(rs.getString("reason"));
                request.setCreatedAt(rs.getTimestamp("created_at"));
                request.setResolvedAt(rs.getTimestamp("resolved_at"));
                return request;
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}


public boolean updateStatus(int requestId, String status) {
    String sql = """
        UPDATE car_change_request
        SET status = ?, resolved_at = SYSDATETIME()
        WHERE request_id = ?
    """;

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setString(1, status);
        ps.setInt(2, requestId);
        return ps.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
    
    
    
    
}
