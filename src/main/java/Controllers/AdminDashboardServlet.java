package Controllers;

import DALs.DashBoardDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/dashboard/admin")
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        DashBoardDAO dao = new DashBoardDAO();

        request.setAttribute("totalUsers", dao.countAccounts());
        request.setAttribute("totalCars", dao.countCars());
        request.setAttribute("activeBookings", dao.countActiveBookings());
        request.setAttribute("maintenanceCars", dao.countMaintenanceCars());
        request.setAttribute("pendingBookings", dao.countPendingBookings());
        request.setAttribute("completedBookings", dao.countCompletedBookings());
        request.setAttribute("totalRevenue", dao.getTotalRevenue());

        request.getRequestDispatcher("/views/admin-dashboard.jsp")
                .forward(request, response);
    }
}