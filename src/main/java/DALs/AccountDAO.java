/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DALs;

import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import models.AccountModel;

/**
 *
 * @author ADMIN
 */
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
                acc.setStatus(rs.getString("status"));
                acc.setRoleId(rs.getInt("role_id"));
                return acc;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

