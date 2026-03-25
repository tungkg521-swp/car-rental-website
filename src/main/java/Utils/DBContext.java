package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBContext {

    private static final String URL
            = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=CarRentalDB1;"
            + "user=sa;"
            + "password=123456;"
            + "encrypt=true;"
            + "trustServerCertificate=true;";

    public Connection connection;

    public DBContext() {
        try {
  String url = "jdbc:sqlserver://localhost:1433;"
                    + "databaseName=CarRentalDB1;"
                    + "user=sa;"
                    + "password=123456;"
                    + "encrypt=true;"
                    + "trustServerCertificate=true;";

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(URL);
            System.out.println("Database connected successfully.");
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println("Database connection failed: " + ex.getMessage());
        }
    }

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(URL);
    }

    public static void main(String[] args) {
        try (Connection conn = DBContext.getConnection()) {
            if (conn != null) {
                System.out.println("Test connection successful.");
            }
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println("Test connection failed: " + ex.getMessage());
        }
    }
}