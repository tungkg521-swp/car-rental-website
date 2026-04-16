/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DALs;

import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import models.StaffModel;

public class StaffDAO extends DBContext {

    public StaffModel findByAccountId(int accountId) {

        String sql = """
            SELECT staff_id,
                   full_name,
                   email,
                   phone,
                   status,
                   created_at,
                   account_id
            FROM staff
            WHERE account_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, accountId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                StaffModel staff = new StaffModel();

                staff.setStaffId(rs.getInt("staff_id"));
                staff.setFullName(rs.getString("full_name"));
                staff.setEmail(rs.getString("email"));
                staff.setPhone(rs.getString("phone"));
                staff.setStatus(rs.getString("status"));

                if (rs.getTimestamp("created_at") != null) {
                    staff.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }

                staff.setAccountId(rs.getInt("account_id"));

                return staff;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
