package DALs;

import Utils.DBContext;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.ReportModel;

public class ReportDAO extends DBContext {

    public List<ReportModel> findAllRentalReports(String startDate, String endDate) {
        List<ReportModel> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT
                rc.contract_id,
                b.booking_id,
                cu.full_name AS customer_name,
                cu.phone AS customer_phone,
                c.plate_number,
                br.brand_name,
                ct.type_name,
                c.model_name,
                CONVERT(date, rc.contract_start_time) AS start_date,
                CONVERT(date, rc.contract_end_time) AS end_date,
                CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time)) AS revenue_date,
                DATEDIFF(DAY, rc.contract_start_time, rc.contract_end_time) + 1 AS rental_days,
                rc.contract_status AS status,
                s.full_name AS staff_name,
                rc.note,
                ISNULL(rc.total_amount, 0) AS total_amount
            FROM rental_contract rc
            JOIN booking b ON rc.booking_id = b.booking_id
            JOIN customer cu ON b.customer_id = cu.customer_id
            JOIN cars c ON rc.car_id = c.car_id
            JOIN brand br ON c.brand_id = br.brand_id
            JOIN cars_type ct ON c.type_id = ct.type_id
            LEFT JOIN staff s ON rc.staff_id = s.staff_id
            WHERE rc.contract_status = 'COMPLETED'
            """);

        List<Object> params = new ArrayList<>();

        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time)) >= ?");
            params.add(Date.valueOf(startDate));
        }

        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time)) <= ?");
            params.add(Date.valueOf(endDate));
        }

        sql.append(" ORDER BY revenue_date ASC, rc.contract_id ASC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReportModel r = new ReportModel();

                    r.setContractId(rs.getInt("contract_id"));
                    r.setBookingId(rs.getInt("booking_id"));
                    r.setCustomerName(rs.getString("customer_name"));
                    r.setCustomerPhone(rs.getString("customer_phone"));
                    r.setPlateNumber(rs.getString("plate_number"));
                    r.setBrandName(rs.getString("brand_name"));
                    r.setTypeName(rs.getString("type_name"));
                    r.setModelName(rs.getString("model_name"));
                    r.setStartDate(rs.getDate("start_date"));
                    r.setEndDate(rs.getDate("end_date"));
                    r.setRevenueDate(rs.getDate("revenue_date"));
                    r.setRentalDays(rs.getInt("rental_days"));
                    r.setTotalPrice(rs.getBigDecimal("total_amount"));
                    r.setStatus(rs.getString("status"));
                    r.setStaffName(rs.getString("staff_name"));
                    r.setNote(rs.getString("note"));

                    list.add(r);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<ReportModel> findRevenueReports(String startDate, String endDate) {
        List<ReportModel> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT
                rc.contract_id,
                b.booking_id,
                cu.full_name AS customer_name,
                cu.phone AS customer_phone,
                c.plate_number,
                br.brand_name,
                ct.type_name,
                c.model_name,
                CONVERT(date, rc.contract_start_time) AS start_date,
                CONVERT(date, rc.contract_end_time) AS end_date,
                CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time)) AS revenue_date,
                DATEDIFF(DAY, rc.contract_start_time, rc.contract_end_time) + 1 AS rental_days,
                rc.contract_status AS status,
                s.full_name AS staff_name,
                rc.note,
                ISNULL(rc.total_amount, 0) AS total_amount
            FROM rental_contract rc
            JOIN booking b ON rc.booking_id = b.booking_id
            JOIN customer cu ON b.customer_id = cu.customer_id
            JOIN cars c ON rc.car_id = c.car_id
            JOIN brand br ON c.brand_id = br.brand_id
            JOIN cars_type ct ON c.type_id = ct.type_id
            LEFT JOIN staff s ON rc.staff_id = s.staff_id
            WHERE rc.contract_status = 'COMPLETED'
            """);

        List<Object> params = new ArrayList<>();

        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time)) >= ?");
            params.add(Date.valueOf(startDate));
        }

        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time)) <= ?");
            params.add(Date.valueOf(endDate));
        }

        sql.append(" ORDER BY revenue_date DESC, rc.contract_id DESC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReportModel r = new ReportModel();

                    r.setContractId(rs.getInt("contract_id"));
                    r.setBookingId(rs.getInt("booking_id"));
                    r.setCustomerName(rs.getString("customer_name"));
                    r.setCustomerPhone(rs.getString("customer_phone"));
                    r.setPlateNumber(rs.getString("plate_number"));
                    r.setBrandName(rs.getString("brand_name"));
                    r.setTypeName(rs.getString("type_name"));
                    r.setModelName(rs.getString("model_name"));
                    r.setStartDate(rs.getDate("start_date"));
                    r.setEndDate(rs.getDate("end_date"));
                    r.setRevenueDate(rs.getDate("revenue_date"));
                    r.setRentalDays(rs.getInt("rental_days"));
                    r.setTotalPrice(rs.getBigDecimal("total_amount"));
                    r.setStatus(rs.getString("status"));
                    r.setStaffName(rs.getString("staff_name"));
                    r.setNote(rs.getString("note"));

                    list.add(r);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<ReportModel> findRevenueByDate(String startDate, String endDate) {
        List<ReportModel> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT
                CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time)) AS revenue_date,
                SUM(ISNULL(rc.total_amount, 0)) AS total_revenue,
                COUNT(rc.contract_id) AS rental_count
            FROM rental_contract rc
            WHERE rc.contract_status = 'COMPLETED'
            """);

        List<Object> params = new ArrayList<>();

        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time)) >= ?");
            params.add(Date.valueOf(startDate));
        }

        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time)) <= ?");
            params.add(Date.valueOf(endDate));
        }

        sql.append("""
            GROUP BY CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time))
            ORDER BY revenue_date ASC
            """);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReportModel r = new ReportModel();

                    r.setRevenueDate(rs.getDate("revenue_date"));
                    r.setTotalPrice(rs.getBigDecimal("total_revenue"));
                    r.setRentalCount(rs.getLong("rental_count"));

                    list.add(r);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public Map<String, Object> getReportSummary(String startDate, String endDate) {
        Map<String, Object> summary = new HashMap<>();

        StringBuilder sql = new StringBuilder("""
            SELECT
                COUNT(rc.contract_id) AS total_trips,
                SUM(ISNULL(rc.total_amount, 0)) AS total_revenue,
                SUM(DATEDIFF(DAY, rc.contract_start_time, rc.contract_end_time) + 1) AS total_rental_days
            FROM rental_contract rc
            WHERE rc.contract_status = 'COMPLETED'
            """);

        List<Object> params = new ArrayList<>();

        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time)) >= ?");
            params.add(Date.valueOf(startDate));
        }

        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time)) <= ?");
            params.add(Date.valueOf(endDate));
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal totalRevenue = rs.getBigDecimal("total_revenue");
                    long totalTrips = rs.getLong("total_trips");
                    int totalRentalDays = rs.getInt("total_rental_days");

                    int totalCars = countAllCars();
                    int periodDays = 30;

                    if (startDate != null && !startDate.isEmpty()
                            && endDate != null && !endDate.isEmpty()) {
                        try {
                            LocalDate s = Date.valueOf(startDate).toLocalDate();
                            LocalDate e = Date.valueOf(endDate).toLocalDate();

                            periodDays = (int) ChronoUnit.DAYS.between(s, e) + 1;
                        } catch (Exception e) {
                            periodDays = 30;
                        }
                    }

                    double utilization = 0;

                    if (totalCars > 0 && periodDays > 0) {
                        utilization = ((double) totalRentalDays / (double) (totalCars * periodDays)) * 100;
                    }

                    summary.put("totalRevenue", totalRevenue == null ? BigDecimal.ZERO : totalRevenue);
                    summary.put("totalTrips", totalTrips);
                    summary.put("utilization", Math.round(utilization * 10.0) / 10.0);
                    summary.put("periodDays", periodDays);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();

            summary.put("totalRevenue", BigDecimal.ZERO);
            summary.put("totalTrips", 0L);
            summary.put("utilization", 0.0);
            summary.put("periodDays", 30);
        }

        return summary;
    }

    public List<ReportModel> findVehicleUsageReports(String startDate, String endDate) {
        List<ReportModel> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT
                c.car_id,
                c.plate_number,
                c.model_name,
                br.brand_name,
                ct.type_name,
                COUNT(rc.contract_id) AS rental_count,
                ISNULL(SUM(DATEDIFF(DAY, rc.contract_start_time, rc.contract_end_time) + 1), 0) AS total_rental_days,
                ISNULL(SUM(rc.total_amount), 0) AS total_revenue,
                MAX(CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time))) AS last_rental_date,
                MAX(m.end_date) AS last_maintenance_date
            FROM cars c
            JOIN brand br ON c.brand_id = br.brand_id
            JOIN cars_type ct ON c.type_id = ct.type_id
            LEFT JOIN rental_contract rc ON c.car_id = rc.car_id
                AND rc.contract_status = 'COMPLETED'
            LEFT JOIN car_maintenance m ON c.car_id = m.car_id
                AND m.status = 'COMPLETED'
            WHERE 1 = 1
            """);

        List<Object> params = new ArrayList<>();

        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND (rc.contract_id IS NULL OR CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time)) >= ?)");
            params.add(Date.valueOf(startDate));
        }

        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND (rc.contract_id IS NULL OR CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time)) <= ?)");
            params.add(Date.valueOf(endDate));
        }

        sql.append("""
            GROUP BY c.car_id, c.plate_number, c.model_name, br.brand_name, ct.type_name
            ORDER BY total_rental_days DESC, rental_count DESC
            """);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReportModel r = new ReportModel();

                    r.setCarId(rs.getInt("car_id"));
                    r.setPlateNumber(rs.getString("plate_number"));
                    r.setModelName(rs.getString("model_name"));
                    r.setBrandName(rs.getString("brand_name"));
                    r.setTypeName(rs.getString("type_name"));
                    r.setRentalCount(rs.getLong("rental_count"));
                    r.setTotalRentalDays(rs.getInt("total_rental_days"));
                    r.setTotalRevenue(rs.getBigDecimal("total_revenue"));
                    r.setLastRentalDate(rs.getDate("last_rental_date"));
                    r.setLastMaintenanceDate(rs.getDate("last_maintenance_date"));

                    list.add(r);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public Map<String, Object> getVehicleUtilization(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();

        String pieSql = """
            SELECT
                COUNT(CASE 
                    WHEN c.status = 'BOOKED'
                      OR rc.contract_status IN ('CREATED', 'WAITING_CUSTOMER_CONFIRM', 'ACTIVE')
                    THEN 1 
                END) AS rented,
                COUNT(CASE 
                    WHEN c.status = 'AVAILABLE' 
                    THEN 1 
                END) AS available,
                COUNT(CASE 
                    WHEN c.status = 'MAINTENANCE' 
                    THEN 1 
                END) AS maintenance
            FROM cars c
            LEFT JOIN rental_contract rc ON c.car_id = rc.car_id
                AND rc.contract_status IN ('CREATED', 'WAITING_CUSTOMER_CONFIRM', 'ACTIVE')
            """;

        StringBuilder barSql = new StringBuilder("""
            SELECT TOP 10
                c.plate_number,
                c.model_name,
                br.brand_name,
                ISNULL(SUM(DATEDIFF(DAY, rc.contract_start_time, rc.contract_end_time) + 1), 0) AS total_rental_days
            FROM cars c
            JOIN brand br ON c.brand_id = br.brand_id
            LEFT JOIN rental_contract rc ON c.car_id = rc.car_id
                AND rc.contract_status = 'COMPLETED'
            WHERE 1 = 1
            """);

        List<Object> params = new ArrayList<>();

        if (startDate != null && !startDate.isEmpty()) {
            barSql.append(" AND (rc.contract_id IS NULL OR CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time)) >= ?)");
            params.add(Date.valueOf(startDate));
        }

        if (endDate != null && !endDate.isEmpty()) {
            barSql.append(" AND (rc.contract_id IS NULL OR CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time)) <= ?)");
            params.add(Date.valueOf(endDate));
        }

        barSql.append("""
            GROUP BY c.car_id, c.plate_number, c.model_name, br.brand_name
            ORDER BY total_rental_days DESC
            """);

        try {
            try (PreparedStatement ps = connection.prepareStatement(pieSql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    Map<String, Integer> pieData = new HashMap<>();

                    pieData.put("rented", rs.getInt("rented"));
                    pieData.put("available", rs.getInt("available"));
                    pieData.put("maintenance", rs.getInt("maintenance"));

                    result.put("pieData", pieData);
                }
            }

            try (PreparedStatement ps = connection.prepareStatement(barSql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }

                try (ResultSet rs = ps.executeQuery()) {
                    List<Map<String, Object>> barList = new ArrayList<>();

                    while (rs.next()) {
                        Map<String, Object> item = new HashMap<>();

                        item.put("plateNumber", rs.getString("plate_number"));
                        item.put("modelName", rs.getString("model_name"));
                        item.put("brandName", rs.getString("brand_name"));
                        item.put("rentalDays", rs.getInt("total_rental_days"));

                        barList.add(item);
                    }

                    result.put("barData", barList);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    private int countAllCars() {
        String sql = "SELECT COUNT(*) FROM cars";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}