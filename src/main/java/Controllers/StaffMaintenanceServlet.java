/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import DALs.MaintenanceDAO;
import DALs.CarDAO;  // để lấy danh sách xe khi add/update
import Models.MaintenanceModel;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/staff/maintenance")
public class StaffMaintenanceServlet extends HttpServlet {

    private MaintenanceDAO maintenanceDAO = new MaintenanceDAO();
    private CarDAO carDAO = new CarDAO();  // giả sử bạn có CarDAO để lấy list xe

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null || action.equals("list")) {
            // View Maintenance Schedule (danh sách)
            List<MaintenanceModel> maintenances = maintenanceDAO.findAll();
            request.setAttribute("maintenances", maintenances);
            request.getRequestDispatcher("/views/staff-maintenance.jsp").forward(request, response);

        } else if (action.equals("detail")) {
            // View Maintenance Detail
            int id = Integer.parseInt(request.getParameter("id"));
            MaintenanceModel maintenance = maintenanceDAO.findById(id);
            if (maintenance != null) {
                request.setAttribute("maintenance", maintenance);
                request.getRequestDispatcher("/views/maintenance-detail.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/staff/maintenance?error=notfound");
            }

        } else if (action.equals("add") || action.equals("edit")) {
            // Form add/update
            if (action.equals("edit")) {
                int id = Integer.parseInt(request.getParameter("id"));
                MaintenanceModel m = maintenanceDAO.findById(id);
                request.setAttribute("maintenance", m);
            }
            // Lấy list xe để chọn khi add/edit
            request.setAttribute("cars", carDAO.findAllCars());  // hoặc findAvailable + MAINTENANCE
            request.getRequestDispatcher("/views/maintenance-form.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        MaintenanceModel m = new MaintenanceModel();
        m.setCarId(Integer.parseInt(request.getParameter("carId")));
        m.setMaintenanceType(request.getParameter("maintenanceType"));
        m.setScheduledDate(java.sql.Date.valueOf(request.getParameter("scheduledDate")));
        m.setMileageScheduled(Integer.parseInt(request.getParameter("mileageScheduled")));
        m.setDescription(request.getParameter("description"));
        m.setEstimatedCost(new java.math.BigDecimal(request.getParameter("estimatedCost")));
        m.setStatus(request.getParameter("status"));

        // Nếu là update/complete
        if ("update".equals(action) || "complete".equals(action)) {
            m.setMaintenanceId(Integer.parseInt(request.getParameter("maintenanceId")));
            if ("complete".equals(action)) {
                m.setActualCompletionDate(java.sql.Date.valueOf(request.getParameter("actualDate")));
                m.setMileageCompleted(Integer.parseInt(request.getParameter("mileageCompleted")));
                m.setActualCost(new java.math.BigDecimal(request.getParameter("actualCost")));
                m.setCompletedBy(/* lấy từ session staff id */ 1); // giả sử
                m.setStatus("COMPLETED");
            }
            boolean success = maintenanceDAO.update(m);
            // redirect với message success/error
        } else {
            // add new
            boolean success = maintenanceDAO.add(m);
        }

        response.sendRedirect(request.getContextPath() + "/staff/maintenance");
    }
}