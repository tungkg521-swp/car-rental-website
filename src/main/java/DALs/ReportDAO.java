package DALs;

import Utils.DBContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.RentalReportModel;

public class ReportDAO extends DBContext {

    public List<RentalReportModel> findAllRentalReports() {
        List<RentalReportModel> list = new ArrayList<>();
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

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                RentalReportModel r = new RentalReportModel();
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
}