package DALs;

import Utils.DBContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.ReportModel;

public class ReportDAO extends DBContext {

    public List<ReportModel> findAllRentalReports() {
        List<ReportModel> list = new ArrayList<>();
        String sql = """
            SELECT 
                COALESCE(c.contract_id, b.booking_id) AS rental_id,
                c.contract_id,
                b.booking_id,
                cust.full_name AS customer_name,
                cust.phone AS customer_phone,
                car.plate_number,
                br.brand_name,
                ct.type_name,
                car.model_name,
                COALESCE(c.contract_start_date, b.start_date) AS start_date,
                COALESCE(c.contract_end_date, b.end_date) AS end_date,
                DATEDIFF(DAY, COALESCE(c.contract_start_date, b.start_date), 
                               COALESCE(c.contract_end_date, b.end_date)) + 1 AS rental_days,
                COALESCE(c.total_amount, b.total_estimated_price) AS total_price,
                COALESCE(c.contract_status, b.status) AS status,
                st.full_name AS staff_name,
                COALESCE(c.note, b.note) AS note
            FROM dbo.booking b
            LEFT JOIN dbo.rental_contract c ON b.booking_id = c.booking_id
            INNER JOIN dbo.customer cust ON b.customer_id = cust.customer_id
            INNER JOIN dbo.cars car ON b.car_id = car.car_id
            INNER JOIN dbo.brand br ON car.brand_id = br.brand_id
            INNER JOIN dbo.cars_type ct ON car.type_id = ct.type_id
            LEFT JOIN dbo.staff st ON COALESCE(c.staff_id, b.staff_id) = st.staff_id
            ORDER BY COALESCE(c.signed_at, b.booking_date) DESC
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ReportModel r = new ReportModel();
                r.setContractId(rs.getInt("contract_id"));
                // Nếu contract_id null thì fallback booking_id (có thể hiển thị trong view)
                if (rs.wasNull()) {
                    r.setContractId(null);
                }
                r.setBookingId(rs.getInt("booking_id"));
                r.setCustomerName(rs.getString("customer_name"));
                r.setCustomerPhone(rs.getString("customer_phone"));
                r.setPlateNumber(rs.getString("plate_number"));
                r.setBrandName(rs.getString("brand_name"));
                r.setTypeName(rs.getString("type_name"));
                r.setModelName(rs.getString("model_name"));
                r.setStartDate(rs.getDate("start_date"));
                r.setEndDate(rs.getDate("end_date"));
                r.setRentalDays(rs.getInt("rental_days"));
                r.setTotalPrice(rs.getBigDecimal("total_price"));
                r.setStatus(rs.getString("status"));
                r.setStaffName(rs.getString("staff_name"));
                r.setNote(rs.getString("note"));

                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ReportModel> findVehicleUsageReport() {
        List<ReportModel> list = new ArrayList<>();

        String sql = """
        SELECT 
            c.car_id,
            c.plate_number,
            c.model_name,
            br.brand_name,
            ct.type_name,
            
            COUNT(rc.contract_id) AS rental_count,
            COALESCE(SUM(DATEDIFF(DAY, rc.contract_start_date, rc.contract_end_date) + 1), 0) AS total_rental_days,
            COALESCE(SUM(rc.total_amount), 0) AS total_revenue,
            MAX(rc.contract_start_date) AS last_rental_date,           -- dùng end_date vì signed_at đang NULL
            MAX(m.scheduled_date) AS last_maintenance_date           -- lấy từ bảng car_maintenance
            
        FROM dbo.cars c
        INNER JOIN dbo.brand br ON c.brand_id = br.brand_id
        INNER JOIN dbo.cars_type ct ON c.type_id = ct.type_id
        
        LEFT JOIN dbo.rental_contract rc 
            ON c.car_id = rc.car_id 
            AND rc.contract_status = 'COMPLETED'                     -- chỉ lấy hợp đồng đã hoàn thành
        
        LEFT JOIN dbo.car_maintenance m 
            ON c.car_id = m.car_id 
            AND m.status = 'COMPLETED'                               -- bảo dưỡng đã hoàn thành
        
        GROUP BY 
            c.car_id, c.plate_number, c.model_name, 
            br.brand_name, ct.type_name
            
        ORDER BY total_rental_days DESC, rental_count DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

 public List<ReportModel> findRevenueReports() {
    List<ReportModel> list = new ArrayList<>();

    String sql = """
        SELECT 
            c.contract_id,
            b.booking_id,
            cust.full_name AS customer_name,
            cust.phone AS customer_phone,
            car.plate_number,
            br.brand_name,
            ct.type_name,
            car.model_name,
            COALESCE(c.contract_start_date, b.start_date) AS start_date,
            COALESCE(c.contract_end_date, b.end_date) AS end_date,
            DATEDIFF(DAY, COALESCE(c.contract_start_date, b.start_date), 
                           COALESCE(c.contract_end_date, b.end_date)) + 1 AS duration_days,
            COALESCE(c.total_amount, b.total_estimated_price, 0) AS amount,
            COALESCE(c.contract_status, b.status) AS status,
            st.full_name AS staff_name,
            COALESCE(c.note, b.note) AS note,
            COALESCE(c.contract_end_date, b.end_date) AS revenue_date
        FROM dbo.booking b
        LEFT JOIN dbo.rental_contract c ON b.booking_id = c.booking_id
        INNER JOIN dbo.customer cust ON b.customer_id = cust.customer_id
        INNER JOIN dbo.cars car ON b.car_id = car.car_id
        INNER JOIN dbo.brand br ON car.brand_id = br.brand_id
        INNER JOIN dbo.cars_type ct ON car.type_id = ct.type_id
        LEFT JOIN dbo.staff st ON COALESCE(c.staff_id, b.staff_id) = st.staff_id
        WHERE COALESCE(c.contract_status, b.status) IN ('COMPLETED')
        ORDER BY revenue_date DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            ReportModel r = new ReportModel();

            // Xử lý contract_id và booking_id (tránh null)
            Integer contractIdVal = rs.getInt("contract_id");
            if (rs.wasNull()) {
                r.setContractId(null);
            } else {
                r.setContractId(contractIdVal);
            }
            r.setBookingId(rs.getInt("booking_id"));

            r.setCustomerName(rs.getString("customer_name"));
            r.setCustomerPhone(rs.getString("customer_phone"));
            r.setPlateNumber(rs.getString("plate_number"));
            r.setBrandName(rs.getString("brand_name"));
            r.setTypeName(rs.getString("type_name"));
            r.setModelName(rs.getString("model_name"));

            r.setStartDate(rs.getDate("start_date"));
            r.setEndDate(rs.getDate("end_date"));

            // Mapping đúng tên setter trong model
            r.setRentalDays(rs.getInt("duration_days"));
            r.setTotalPrice(rs.getBigDecimal("amount"));

            r.setStatus(rs.getString("status"));
            r.setStaffName(rs.getString("staff_name"));
            r.setNote(rs.getString("note"));

            r.setRevenueDate(rs.getDate("revenue_date"));

//            // Log debug để kiểm tra dữ liệu có vào không
//            System.out.println("Revenue loaded: " +
//                    "ContractID=" + r.getContractId() +
//                    ", BookingID=" + r.getBookingId() +
//                    ", Amount=" + r.getTotalPrice() +
//                    ", Days=" + r.getRentalDays() +
//                    ", RevenueDate=" + r.getRevenueDate());

            list.add(r);
        }

        // Log tổng số record sau khi query
        System.out.println("Total revenue records loaded: " + list.size());

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return list;
}
}
