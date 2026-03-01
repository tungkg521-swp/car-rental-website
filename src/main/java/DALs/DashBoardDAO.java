package DALs;

import Utils.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashBoardDAO extends DBContext {

    public int countAccounts() {
        String sql = "SELECT COUNT(*) FROM account";
        return executeCount(sql);
    }

    public int countCars() {
        String sql = "SELECT COUNT(*) FROM cars";
        return executeCount(sql);
    }

    public int countActiveBookings() {
        String sql = "SELECT COUNT(*) FROM booking WHERE status = 'ACTIVE'";
        return executeCount(sql);
    }

    public int countMaintenanceCars() {
        String sql = "SELECT COUNT(*) FROM cars WHERE status = 'MAINTENANCE'";
        return executeCount(sql);
    }

    public int countPendingBookings() {
        String sql = "SELECT COUNT(*) FROM booking WHERE status = 'PENDING'";
        return executeCount(sql);
    }

    public int countCompletedBookings() {
        String sql = "SELECT COUNT(*) FROM booking WHERE status = 'COMPLETED'";
        return executeCount(sql);
    }

    public double getTotalRevenue() {
        String sql = "SELECT ISNULL(SUM(total_estimated_price),0) FROM booking WHERE status = 'COMPLETED'";
        double total = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            
            if (rs.next()) {
                total = rs.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return total;
    }

    private int executeCount(String sql) {
        int count = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }
}
