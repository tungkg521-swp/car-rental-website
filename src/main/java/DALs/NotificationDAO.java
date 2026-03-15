package DALs;

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

        String sql = "SELECT * FROM notification WHERE target_role = 'staff' ORDER BY created_at DESC";

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

//    public static void main(String[] args) {
//
//        NotificationDAO dao = new NotificationDAO();
//
//        int customerId = 1; // đổi ID theo database
//
//        List<NotificationModel> list = dao.findByCustomerId(customerId);
//
//        System.out.println("Total notification: " + list.size());
//
//        for (NotificationModel n : list) {
//            System.out.println(n);
//        }
//    }
}
