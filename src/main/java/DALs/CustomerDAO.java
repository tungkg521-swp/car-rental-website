package DALs;

import Utils.DBContext;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import models.CustomerModel;

public class CustomerDAO extends DBContext {

    public CustomerModel getByAccountId(int accountId) {

        String sql = """
            SELECT customer_id, full_name, email, phone, status,
                   created_at, account_id, address, dob
            FROM customer
            WHERE account_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                CustomerModel c = new CustomerModel();
                c.setCustomerId(rs.getInt("customer_id"));
                c.setFullName(rs.getString("full_name"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                c.setStatus(rs.getString("status"));
                c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                c.setAccountId(rs.getInt("account_id"));
                c.setAddress(rs.getString("address"));

                if (rs.getDate("dob") != null) {
                    c.setDob(rs.getDate("dob").toLocalDate());
                }

                return c;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public List<CustomerModel> findAllCustomers() {
    List<CustomerModel> list = new ArrayList<>();

    String sql = """
        SELECT customer_id,
               full_name,
               email,
               phone,
               status,
               created_at,
               account_id,
               address,
               dob
        FROM customer
        ORDER BY created_at DESC
    """;

    try (PreparedStatement ps = connection.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            CustomerModel customer = new CustomerModel();

            customer.setCustomerId(rs.getInt("customer_id"));
            customer.setFullName(rs.getString("full_name"));
            customer.setEmail(rs.getString("email"));
            customer.setPhone(rs.getString("phone"));
            customer.setStatus(rs.getString("status"));

            customer.setCreatedAt(
                    rs.getTimestamp("created_at").toLocalDateTime()
            );

            customer.setAccountId(rs.getInt("account_id"));
            customer.setAddress(rs.getString("address"));

            if (rs.getDate("dob") != null) {
                customer.setDob(rs.getDate("dob").toLocalDate());
            }

            list.add(customer);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

    
    public CustomerModel findById(int id) {

        String sql = """
            SELECT customer_id,
                   full_name,
                   email,
                   phone,
                   status,
                   created_at,
                   account_id,
                   address,
                   dob
            FROM customer
            WHERE customer_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                CustomerModel c = new CustomerModel();

                c.setCustomerId(rs.getInt("customer_id"));
                c.setFullName(rs.getString("full_name"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                c.setStatus(rs.getString("status"));
                c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                c.setAccountId(rs.getInt("account_id"));
                c.setAddress(rs.getString("address"));

                Date dob = rs.getDate("dob");
                if (dob != null) {
                    c.setDob(dob.toLocalDate());
                }

                return c;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public int updateProfile(int accountId,
                         String fullName,
                         String dob,
                         String phone,
                         String email) {

    String sql = "UPDATE customer "
               + "SET full_name = ?, "
               + "dob = ?, "
               + "phone = ?, "
               + "email = ? "
               + "WHERE account_id = ?";

    try (PreparedStatement ps = connection.prepareStatement(sql)) {

        ps.setString(1, fullName);
        ps.setString(2, dob);
        ps.setString(3, phone);
        ps.setString(4, email);
        ps.setInt(5, accountId);

        return ps.executeUpdate();

    } catch (Exception e) {
        e.printStackTrace();
    }

    return 0;
}


}
