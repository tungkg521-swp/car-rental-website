<<<<<<< HEAD
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

import java.sql.*;

/**
 *
 * @author shandy
 */
public class DBContext {

    public Connection connection;

    public DBContext() {
        try {
            String url = "jdbc:sqlserver://localhost:1433;"
                    + "databaseName=CarRentalDB;"
                    + "user=sa;"
                    + "password=123456;"
                    + "encrypt=true;"
                    + "trustServerCertificate=true;";
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url);
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println("Database connection failed: " + ex);
        }
    }
    
    public static void main(String[] args) {
        new DBContext();  // chỉ cần khởi tạo là nó in ra log
    }
}
=======
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

import java.sql.*;

/**
 *
 * @author shandy
 */
public class DBContext {

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
            connection = DriverManager.getConnection(url);
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println("Database connection failed: " + ex);
        }
    }
    
    public static void main(String[] args) {
        new DBContext();  // chỉ cần khởi tạo là nó in ra log
    }
}
>>>>>>> ff40f09d586c56233d65dc66d84d55a00d91b880
