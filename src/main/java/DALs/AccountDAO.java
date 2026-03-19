package DALs;

import Utils.DBContext;
import java.sql.*;
import models.AccountModel;

public class AccountDAO extends DBContext {

    public AccountModel findByEmail(String email) {

        String sql = "SELECT * FROM account WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                AccountModel acc = new AccountModel();
                acc.setAccountId(rs.getInt("account_id"));
                acc.setEmail(rs.getString("email"));
                acc.setPasswordHash(rs.getString("password_hash"));
                acc.setRoleId(rs.getInt("role_id"));
                acc.setStatus(rs.getString("status"));

                return acc;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return null;
    }

    public int insertAccount(AccountModel acc) {

        String sql = "INSERT INTO account(email, password_hash, role_id, status) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, acc.getEmail());
            ps.setString(2, acc.getPasswordHash());
            ps.setInt(3, acc.getRoleId());
            ps.setString(4, "ACTIVE");

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                return -1;
            }

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return -1;
    }

    public void updateLastLogin(int accountId) {

        String sql = "UPDATE account SET last_login_at = GETDATE() WHERE account_id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, accountId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AccountModel findById(int accountId) {
        String sql = "SELECT * FROM account WHERE account_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                AccountModel acc = new AccountModel();
                acc.setAccountId(rs.getInt("account_id"));
                acc.setEmail(rs.getString("email"));
                acc.setPasswordHash(rs.getString("password_hash"));
                acc.setRoleId(rs.getInt("role_id"));
                acc.setStatus(rs.getString("status"));
                return acc;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return null;
    }

    public int updatePasswordByAccountId(int accountId, String newPasswordHash) {
        String sql = "UPDATE account SET password_hash = ? WHERE account_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, accountId);
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}