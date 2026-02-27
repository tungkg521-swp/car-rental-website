package DALs;

import Models.VoucherModel;
import Utils.DBContext;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

public class VoucherDAO {

    public VoucherModel findByCode(String code) throws SQLException {

        String sql = "SELECT voucherId, code, discount, type, expireDate, status " +
                     "FROM Voucher WHERE code = ? AND status = 1";

        try (Connection conn = new DBContext().connection;
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    VoucherModel voucher = new VoucherModel();

                    voucher.setVoucherId(rs.getInt("voucherId"));
                    voucher.setCode(rs.getString("code"));
                    voucher.setDiscount(rs.getBigDecimal("discount"));
                    voucher.setType(rs.getString("type"));

                    Date sqlDate = rs.getDate("expireDate");
                    if (sqlDate != null) {
                        voucher.setExpireDate(sqlDate);
                    }

                    voucher.setActive(rs.getBoolean("status"));

                    return voucher;
                }
            }
        }

        return null; // không tìm thấy
    }
}