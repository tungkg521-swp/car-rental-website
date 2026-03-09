package DALs;

import models.VoucherModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import Utils.DBContext;

public class VoucherDAO extends DBContext {

    public VoucherDAO() {
        super();
    }

    // Get all vouchers
   public List<VoucherModel> getAllVouchers() {
    List<VoucherModel> list = new ArrayList<>();

    String sql = """
        SELECT voucher_id, code, discount_type, discount_value, start_date, end_date, 
               status, created_at, max_uses, used_count, min_booking_amount
        FROM voucher
        ORDER BY voucher_id ASC
        """;

    try (PreparedStatement ps = connection.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            list.add(mapResultSetToVoucher(rs));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return list;
}
    // Find voucher by ID
    public VoucherModel findById(int voucherId) {
        String sql = """
            SELECT voucher_id, code, discount_type, discount_value, start_date, end_date, 
                   status, created_at, max_uses, used_count, min_booking_amount
            FROM voucher
            WHERE voucher_id = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, voucherId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVoucher(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Find voucher by code (ĐÃ SỬA LỖI)
    public VoucherModel findByCode(String code) {
        String sql = """
            SELECT voucher_id, code, discount_type, discount_value, start_date, end_date, 
                   status, created_at, max_uses, used_count, min_booking_amount
            FROM voucher
            WHERE code = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code); // QUAN TRỌNG: Set parameter
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVoucher(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Insert new voucher
   public boolean insert(VoucherModel voucher) {

    String sql = """
        INSERT INTO voucher
        (code, discount_type, discount_value, start_date, end_date,
         status, created_at, max_uses, used_count, min_booking_amount)
        VALUES (?, ?, ?, GETDATE(), ?, ?, GETDATE(), ?, ?, ?)
        """;

    try (PreparedStatement ps = connection.prepareStatement(sql)) {

        ps.setString(1, voucher.getCode());
        ps.setString(2, voucher.getType());
        ps.setBigDecimal(3, voucher.getDiscount());
        ps.setDate(4, voucher.getExpireDate());

        // ⚠ QUAN TRỌNG: DB là varchar(20)
        ps.setString(5, voucher.isStatus() ? "active" : "inactive");

        ps.setInt(6, voucher.getMaxUses());
        ps.setInt(7, 0); // used_count mặc định 0
        ps.setBigDecimal(8, voucher.getMinBookingAmount());

        int result = ps.executeUpdate();
        return result > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("SQL Error: " + e.getMessage());
    }
    return false;
}
    // Update existing voucher
   public boolean update(VoucherModel voucher) {

    String sql = """
        UPDATE voucher
        SET code = ?, 
            discount_type = ?, 
            discount_value = ?, 
            end_date = ?, 
            status = ?, 
            max_uses = ?, 
            min_booking_amount = ?
        WHERE voucher_id = ?
        """;

    try (PreparedStatement ps = connection.prepareStatement(sql)) {

        ps.setString(1, voucher.getCode());
        ps.setString(2, voucher.getType());
        ps.setBigDecimal(3, voucher.getDiscount());
        ps.setDate(4, voucher.getExpireDate());

        // ⚠ FIX
        ps.setString(5, voucher.isStatus() ? "active" : "inactive");

        ps.setInt(6, voucher.getMaxUses());
        ps.setBigDecimal(7, voucher.getMinBookingAmount());
        ps.setInt(8, voucher.getVoucherId());

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("SQL Error: " + e.getMessage());
    }
    return false;
}

    // Delete voucher
    public boolean delete(int voucherId) {
        String sql = "DELETE FROM voucher WHERE voucher_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, voucherId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to map ResultSet to VoucherModel
    private VoucherModel mapResultSetToVoucher(ResultSet rs) throws SQLException {
        return new VoucherModel(
            rs.getInt("voucher_id"),
            rs.getString("code"),
            rs.getBigDecimal("discount_value"),
            rs.getString("discount_type"),
            rs.getDate("start_date"),
            rs.getDate("end_date"),
            parseStatus(rs.getObject("status")),
            rs.getTimestamp("created_at") != null ? 
                new java.sql.Date(rs.getTimestamp("created_at").getTime()) : null,
            rs.getInt("max_uses"),
            rs.getInt("used_count"),
            rs.getBigDecimal("min_booking_amount")
        );
    }

    // Parse status from database to string
    private String parseStatus(Object statusObj) {
        if (statusObj == null) return "inactive";
        
        if (statusObj instanceof Boolean) {
            return (Boolean) statusObj ? "active" : "inactive";
        }
        
        if (statusObj instanceof Number) {
            int intValue = ((Number) statusObj).intValue();
            return intValue == 1 ? "active" : "inactive";
        }
        
        String strValue = String.valueOf(statusObj).toLowerCase();
        if ("1".equals(strValue) || "true".equals(strValue) || "active".equals(strValue)) {
            return "active";
        }
        return "inactive";
    }

    // Kiểm tra code đã tồn tại chưa
    public boolean existsByCode(String code) {
        String sql = "SELECT COUNT(*) FROM voucher WHERE code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}