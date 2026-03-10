/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DALs;

import Models.MaintenanceModel;
import Utils.DBContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceDAO extends DBContext {

    // Lấy tất cả lịch bảo dưỡng (cho staff dashboard / list)
    public List<MaintenanceModel> findAll() {
        List<MaintenanceModel> list = new ArrayList<>();
        String sql = """
            SELECT 
                m.maintenance_id, m.car_id, c.model_name,
                m.maintenance_type, m.scheduled_date, m.actual_completion_date,
                m.mileage_scheduled, m.mileage_completed,
                m.description, m.estimated_cost, m.actual_cost,
                m.status, m.created_by, m.created_at, m.updated_at, m.completed_by
            FROM car_maintenance m
            JOIN cars c ON m.car_id = c.car_id
            ORDER BY m.scheduled_date DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MaintenanceModel m = new MaintenanceModel();
                m.setMaintenanceId(rs.getInt("maintenance_id"));
                m.setCarId(rs.getInt("car_id"));
                m.setModelName(rs.getString("model_name"));
                m.setMaintenanceType(rs.getString("maintenance_type"));
                m.setScheduledDate(rs.getDate("scheduled_date"));
                m.setActualCompletionDate(rs.getDate("actual_completion_date"));
                m.setMileageScheduled(rs.getInt("mileage_scheduled"));
                m.setMileageCompleted(rs.getInt("mileage_completed"));
                m.setDescription(rs.getString("description"));
                m.setEstimatedCost(rs.getBigDecimal("estimated_cost"));
                m.setActualCost(rs.getBigDecimal("actual_cost"));
                m.setStatus(rs.getString("status"));
                m.setCreatedBy(rs.getInt("created_by"));
                m.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                m.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                m.setCompletedBy(rs.getInt("completed_by"));
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy theo car_id
   public MaintenanceModel findById(int maintenanceId) {
        MaintenanceModel m = null;
        String sql = """
            SELECT 
                m.*, c.model_name, c.license_plate
            FROM car_maintenance m
            JOIN cars c ON m.car_id = c.car_id
            WHERE m.maintenance_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, maintenanceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                m = mapRowToModel(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return m;
    }

    // Helper method để map ResultSet → Model (dùng chung)
    private MaintenanceModel mapRowToModel(ResultSet rs) throws SQLException {
        MaintenanceModel m = new MaintenanceModel();
        m.setMaintenanceId(rs.getInt("maintenance_id"));
        m.setCarId(rs.getInt("car_id"));
        m.setModelName(rs.getString("model_name"));
        m.setMaintenanceType(rs.getString("maintenance_type"));
        m.setScheduledDate(rs.getDate("scheduled_date"));
        m.setActualCompletionDate(rs.getDate("actual_completion_date"));
        m.setMileageScheduled(rs.getInt("mileage_scheduled"));
        m.setMileageCompleted(rs.getInt("mileage_completed"));
        m.setDescription(rs.getString("description"));
        m.setEstimatedCost(rs.getBigDecimal("estimated_cost"));
        m.setActualCost(rs.getBigDecimal("actual_cost"));
        m.setStatus(rs.getString("status"));
        m.setCreatedBy(rs.getInt("created_by"));
        m.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        m.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
        m.setCompletedBy(rs.getInt("completed_by"));
        return m;
    }

    // 3. Thêm mới lịch bảo dưỡng
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
            ps.setObject(8, m.getCreatedBy());  // có thể null
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Update lịch (sửa thông tin hoặc mark hoàn thành / hủy)
    public boolean update(MaintenanceModel m) {
        String sql = """
            UPDATE car_maintenance SET
                maintenance_type = ?,
                scheduled_date = ?,
                mileage_scheduled = ?,
                description = ?,
                estimated_cost = ?,
                status = ?,
                actual_completion_date = ?,
                mileage_completed = ?,
                actual_cost = ?,
                completed_by = ?,
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
            ps.setDate(7, m.getActualCompletionDate());
            ps.setObject(8, m.getMileageCompleted());
            ps.setBigDecimal(9, m.getActualCost());
            ps.setObject(10, m.getCompletedBy());
            ps.setInt(11, m.getMaintenanceId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}