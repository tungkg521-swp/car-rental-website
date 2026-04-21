package DALs;

import models.VoucherModel;
import java.time.LocalDate;
import java.sql.Date;
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

    public List<VoucherModel> getAllVouchers() {
        List<VoucherModel> list = new ArrayList<>();

        String sql = """
        SELECT voucher_id, code, discount_type, discount_value, start_date, end_date, 
               status, created_at, max_uses, used_count, min_booking_amount
        FROM voucher
        ORDER BY voucher_id ASC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToVoucher(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<VoucherModel> getActiveVouchers() {
        List<VoucherModel> list = new ArrayList<>();

        String sql = """
        SELECT voucher_id, code, discount_value, discount_type,
               start_date, end_date, status,
               max_uses, used_count, min_booking_amount
        FROM voucher
        WHERE LOWER(status) = 'active'
          AND end_date >= CAST(GETDATE() AS DATE)
          AND (max_uses IS NULL OR ISNULL(used_count, 0) < max_uses)
        ORDER BY voucher_id DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                VoucherModel voucher = new VoucherModel(
                        rs.getInt("voucher_id"),
                        rs.getString("code"),
                        rs.getBigDecimal("discount_value"),
                        rs.getString("discount_type"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getString("status"),
                        null,
                        rs.getInt("max_uses"),
                        rs.getInt("used_count"),
                        rs.getBigDecimal("min_booking_amount")
                );
                list.add(voucher);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

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

    public VoucherModel findByCode(String code) {
        String sql = """
            SELECT voucher_id, code, discount_type, discount_value, start_date, end_date, 
                   status, created_at, max_uses, used_count, min_booking_amount
            FROM voucher
            WHERE code = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);

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

    public boolean insert(VoucherModel voucher) {
        String sql = """
        INSERT INTO voucher
        (code, discount_type, discount_value, start_date, end_date,
         status, created_at, max_uses, used_count, min_booking_amount)
        VALUES (?, ?, ?, GETDATE(), ?, ?, GETDATE(), ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int usedCount = 0;
            String status = buildVoucherStatus(voucher.getExpireDate(), voucher.getMaxUses(), usedCount);

            ps.setString(1, voucher.getCode());
            ps.setString(2, voucher.getType());
            ps.setBigDecimal(3, voucher.getDiscount());
            ps.setDate(4, voucher.getExpireDate());
            ps.setString(5, status);
            ps.setInt(6, voucher.getMaxUses());
            ps.setInt(7, usedCount);
            ps.setBigDecimal(8, voucher.getMinBookingAmount());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL Error: " + e.getMessage());
        }
        return false;
    }

    public boolean update(VoucherModel voucher) {
        String getUsedCountSql = "SELECT used_count FROM voucher WHERE voucher_id = ?";

        try (PreparedStatement getPs = connection.prepareStatement(getUsedCountSql)) {
            getPs.setInt(1, voucher.getVoucherId());

            int usedCount = 0;
            try (ResultSet rs = getPs.executeQuery()) {
                if (rs.next()) {
                    usedCount = rs.getInt("used_count");
                } else {
                    return false;
                }
            }

            String status = buildVoucherStatus(voucher.getExpireDate(), voucher.getMaxUses(), usedCount);

            String updateSql = """
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

            try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                ps.setString(1, voucher.getCode());
                ps.setString(2, voucher.getType());
                ps.setBigDecimal(3, voucher.getDiscount());
                ps.setDate(4, voucher.getExpireDate());
                ps.setString(5, status);
                ps.setInt(6, voucher.getMaxUses());
                ps.setBigDecimal(7, voucher.getMinBookingAmount());
                ps.setInt(8, voucher.getVoucherId());

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL Error: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int voucherId) {
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;

        try {
            conn = connection;
            conn.setAutoCommit(false);

            String clearBookingSql = "UPDATE booking SET voucher_id = NULL WHERE voucher_id = ?";
            ps = conn.prepareStatement(clearBookingSql);
            ps.setInt(1, voucherId);
            ps.executeUpdate();
            ps.close();

            String deleteVoucherSql = "DELETE FROM voucher WHERE voucher_id = ?";
            ps = conn.prepareStatement(deleteVoucherSql);
            ps.setInt(1, voucherId);

            int result = ps.executeUpdate();
            conn.commit();

            return result > 0;

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean markVoucherAsUsed(int voucherId) {
        String selectSql = "SELECT end_date, max_uses, used_count FROM voucher WHERE voucher_id = ?";
        String updateSql = """
        UPDATE voucher
        SET used_count = ?, status = ?
        WHERE voucher_id = ?
        """;

        try (PreparedStatement selectPs = connection.prepareStatement(selectSql)) {
            selectPs.setInt(1, voucherId);

            try (ResultSet rs = selectPs.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }

                Date expireDate = rs.getDate("end_date");
                int maxUses = rs.getInt("max_uses");
                int usedCount = rs.getInt("used_count") + 1;

                String status = buildVoucherStatus(expireDate, maxUses, usedCount);

                try (PreparedStatement updatePs = connection.prepareStatement(updateSql)) {
                    updatePs.setInt(1, usedCount);
                    updatePs.setString(2, status);
                    updatePs.setInt(3, voucherId);
                    return updatePs.executeUpdate() > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void refreshVoucherStatus() {
        String sql = """
        UPDATE voucher
        SET status = CASE
                        WHEN end_date < CAST(GETDATE() AS DATE) THEN 'inactive'
                        WHEN max_uses IS NOT NULL AND ISNULL(used_count, 0) >= max_uses THEN 'inactive'
                        ELSE 'active'
                     END
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private VoucherModel mapResultSetToVoucher(ResultSet rs) throws SQLException {
        return new VoucherModel(
                rs.getInt("voucher_id"),
                rs.getString("code"),
                rs.getBigDecimal("discount_value"),
                rs.getString("discount_type"),
                rs.getDate("start_date"),
                rs.getDate("end_date"),
                parseStatus(rs.getObject("status")),
                rs.getTimestamp("created_at") != null
                ? new java.sql.Date(rs.getTimestamp("created_at").getTime()) : null,
                rs.getInt("max_uses"),
                rs.getInt("used_count"),
                rs.getBigDecimal("min_booking_amount")
        );
    }

    private String parseStatus(Object statusObj) {
        if (statusObj == null) {
            return "inactive";
        }

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

    private String buildVoucherStatus(Date expireDate, int maxUses, int usedCount) {
        Date today = Date.valueOf(LocalDate.now());

        if (expireDate == null || expireDate.before(today)) {
            return "inactive";
        }

        if (maxUses > 0 && usedCount >= maxUses) {
            return "inactive";
        }

        return "active";
    }
}
