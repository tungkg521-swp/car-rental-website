package DALs;

import models.CustomerModel;
import models.NotificationModel;
import Utils.DBContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public List<NotificationModel> findByCustomerId(int customerId) {
        List<NotificationModel> list = new ArrayList<>();

        String sql = "SELECT * FROM notification WHERE customer_id = ? ORDER BY created_at DESC";

        try {
            DBContext db = new DBContext();
            PreparedStatement ps = db.connection.prepareStatement(sql);
            ps.setInt(1, customerId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                NotificationModel n = new NotificationModel();
                n.setNotificationId(rs.getInt("notification_id"));
                n.setCustomerId(rs.getInt("customer_id"));
                n.setTitle(rs.getString("title"));
                n.setContent(rs.getString("content"));
                n.setIsRead(rs.getBoolean("is_read"));
                n.setCreatedAt(rs.getTimestamp("created_at"));

                list.add(n);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<NotificationModel> findForStaff() {
        List<NotificationModel> list = new ArrayList<>();

        String sql = "SELECT * FROM notification ORDER BY created_at DESC";

        try {
            DBContext db = new DBContext();
            PreparedStatement ps = db.connection.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                NotificationModel n = new NotificationModel();

                n.setNotificationId(rs.getInt("notification_id"));
                n.setCustomerId(rs.getInt("customer_id"));
                n.setTitle(rs.getString("title"));
                n.setContent(rs.getString("content"));
                n.setIsRead(rs.getBoolean("is_read"));
                n.setCreatedAt(rs.getTimestamp("created_at"));

                list.add(n);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insert(NotificationModel n) {

        String sql = """
        INSERT INTO notification
        (customer_id, title, content, is_read, created_at)
        VALUES (?, ?, ?, ?, GETDATE())
        """;

        try {

            DBContext db = new DBContext();
            PreparedStatement ps = db.connection.prepareStatement(sql);

            ps.setInt(1, n.getCustomerId());
            ps.setString(2, n.getTitle());
            ps.setString(3, n.getContent());
            ps.setBoolean(4, n.isIsRead());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertToCustomer(int customerId, String title, String content) {

        String sql = """
        INSERT INTO notification
        (customer_id, title, content, type, reference_id, is_read, created_at, reference_type, target_role)
        VALUES (?, ?, ?, 'SYSTEM', NULL, 0, GETDATE(), 'SYSTEM', 'CUSTOMER')
    """;

        try {

            DBContext db = new DBContext();
            PreparedStatement ps = db.connection.prepareStatement(sql);

            ps.setInt(1, customerId);
            ps.setString(2, title);
            ps.setString(3, content);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertToAll(String title, String content) {

        String sql = """
        INSERT INTO notification
        (customer_id, title, content, type, reference_id, is_read, created_at, reference_type, target_role)
        SELECT 
            customer_id,
            ?,
            ?,
            'SYSTEM',
            NULL,
            0,
            GETDATE(),
            'SYSTEM',
            'CUSTOMER'
        FROM customer
    """;

        try {

            DBContext db = new DBContext();
            PreparedStatement ps = db.connection.prepareStatement(sql);

            ps.setString(1, title);
            ps.setString(2, content);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public int getCustomerIdByAccountId(int accountId) {

        String sql = "SELECT customer_id FROM customer WHERE account_id = ?";

        try {
            DBContext db = new DBContext();
            PreparedStatement ps = db.connection.prepareStatement(sql);
            ps.setInt(1, accountId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("customer_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static void main(String[] args) {

        NotificationDAO dao = new NotificationDAO();

        int customerId = 1; // đổi ID theo database

        List<NotificationModel> list = dao.findByCustomerId(customerId);

        System.out.println("Total notification: " + list.size());

        for (NotificationModel n : list) {
            System.out.println(n);
        }
    }

    public List<CustomerModel> getAllCustomers() {

        List<CustomerModel> list = new ArrayList<>();

        String sql = "SELECT customer_id, full_name FROM customer";

        try {

            DBContext db = new DBContext();
            PreparedStatement ps = db.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                CustomerModel c = new CustomerModel();

                c.setCustomerId(rs.getInt("customer_id"));
                c.setFullName(rs.getString("full_name"));

                list.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
