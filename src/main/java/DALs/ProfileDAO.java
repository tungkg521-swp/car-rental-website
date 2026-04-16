package DALs;

import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import models.AccountModel;

public class ProfileDAO extends DBContext {

    public int changePassword(int accountId, String oldPasswordHash, String newPasswordHash) throws SQLException {

        String sql = "UPDATE account "
                + "SET password_hash = ? "
                + "WHERE account_id = ? AND password_hash = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, newPasswordHash);
            ps.setInt(2, accountId);
            ps.setString(3, oldPasswordHash);

            return ps.executeUpdate();
        }
    }
}
