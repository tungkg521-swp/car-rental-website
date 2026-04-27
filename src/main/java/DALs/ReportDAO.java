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

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    /*
     * Doanh thu thực của đơn:
     * - Không cộng tiền cọc deposit_amount.
     * - Không ưu tiên final_amount vì trong flow của bạn final_amount có thể là số tiền cần thanh toán thêm.
     * - Công thức đúng theo DB hiện tại:
     *      total_amount + extra_km_fee + other_extra_fee
     */
    private BigDecimal calculateRevenue(BigDecimal totalAmount,
                                        BigDecimal extraKmFee,
                                        BigDecimal contractExtraFee,
                                        BigDecimal checkExtraFee) {

        BigDecimal rental = safe(totalAmount);
        BigDecimal kmFee = safe(extraKmFee);

        /*
         * Nếu controller có cập nhật phí phát sinh khác vào rental_contract.extra_fee_total
         * thì dùng cột đó.
         * Nếu rental_contract.extra_fee_total = 0 nhưng phí nằm ở car_check.extra_fee_total
         * thì fallback qua car_check.
         *
         * Làm vậy để tránh bị cộng trùng cùng một khoản phí.
         */
        BigDecimal otherFee = safe(contractExtraFee).compareTo(BigDecimal.ZERO) > 0
                ? safe(contractExtraFee)
                : safe(checkExtraFee);

        return rental.add(kmFee).add(otherFee);
    }

    private String returnCheckFeeJoinSql() {
        return """
            LEFT JOIN (
                SELECT 
                    contract_id,
                    SUM(ISNULL(extra_fee_total, 0)) AS return_check_extra_fee
                FROM car_check
                WHERE check_type = 'RETURN'
                   OR check_result = 'RETURN_CHECK'
                GROUP BY contract_id
            ) return_fee
                ON rc.contract_id = return_fee.contract_id
            """;
    }

    public List<ReportModel> findAllRentalReports(String startDate, String endDate) {
        List<ReportModel> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("""
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

                rc.total_amount,
                rc.extra_km_fee,
                rc.extra_fee_total,
                ISNULL(return_fee.return_check_extra_fee, 0) AS return_check_extra_fee
            FROM rental_contract rc
            INNER JOIN booking b 
                ON rc.booking_id = b.booking_id
            INNER JOIN customer cu 
                ON b.customer_id = cu.customer_id
            INNER JOIN cars c 
                ON rc.car_id = c.car_id
            INNER JOIN brand br 
                ON c.brand_id = br.brand_id
            INNER JOIN cars_type ct 
                ON c.type_id = ct.type_id
            LEFT JOIN staff s 
                ON rc.staff_id = s.staff_id
            """);

        sql.append(returnCheckFeeJoinSql());

        sql.append("""
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
                    BigDecimal revenue = calculateRevenue(
                            rs.getBigDecimal("total_amount"),
                            rs.getBigDecimal("extra_km_fee"),
                            rs.getBigDecimal("extra_fee_total"),
                            rs.getBigDecimal("return_check_extra_fee")
                    );

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
                    r.setTotalPrice(revenue);
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

        StringBuilder sql = new StringBuilder();
        sql.append("""
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

                rc.total_amount,
                rc.extra_km_fee,
                rc.extra_fee_total,
                ISNULL(return_fee.return_check_extra_fee, 0) AS return_check_extra_fee
            FROM rental_contract rc
            INNER JOIN booking b 
                ON rc.booking_id = b.booking_id
            INNER JOIN customer cu 
                ON b.customer_id = cu.customer_id
            INNER JOIN cars c 
                ON rc.car_id = c.car_id
            INNER JOIN brand br 
                ON c.brand_id = br.brand_id
            INNER JOIN cars_type ct 
                ON c.type_id = ct.type_id
            LEFT JOIN staff s 
                ON rc.staff_id = s.staff_id
            """);

        sql.append(returnCheckFeeJoinSql());

        sql.append("""
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
                    BigDecimal revenue = calculateRevenue(
                            rs.getBigDecimal("total_amount"),
                            rs.getBigDecimal("extra_km_fee"),
                            rs.getBigDecimal("extra_fee_total"),
                            rs.getBigDecimal("return_check_extra_fee")
                    );

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
                    r.setTotalPrice(revenue);
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

        StringBuilder sql = new StringBuilder();
        sql.append("""
            SELECT
                CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time)) AS revenue_date,
                rc.total_amount,
                rc.extra_km_fee,
                rc.extra_fee_total,
                ISNULL(return_fee.return_check_extra_fee, 0) AS return_check_extra_fee
            FROM rental_contract rc
            INNER JOIN booking b 
                ON rc.booking_id = b.booking_id
            """);

        sql.append(returnCheckFeeJoinSql());

        sql.append("""
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

            Map<Date, BigDecimal> dailyRevenueMap = new HashMap<>();
            Map<Date, Long> dailyCountMap = new HashMap<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date revenueDate = rs.getDate("revenue_date");

                    if (revenueDate == null) {
                        continue;
                    }

                    BigDecimal revenue = calculateRevenue(
                            rs.getBigDecimal("total_amount"),
                            rs.getBigDecimal("extra_km_fee"),
                            rs.getBigDecimal("extra_fee_total"),
                            rs.getBigDecimal("return_check_extra_fee")
                    );

                    dailyRevenueMap.put(
                            revenueDate,
                            dailyRevenueMap.getOrDefault(revenueDate, BigDecimal.ZERO).add(revenue)
                    );

                    dailyCountMap.put(
                            revenueDate,
                            dailyCountMap.getOrDefault(revenueDate, 0L) + 1
                    );
                }
            }

            for (Map.Entry<Date, BigDecimal> entry : dailyRevenueMap.entrySet()) {
                ReportModel r = new ReportModel();

                r.setRevenueDate(entry.getKey());
                r.setTotalPrice(entry.getValue());
                r.setRentalCount(dailyCountMap.getOrDefault(entry.getKey(), 0L));

                list.add(r);
            }

            list.sort((a, b) -> a.getRevenueDate().compareTo(b.getRevenueDate()));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public Map<String, Object> getReportSummary(String startDate, String endDate) {
        Map<String, Object> summary = new HashMap<>();

        StringBuilder sql = new StringBuilder();
        sql.append("""
            SELECT
                rc.contract_id,
                rc.car_id,
                rc.contract_start_time,
                rc.contract_end_time,
                rc.actual_return_time,

                rc.total_amount,
                rc.extra_km_fee,
                rc.extra_fee_total,
                ISNULL(return_fee.return_check_extra_fee, 0) AS return_check_extra_fee
            FROM rental_contract rc
            INNER JOIN booking b 
                ON rc.booking_id = b.booking_id
            """);

        sql.append(returnCheckFeeJoinSql());

        sql.append("""
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

        long totalTrips = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        int totalRentalDays = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    totalTrips++;

                    BigDecimal revenue = calculateRevenue(
                            rs.getBigDecimal("total_amount"),
                            rs.getBigDecimal("extra_km_fee"),
                            rs.getBigDecimal("extra_fee_total"),
                            rs.getBigDecimal("return_check_extra_fee")
                    );

                    totalRevenue = totalRevenue.add(revenue);

                    java.sql.Timestamp startTs = rs.getTimestamp("contract_start_time");
                    java.sql.Timestamp endTs = rs.getTimestamp("contract_end_time");

                    if (startTs != null && endTs != null) {
                        LocalDate s = startTs.toLocalDateTime().toLocalDate();
                        LocalDate e = endTs.toLocalDateTime().toLocalDate();

                        totalRentalDays += (int) ChronoUnit.DAYS.between(s, e) + 1;
                    }
                }
            }

            int totalCars = countAllCars();
            int periodDays = 30;

            if (startDate != null && !startDate.isEmpty()
                    && endDate != null && !endDate.isEmpty()) {
                try {
                    LocalDate s = Date.valueOf(startDate).toLocalDate();
                    LocalDate e = Date.valueOf(endDate).toLocalDate();

                    periodDays = (int) ChronoUnit.DAYS.between(s, e) + 1;
                } catch (Exception ignored) {
                    periodDays = 30;
                }
            }

            double utilization = (totalCars > 0 && periodDays > 0)
                    ? ((double) totalRentalDays / (double) (totalCars * periodDays)) * 100.0
                    : 0.0;

            summary.put("totalRevenue", totalRevenue);
            summary.put("totalTrips", totalTrips);
            summary.put("utilization", Math.round(utilization * 10.0) / 10.0);
            summary.put("periodDays", periodDays);

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

        StringBuilder sql = new StringBuilder();
        sql.append("""
            SELECT
                c.car_id,
                c.plate_number,
                c.model_name,
                br.brand_name,
                ct.type_name,

                COUNT(rc.contract_id) AS rental_count,

                ISNULL(SUM(
                    CASE
                        WHEN rc.contract_id IS NOT NULL
                        THEN DATEDIFF(DAY, rc.contract_start_time, rc.contract_end_time) + 1
                        ELSE 0
                    END
                ), 0) AS total_rental_days,

                MAX(CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time))) AS last_rental_date,

                MAX(m.last_maintenance_date) AS last_maintenance_date,

                ISNULL(SUM(
                    CASE
                        WHEN rc.contract_id IS NOT NULL THEN
                            ISNULL(rc.total_amount, 0)
                            + ISNULL(rc.extra_km_fee, 0)
                            + CASE 
                                WHEN ISNULL(rc.extra_fee_total, 0) > 0
                                    THEN ISNULL(rc.extra_fee_total, 0)
                                ELSE ISNULL(return_fee.return_check_extra_fee, 0)
                              END
                        ELSE 0
                    END
                ), 0) AS total_revenue
            FROM cars c
            INNER JOIN brand br 
                ON c.brand_id = br.brand_id
            INNER JOIN cars_type ct 
                ON c.type_id = ct.type_id
            LEFT JOIN rental_contract rc
                ON c.car_id = rc.car_id
               AND rc.contract_status = 'COMPLETED'
            LEFT JOIN booking b
                ON rc.booking_id = b.booking_id
            LEFT JOIN (
                SELECT 
                    contract_id,
                    SUM(ISNULL(extra_fee_total, 0)) AS return_check_extra_fee
                FROM car_check
                WHERE check_type = 'RETURN'
                   OR check_result = 'RETURN_CHECK'
                GROUP BY contract_id
            ) return_fee
                ON rc.contract_id = return_fee.contract_id
            LEFT JOIN (
                SELECT 
                    car_id, 
                    MAX(end_date) AS last_maintenance_date
                FROM car_maintenance
                WHERE status = 'COMPLETED'
                GROUP BY car_id
            ) m
                ON c.car_id = m.car_id
            WHERE 1 = 1
            """);

        List<Object> params = new ArrayList<>();

        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND (rc.contract_end_time IS NULL OR CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time)) >= ?)");
            params.add(Date.valueOf(startDate));
        }

        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND (rc.contract_end_time IS NULL OR CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time)) <= ?)");
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
            LEFT JOIN rental_contract rc
                ON c.car_id = rc.car_id
               AND rc.contract_status IN ('CREATED', 'WAITING_CUSTOMER_CONFIRM', 'ACTIVE')
            """;

        StringBuilder barSql = new StringBuilder("""
            SELECT TOP 10
                c.plate_number,
                c.model_name,
                br.brand_name,

                ISNULL(SUM(
                    CASE
                        WHEN rc.contract_id IS NOT NULL
                        THEN DATEDIFF(DAY, rc.contract_start_time, rc.contract_end_time) + 1
                        ELSE 0
                    END
                ), 0) AS total_rental_days
            FROM cars c
            INNER JOIN brand br 
                ON c.brand_id = br.brand_id
            LEFT JOIN rental_contract rc
                ON c.car_id = rc.car_id
               AND rc.contract_status = 'COMPLETED'
            WHERE 1 = 1
            """);

        List<Object> params = new ArrayList<>();

        if (startDate != null && !startDate.isEmpty()) {
            barSql.append(" AND (rc.contract_end_time IS NULL OR CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time)) >= ?)");
            params.add(Date.valueOf(startDate));
        }

        if (endDate != null && !endDate.isEmpty()) {
            barSql.append(" AND (rc.contract_end_time IS NULL OR CONVERT(date, ISNULL(rc.actual_return_time, rc.contract_end_time)) <= ?)");
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