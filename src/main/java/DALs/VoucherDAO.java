package DALs;

import Utils.DBContext;
import models.VoucherModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class VoucherDAO extends DBContext {

    public VoucherDAO() {
        super();
    }

    // Get all vouchers
    public List<VoucherModel> getAllVouchers() {
        List<VoucherModel> list = new ArrayList<>();

        String sql = """
            SELECT 
                voucher_id,
                code,
                discount,
                start_date,
                end_date,
                status
            FROM vouchers
            ORDER BY voucher_id DESC
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new VoucherModel(
                    rs.getInt("voucher_id"),
                    rs.getString("code"),
                    rs.getBigDecimal("discount"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getString("status")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("VOUCHER LIST SIZE = " + list.size());
        return list;
    }

    // Find voucher by ID
    public VoucherModel findById(int voucherId) {
        String sql = """
            SELECT 
                voucher_id,
                code,
                discount,
                start_date,
                end_date,
                status
            FROM vouchers
            WHERE voucher_id = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, voucherId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new VoucherModel(
                    rs.getInt("voucher_id"),
                    rs.getString("code"),
                    rs.getBigDecimal("discount"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getString("status")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Insert new voucher
    public boolean insert(VoucherModel voucher) {
        String sql = """
            INSERT INTO vouchers 
            (code, discount, start_date, end_date, status)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, voucher.getCode());
            ps.setBigDecimal(2, voucher.getDiscount());
            ps.setDate(3, voucher.getStartDate());
            ps.setDate(4, voucher.getEndDate());
            ps.setString(5, voucher.getStatus());

            int result = ps.executeUpdate();
            System.out.println("INSERT VOUCHER: " + (result > 0 ? "SUCCESS" : "FAILED"));
            return result > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Update existing voucher
    public boolean update(VoucherModel voucher) {
        String sql = """
            UPDATE vouchers
            SET code = ?,
                discount = ?,
                start_date = ?,
                end_date = ?,
                status = ?
            WHERE voucher_id = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, voucher.getCode());
            ps.setBigDecimal(2, voucher.getDiscount());
            ps.setDate(3, voucher.getStartDate());
            ps.setDate(4, voucher.getEndDate());
            ps.setString(5, voucher.getStatus());
            ps.setInt(6, voucher.getVoucherId());

            int result = ps.executeUpdate();
            System.out.println("UPDATE VOUCHER: " + (result > 0 ? "SUCCESS" : "FAILED"));
            return result > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Delete voucher
    public boolean delete(int voucherId) {
        String sql = "DELETE FROM vouchers WHERE voucher_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, voucherId);

            int result = ps.executeUpdate();
            System.out.println("DELETE VOUCHER: " + (result > 0 ? "SUCCESS" : "FAILED"));
            return result > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Find voucher by code
    public VoucherModel findByCode(String code) {
        String sql = """
            SELECT 
                voucher_id,
                code,
                discount,
                start_date,
                end_date,
                status
            FROM vouchers
            WHERE code = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new VoucherModel(
                    rs.getInt("voucher_id"),
                    rs.getString("code"),
                    rs.getBigDecimal("discount"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getString("status")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
