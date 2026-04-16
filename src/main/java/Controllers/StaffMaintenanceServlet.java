package Controllers;

import DALs.CarDAO;
import DALs.MaintenanceDAO;

import service.MaintenanceService;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.MaintenanceModel;

@WebServlet("/staff/maintenance")
public class StaffMaintenanceServlet extends HttpServlet {


    private final MaintenanceDAO maintenanceDAO = new MaintenanceDAO();


    private final CarDAO carDAO = new CarDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null || action.equals("list")) {
            List<MaintenanceModel> maintenances = maintenanceDAO.findAll();

            request.setAttribute("maintenances", maintenances);
            request.getRequestDispatcher("/views/staff-maintenance.jsp").forward(request, response);

        } else if (action.equals("detail")) {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/staff/maintenance?error=missing_id");
                return;
            }
            int id = Integer.parseInt(idStr);

            MaintenanceModel maintenance = maintenanceDAO.findById(id);

            if (maintenance != null) {
                request.setAttribute("maintenance", maintenance);
                request.getRequestDispatcher("/views/staff-maintenance-detail.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/staff/maintenance?error=notfound");
            }

        } else if (action.equals("add")) {
            request.setAttribute("cars", carDAO.findAllCars());
            request.setAttribute("isEdit", false);
            request.getRequestDispatcher("/views/maintenance-form.jsp").forward(request, response);

        } else if (action.equals("edit")) {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/staff/maintenance?error=missing_id");
                return;
            }
            int id = Integer.parseInt(idStr);

            MaintenanceModel maintenance = maintenanceDAO.findById(id);

            if (maintenance != null) {
                request.setAttribute("maintenance", maintenance);
                request.setAttribute("cars", carDAO.findAllCars());
                request.setAttribute("isEdit", true);
                request.getRequestDispatcher("/views/maintenance-form.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/staff/maintenance?error=notfound");
            }

        } else if (action.equals("delete")) {
            String idStr = request.getParameter("id");
            if (idStr != null && !idStr.trim().isEmpty()) {
                int id = Integer.parseInt(idStr);

                MaintenanceModel m = maintenanceDAO.findById(id);
                boolean success = false;
                if (m != null) {
                    success = maintenanceDAO.delete(id);
                    if (success) {
                        carDAO.updateCarStatus(m.getCarId(), "AVAILABLE");
                    }
                }

                if (success) {
                    request.getSession().setAttribute("message", "Xóa lịch bảo dưỡng thành công!");
                } else {
                    request.getSession().setAttribute("error", "Không thể xóa lịch bảo dưỡng.");
                }
            }
            response.sendRedirect(request.getContextPath() + "/staff/maintenance");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            try {
                MaintenanceModel m = new MaintenanceModel();
                m.setCarId(Integer.parseInt(request.getParameter("carId")));
                m.setMaintenanceType(request.getParameter("maintenanceType"));
                m.setScheduledDate(java.sql.Date.valueOf(request.getParameter("scheduledDate")));
                m.setMileageScheduled(Integer.parseInt(request.getParameter("mileageScheduled")));
                m.setDescription(request.getParameter("description"));
                m.setEstimatedCost(new java.math.BigDecimal(request.getParameter("estimatedCost")));
                m.setStatus("SCHEDULED");

                Integer staffId = (Integer) request.getSession().getAttribute("staffId");
                if (staffId == null) {
                    staffId = 1;
                }
                m.setCreatedBy(staffId);

                boolean success = maintenanceDAO.add(m);
                if (success) {
                    carDAO.updateCarStatus(m.getCarId(), "MAINTENANCE");
                }

                if (success) {
                    request.getSession().setAttribute("message", "Tạo lịch bảo dưỡng thành công! Xe đã chuyển sang MAINTENANCE.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("error", "Dữ liệu không hợp lệ.");
            }
            response.sendRedirect(request.getContextPath() + "/staff/maintenance");

        } else if ("update".equals(action)) {
            try {
                MaintenanceModel m = new MaintenanceModel();
                m.setMaintenanceId(Integer.parseInt(request.getParameter("maintenanceId")));
                m.setCarId(Integer.parseInt(request.getParameter("carId")));
                m.setMaintenanceType(request.getParameter("maintenanceType"));
                m.setScheduledDate(java.sql.Date.valueOf(request.getParameter("scheduledDate")));
                m.setMileageScheduled(Integer.parseInt(request.getParameter("mileageScheduled")));
                m.setDescription(request.getParameter("description"));
                m.setEstimatedCost(new java.math.BigDecimal(request.getParameter("estimatedCost")));
                m.setStatus(request.getParameter("status"));


                boolean success = maintenanceDAO.update(m);
                if (success) {
                    if ("COMPLETED".equalsIgnoreCase(m.getStatus()) || "CANCELLED".equalsIgnoreCase(m.getStatus())) {
                        carDAO.updateCarStatus(m.getCarId(), "AVAILABLE");
                    } else {
                        carDAO.updateCarStatus(m.getCarId(), "MAINTENANCE");
                    }
                }

                if (success) {
                    request.getSession().setAttribute("message", "Cập nhật lịch bảo dưỡng thành công!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("error", "Dữ liệu không hợp lệ.");
            }
            response.sendRedirect(request.getContextPath() + "/staff/maintenance");
        }
    }
}
