package DALs;

import Utils.DBContext;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import models.CustomerModel;

public class CustomerDAO extends DBContext {

    // lấy customer theo accountId
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

    // lấy toàn bộ customer
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

    // tìm customer theo id
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

    // update profile
    public int updateProfile(int accountId,
                             String fullName,
                             LocalDate dob,
                             String phone,
                             String email) {

        String sql = """
            UPDATE customer
            SET full_name = ?,
                dob = ?,
                phone = ?,
                email = ?
            WHERE account_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, fullName);

            if (dob != null) {
                ps.setDate(2, Date.valueOf(dob));
            } else {
                ps.setNull(2, Types.DATE);
            }

            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setInt(5, accountId);

            return ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    // insert customer
    public int insertCustomer(CustomerModel customer) {

        String sql = """
            INSERT INTO customer
            (full_name, email, phone, status, created_at, account_id, address, dob)
            VALUES (?, ?, ?, ?, GETDATE(), ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPhone());

            ps.setString(4,
                    customer.getStatus() == null ? "ACTIVE" : customer.getStatus());

            ps.setInt(5, customer.getAccountId());
            ps.setString(6, customer.getAddress());

            if (customer.getDob() != null) {
                ps.setDate(7, Date.valueOf(customer.getDob()));
            } else {
                ps.setNull(7, Types.DATE);
            }

            return ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    // update status
    public int updateStatus(int customerId, String status) {

        String sql = "UPDATE customer SET status = ? WHERE customer_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, customerId);

            return ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    // delete customer
    public int deleteCustomer(int customerId) {

        String sql = "DELETE FROM customer WHERE customer_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            return ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    // check email tồn tại
    public boolean existsByEmail(String email) {

        String sql = "SELECT 1 FROM customer WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // search customer
    public List<CustomerModel> searchCustomer(String fullname, String status) {

        List<CustomerModel> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
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
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (fullname != null && !fullname.trim().isEmpty()) {

            sql.append(" AND LOWER(full_name) LIKE ? ");
            params.add("%" + fullname.trim().toLowerCase() + "%");
        }

        if (status != null && !status.equalsIgnoreCase("ALL")) {

            sql.append(" AND status = ? ");
            params.add(status);
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();

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

    // ===== FIX LỖI REGISTER =====
    // check phone tồn tại
    public CustomerModel findByPhone(String phone) {

        String sql = "SELECT * FROM customer WHERE phone = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, phone);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                CustomerModel c = new CustomerModel();

                c.setCustomerId(rs.getInt("customer_id"));
                c.setFullName(rs.getString("full_name"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                c.setStatus(rs.getString("status"));
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

}