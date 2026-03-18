/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DALs;

import Utils.DBContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.MaintenanceModel;

public class MaintenanceDAO extends DBContext {

    public List<MaintenanceModel> findAll() {
        List<MaintenanceModel> list = new ArrayList<>();
        String sql = """
        SELECT 
            m.maintenance_id, m.car_id, c.model_name, c.plate_number AS license_plate,
            i.image_url AS car_image_url,                    -- LẤY ĐƯỜNG DẪN ĐẦY ĐỦ
            m.maintenance_type, m.scheduled_date,
            m.mileage_scheduled, m.description, m.estimated_cost,
            m.status, m.created_by, m.created_at, m.updated_at
        FROM car_maintenance m
        JOIN cars c ON m.car_id = c.car_id
        LEFT JOIN cars_image i ON c.car_id = i.car_id AND i.is_primary = 1   -- THÊM DÒNG NÀY
        ORDER BY m.scheduled_date DESC
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MaintenanceModel m = new MaintenanceModel();
                m.setMaintenanceId(rs.getInt("maintenance_id"));
                m.setCarId(rs.getInt("car_id"));
                m.setModelName(rs.getString("model_name"));
                m.setLicensePlate(rs.getString("license_plate"));
                m.setCarImageUrl(rs.getString("car_image_url"));        // ← QUAN TRỌNG
                m.setMaintenanceType(rs.getString("maintenance_type"));
                m.setScheduledDate(rs.getDate("scheduled_date"));
                m.setMileageScheduled(rs.getInt("mileage_scheduled"));
                m.setDescription(rs.getString("description"));
                m.setEstimatedCost(rs.getBigDecimal("estimated_cost"));
                m.setStatus(rs.getString("status"));
                m.setCreatedBy(rs.getInt("created_by"));
                m.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                m.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                list.add(m);
            }
//         System.out.println("findAll() found " + list.size() + " maintenances");  // ← DEBUG
        }catch (SQLException e) {
        //System.err.println("Error in findAll: " + e.getMessage());
        e.printStackTrace();
    }
        return list;
    }

    public MaintenanceModel findById(int maintenanceId) {
        MaintenanceModel m = null;
        String sql = """
        SELECT 
            m.maintenance_id, m.car_id, m.maintenance_type, m.scheduled_date,
            m.mileage_scheduled, m.description, m.estimated_cost,
            m.status, m.created_by, m.created_at, m.updated_at,
            c.model_name, c.plate_number AS license_plate,
            i.image_url AS car_image_url
        FROM car_maintenance m
        JOIN cars c ON m.car_id = c.car_id
        LEFT JOIN cars_image i ON c.car_id = i.car_id AND i.is_primary = 1
        WHERE m.maintenance_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, maintenanceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                m = mapRowToModel(rs);
                m.setCarImageUrl(rs.getString("car_image_url"));  // có thể null nếu không có ảnh primary
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return m;
    }

    private MaintenanceModel mapRowToModel(ResultSet rs) throws SQLException {
        MaintenanceModel m = new MaintenanceModel();
        m.setMaintenanceId(rs.getInt("maintenance_id"));
        m.setCarId(rs.getInt("car_id"));
        m.setModelName(rs.getString("model_name"));
        m.setLicensePlate(rs.getString("license_plate"));
        m.setCarImageUrl(rs.getString("car_image_url"));        // ← ĐÃ CÓ
        m.setMaintenanceType(rs.getString("maintenance_type"));
        m.setScheduledDate(rs.getDate("scheduled_date"));
        m.setMileageScheduled(rs.getInt("mileage_scheduled"));
        m.setDescription(rs.getString("description"));
        m.setEstimatedCost(rs.getBigDecimal("estimated_cost"));
        m.setStatus(rs.getString("status"));
        m.setCreatedBy(rs.getInt("created_by"));
        m.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        m.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
        return m;
    }

    public boolean add(MaintenanceModel m) {
        String sql = """
            INSERT INTO car_maintenance 
            (car_id, maintenance_type, scheduled_date, mileage_scheduled, 
             description, estimated_cost, status, created_by)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, m.getCarId());
            ps.setString(2, m.getMaintenanceType());
            ps.setDate(3, m.getScheduledDate());
            ps.setInt(4, m.getMileageScheduled());
            ps.setString(5, m.getDescription());
            ps.setBigDecimal(6, m.getEstimatedCost());
            ps.setString(7, m.getStatus() != null ? m.getStatus() : "SCHEDULED");
            ps.setObject(8, m.getCreatedBy());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(MaintenanceModel m) {
        String sql = """
            UPDATE car_maintenance SET
                maintenance_type = ?,
                scheduled_date = ?,
                mileage_scheduled = ?,
                description = ?,
                estimated_cost = ?,
                status = ?,
                updated_at = GETDATE()
            WHERE maintenance_id = ?
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, m.getMaintenanceType());
            ps.setDate(2, m.getScheduledDate());
            ps.setInt(3, m.getMileageScheduled());
            ps.setString(4, m.getDescription());
            ps.setBigDecimal(5, m.getEstimatedCost());
            ps.setString(6, m.getStatus());
            ps.setInt(7, m.getMaintenanceId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Nếu sau này cần thêm hàm findOverdue, cancel, etc. thì bổ sung ở đây
}
