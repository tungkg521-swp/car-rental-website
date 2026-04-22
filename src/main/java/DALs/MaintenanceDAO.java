package DALs;

import Utils.DBContext;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.MaintenanceModel;

public class MaintenanceDAO extends DBContext {

    public List<MaintenanceModel> findAll() {
        List<MaintenanceModel> list = new ArrayList<>();

        String sql = """
            SELECT 
                m.maintenance_id,
                m.car_id,
                c.model_name,
                c.plate_number AS license_plate,
                i.image_url AS car_image_url,
                m.maintenance_type,
                m.start_date,
                m.end_date,
                m.mileage_scheduled,
                m.description,
                m.estimated_cost,
                m.status,
                m.created_by,
                m.updated_at
            FROM car_maintenance m
            JOIN cars c ON m.car_id = c.car_id
            LEFT JOIN cars_image i ON c.car_id = i.car_id AND i.is_primary = 1
            ORDER BY m.start_date DESC, m.maintenance_id DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MaintenanceModel m = new MaintenanceModel();
                m.setMaintenanceId(rs.getInt(1));
                m.setCarId(rs.getInt(2));
                m.setModelName(rs.getString(3));
                m.setLicensePlate(rs.getString(4));
                m.setCarImageUrl(rs.getString(5));
                m.setMaintenanceType(rs.getString(6));
                m.setStartDate(rs.getDate(7));
                m.setEndDate(rs.getDate(8));
                m.setMileageScheduled(rs.getInt(9));
                m.setDescription(rs.getString(10));
                m.setEstimatedCost(rs.getBigDecimal(11));
                m.setStatus(rs.getString(12));
                m.setCreatedBy((Integer) rs.getObject(13));
                if (rs.getTimestamp(14) != null) {
                    m.setUpdatedAt(rs.getTimestamp(14).toLocalDateTime());
                }
                list.add(m);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public MaintenanceModel findById(int maintenanceId) {
        String sql = """
            SELECT 
                m.maintenance_id,
                m.car_id,
                c.model_name,
                c.plate_number AS license_plate,
                i.image_url AS car_image_url,
                m.maintenance_type,
                m.start_date,
                m.end_date,
                m.mileage_scheduled,
                m.description,
                m.estimated_cost,
                m.status,
                m.created_by,
                m.updated_at
            FROM car_maintenance m
            JOIN cars c ON m.car_id = c.car_id
            LEFT JOIN cars_image i ON c.car_id = i.car_id AND i.is_primary = 1
            WHERE m.maintenance_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, maintenanceId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                MaintenanceModel m = new MaintenanceModel();
                m.setMaintenanceId(rs.getInt(1));
                m.setCarId(rs.getInt(2));
                m.setModelName(rs.getString(3));
                m.setLicensePlate(rs.getString(4));
                m.setCarImageUrl(rs.getString(5));
                m.setMaintenanceType(rs.getString(6));
                m.setStartDate(rs.getDate(7));
                m.setEndDate(rs.getDate(8));
                m.setMileageScheduled(rs.getInt(9));
                m.setDescription(rs.getString(10));
                m.setEstimatedCost(rs.getBigDecimal(11));
                m.setStatus(rs.getString(12));
                m.setCreatedBy((Integer) rs.getObject(13));
                if (rs.getTimestamp(14) != null) {
                    m.setUpdatedAt(rs.getTimestamp(14).toLocalDateTime());
                }
                return m;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }
//
//    private MaintenanceModel mapRowToModel(ResultSet rs) throws SQLException {
//        MaintenanceModel m = new MaintenanceModel();
//
//        m.setMaintenanceId(rs.getInt("maintenance_id"));
//        m.setCarId(rs.getInt("car_id"));
//        m.setModelName(rs.getString("model_name"));
//        m.setLicensePlate(rs.getString("license_plate"));
//        m.setCarImageUrl(rs.getString("car_image_url"));
//        m.setMaintenanceType(rs.getString("maintenance_type"));
//        m.setStartDate(rs.getDate("start_date"));
//        m.setEndDate(rs.getDate("end_date"));
//        m.setMileageScheduled(rs.getInt("mileage_scheduled"));
//        m.setDescription(rs.getString("description"));
//        m.setEstimatedCost(rs.getBigDecimal("estimated_cost"));

    ////        m.setStatus(rs.getString("status"));
////        m.setCreatedBy((Integer) rs.getObject("created_by"));
//
//        if (rs.getTimestamp("updated_at") != null) {
//            m.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
//        }
//
//        return m;
//    }

    public boolean add(MaintenanceModel m) {
        String sql = """
            INSERT INTO car_maintenance
            (
                car_id,
                maintenance_type,
                start_date,
                end_date,
                mileage_scheduled,
                description,
                estimated_cost,
                status,
                created_by,
                updated_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, m.getCarId());
            ps.setString(2, m.getMaintenanceType());
            ps.setDate(3, m.getStartDate());
            ps.setDate(4, m.getEndDate());
            ps.setInt(5, m.getMileageScheduled());
            ps.setString(6, m.getDescription());
            ps.setBigDecimal(7, m.getEstimatedCost());
            ps.setString(8, m.getStatus() != null ? m.getStatus() : "IN_PROGRESS");
            ps.setObject(9, m.getCreatedBy());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(MaintenanceModel m) {
        String sql = """
            UPDATE car_maintenance
            SET
                maintenance_type = ?,
                start_date = ?,
                end_date = ?,
                mileage_scheduled = ?,
                description = ?,
                estimated_cost = ?,
                status = ?,
                updated_at = GETDATE()
            WHERE maintenance_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, m.getMaintenanceType());
            ps.setDate(2, m.getStartDate());
            ps.setDate(3, m.getEndDate());
            ps.setInt(4, m.getMileageScheduled());
            ps.setString(5, m.getDescription());
            ps.setBigDecimal(6, m.getEstimatedCost());
            ps.setString(7, m.getStatus());
            ps.setInt(8, m.getMaintenanceId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(int maintenanceId) {
        String sql = "DELETE FROM car_maintenance WHERE maintenance_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, maintenanceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean hasScheduleConflict(int carId, Date startDate, Date endDate, Integer excludeMaintenanceId) {
        String sql = """
            SELECT TOP 1 1
            FROM (
                SELECT 
                    CAST(b.start_time AS date) AS start_date,
                    CAST(b.end_time AS date) AS end_date
                FROM booking b
                WHERE b.car_id = ?
                  AND b.status IN ('PENDING_APPROVAL', 'AWAITING_PAYMENT', 'CONFIRMED', 'ACTIVE')

                UNION ALL

                SELECT
                    CAST(rc.contract_start_time AS date) AS start_date,
                    CAST(rc.contract_end_time AS date) AS end_date
                FROM rental_contract rc
                WHERE rc.car_id = ?
                  AND rc.contract_status IN ('WAITING_CUSTOMER_CONFIRM', 'ACTIVE', 'ONGOING')

                UNION ALL

                SELECT
                    m.start_date,
                    m.end_date
                FROM car_maintenance m
                WHERE m.car_id = ?
                  AND m.status IN ('SCHEDULED', 'IN_PROGRESS')
                  AND (? IS NULL OR m.maintenance_id <> ?)
            ) x
            WHERE x.start_date <= ?
              AND x.end_date >= ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, carId);
            ps.setInt(2, carId);
            ps.setInt(3, carId);
            ps.setObject(4, excludeMaintenanceId);
            ps.setObject(5, excludeMaintenanceId);
            ps.setDate(6, endDate);
            ps.setDate(7, startDate);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<String[]> getBlockedRangesByCarId(int carId, Integer excludeMaintenanceId) {
        List<String[]> ranges = new ArrayList<>();

        String sql = """
            SELECT start_date, end_date
            FROM (
                SELECT 
                    CAST(b.start_time AS date) AS start_date,
                    CAST(b.end_time AS date) AS end_date
                FROM booking b
                WHERE b.car_id = ?
                  AND b.status IN ('PENDING_APPROVAL', 'AWAITING_PAYMENT', 'CONFIRMED', 'ACTIVE')

                UNION ALL

                SELECT
                    CAST(rc.contract_start_time AS date) AS start_date,
                    CAST(rc.contract_end_time AS date) AS end_date
                FROM rental_contract rc
                WHERE rc.car_id = ?
                  AND rc.contract_status IN ('WAITING_CUSTOMER_CONFIRM', 'ACTIVE', 'ONGOING')

                UNION ALL

                SELECT
                    m.start_date,
                    m.end_date
                FROM car_maintenance m
                WHERE m.car_id = ?
                  AND m.status IN ('SCHEDULED', 'IN_PROGRESS')
                  AND (? IS NULL OR m.maintenance_id <> ?)
            ) x
            ORDER BY start_date
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, carId);
            ps.setInt(2, carId);
            ps.setInt(3, carId);
            ps.setObject(4, excludeMaintenanceId);
            ps.setObject(5, excludeMaintenanceId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ranges.add(new String[]{
                        rs.getDate("start_date").toString(),
                        rs.getDate("end_date").toString()
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ranges;
    }

    public boolean hasActiveMaintenanceForCar(int carId, Integer excludeMaintenanceId) {
        String sql = """
            SELECT TOP 1 1
            FROM car_maintenance
            WHERE car_id = ?
              AND status IN ('SCHEDULED', 'IN_PROGRESS')
              AND (? IS NULL OR maintenance_id <> ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, carId);
            ps.setObject(2, excludeMaintenanceId);
            ps.setObject(3, excludeMaintenanceId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
