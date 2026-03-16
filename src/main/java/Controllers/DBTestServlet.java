package Controllers;

import Utils.DBContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/debug/db")
public class DBTestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            DBContext ctx = new DBContext();
            Connection con = ctx.connection;

            out.println("<h2>DB Test</h2>");
            if (con == null) {
                out.println("<p style='color:red'>Connection is null</p>");
                return;
            }

            out.println("<p>Connection: " + con + "</p>");

            String sqlTotal = "SELECT COUNT(*) AS cnt FROM cars";
            String sqlAvailable = "SELECT COUNT(*) AS cnt FROM cars WHERE status = 'AVAILABLE'";

            try (PreparedStatement ps = con.prepareStatement(sqlTotal);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    out.println("<p>Total cars: " + rs.getInt("cnt") + "</p>");
                }
            } catch (Exception e) {
                out.println("<p>Error counting total cars: " + e + "</p>");
            }

            try (PreparedStatement ps = con.prepareStatement(sqlAvailable);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    out.println("<p>Available cars: " + rs.getInt("cnt") + "</p>");
                }
            } catch (Exception e) {
                out.println("<p>Error counting available cars: " + e + "</p>");
            }

        }
    }
}
