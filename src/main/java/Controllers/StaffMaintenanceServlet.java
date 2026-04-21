package Controllers;

import DALs.CarDAO;
import DALs.MaintenanceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import models.CarModel;
import models.MaintenanceModel;

@WebServlet(name = "StaffMaintenanceServlet", urlPatterns = {"/staff/maintenance"})
public class StaffMaintenanceServlet extends HttpServlet {

    private final MaintenanceDAO maintenanceDAO = new MaintenanceDAO();
    private final CarDAO carDAO = new CarDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null || action.equals("list")) {
            List<MaintenanceModel> maintenanceList = maintenanceDAO.findAll();
            request.setAttribute("maintenanceList", maintenanceList);
            request.getRequestDispatcher("/views/staff-maintenance.jsp").forward(request, response);
            return;
        }

        if ("blocked-dates".equals(action)) {
            response.setContentType("application/json;charset=UTF-8");

            try {
                int carId = Integer.parseInt(request.getParameter("carId"));
                Integer excludeMaintenanceId = null;

                String maintenanceIdRaw = request.getParameter("maintenanceId");
                if (maintenanceIdRaw != null && !maintenanceIdRaw.trim().isEmpty()) {
                    excludeMaintenanceId = Integer.parseInt(maintenanceIdRaw);
                }

                List<String[]> ranges = maintenanceDAO.getBlockedRangesByCarId(carId, excludeMaintenanceId);

                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < ranges.size(); i++) {
                    String[] range = ranges.get(i);
                    json.append("{\"from\":\"")
                            .append(range[0])
                            .append("\",\"to\":\"")
                            .append(range[1])
                            .append("\"}");
                    if (i < ranges.size() - 1) {
                        json.append(",");
                    }
                }
                json.append("]");

                response.getWriter().write(json.toString());
            } catch (Exception e) {
                e.printStackTrace();
                response.getWriter().write("[]");
            }
            return;
        }

        if (action.equals("add")) {
            List<CarModel> carList = carDAO.findAllCars();
            request.setAttribute("carList", carList);
            request.getRequestDispatcher("/views/maintenance-form.jsp").forward(request, response);
            return;
        }

        if (action.equals("edit")) {
            try {
                int maintenanceId = Integer.parseInt(request.getParameter("id"));
                MaintenanceModel maintenance = maintenanceDAO.findById(maintenanceId);
                List<CarModel> carList = carDAO.findAllCars();

                request.setAttribute("maintenance", maintenance);
                request.setAttribute("carList", carList);
                request.getRequestDispatcher("/views/maintenance-form.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("error", "Không tìm thấy lịch bảo dưỡng.");
                response.sendRedirect(request.getContextPath() + "/staff/maintenance");
            }
            return;
        }

        if (action.equals("detail")) {
            try {
                int maintenanceId = Integer.parseInt(request.getParameter("id"));
                MaintenanceModel maintenance = maintenanceDAO.findById(maintenanceId);

                if (maintenance == null) {
                    request.getSession().setAttribute("error", "Không tìm thấy chi tiết bảo dưỡng.");
                    response.sendRedirect(request.getContextPath() + "/staff/maintenance");
                    return;
                }

                request.setAttribute("maintenance", maintenance);
                request.getRequestDispatcher("/views/staff-maintenance-detail.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("error", "Không thể mở chi tiết bảo dưỡng.");
                response.sendRedirect(request.getContextPath() + "/staff/maintenance");
            }
            return;
        }

        response.sendRedirect(request.getContextPath() + "/staff/maintenance");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            handleAdd(request, response);
        } else if ("update".equals(action)) {
            handleUpdate(request, response);
        } else if ("delete".equals(action)) {
            handleDelete(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/staff/maintenance");
        }
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            MaintenanceModel maintenance = new MaintenanceModel();

            int carId = Integer.parseInt(request.getParameter("carId"));
            String maintenanceType = request.getParameter("maintenanceType");
            String startDateRaw = request.getParameter("startDate");
            String endDateRaw = request.getParameter("endDate");
            String mileageRaw = request.getParameter("mileageScheduled");
            String description = request.getParameter("description");
            String estimatedCostRaw = request.getParameter("estimatedCost");

            Date startDate = Date.valueOf(startDateRaw);
            Date endDate = Date.valueOf(endDateRaw);

            if (endDate.before(startDate)) {
                request.getSession().setAttribute("error", "Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu.");
                response.sendRedirect(request.getContextPath() + "/staff/maintenance?action=add");
                return;
            }

            boolean conflict = maintenanceDAO.hasScheduleConflict(carId, startDate, endDate, null);
            if (conflict) {
                request.getSession().setAttribute("error", "Xe đã có lịch thuê, hợp đồng hoặc lịch bảo dưỡng trùng trong khoảng thời gian này.");
                response.sendRedirect(request.getContextPath() + "/staff/maintenance?action=add");
                return;
            }

            maintenance.setCarId(carId);
            maintenance.setMaintenanceType(maintenanceType);
            maintenance.setStartDate(startDate);
            maintenance.setEndDate(endDate);
            maintenance.setMileageScheduled(Integer.parseInt(mileageRaw));
            maintenance.setDescription(description);

            if (estimatedCostRaw != null && !estimatedCostRaw.trim().isEmpty()) {
                maintenance.setEstimatedCost(new BigDecimal(estimatedCostRaw.trim()));
            } else {
                maintenance.setEstimatedCost(BigDecimal.ZERO);
            }

            maintenance.setStatus("IN_PROGRESS");

            Integer createdBy = null;
            Object staffIdObj = request.getSession().getAttribute("staffId");
            if (staffIdObj instanceof Integer) {
                createdBy = (Integer) staffIdObj;
            }

            Object accountIdObj = request.getSession().getAttribute("accountId");
            if (createdBy == null && accountIdObj instanceof Integer) {
                createdBy = (Integer) accountIdObj;
            }

            maintenance.setCreatedBy(createdBy);

            boolean success = maintenanceDAO.add(maintenance);

            if (success) {
                carDAO.updateCarStatus(carId, "MAINTENANCE");
                request.getSession().setAttribute("message",
                        "Tạo lịch bảo dưỡng thành công. Trạng thái xe đã chuyển sang MAINTENANCE.");
            } else {
                request.getSession().setAttribute("error", "Tạo lịch bảo dưỡng thất bại.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Dữ liệu nhập không hợp lệ.");
        }

        response.sendRedirect(request.getContextPath() + "/staff/maintenance");
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int maintenanceId = Integer.parseInt(request.getParameter("maintenanceId"));
            int carId = Integer.parseInt(request.getParameter("carId"));
            String maintenanceType = request.getParameter("maintenanceType");
            String startDateRaw = request.getParameter("startDate");
            String endDateRaw = request.getParameter("endDate");
            String mileageRaw = request.getParameter("mileageScheduled");
            String description = request.getParameter("description");
            String estimatedCostRaw = request.getParameter("estimatedCost");
            String status = request.getParameter("status");

            Date startDate = Date.valueOf(startDateRaw);
            Date endDate = Date.valueOf(endDateRaw);

            if (endDate.before(startDate)) {
                request.getSession().setAttribute("error", "Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu.");
                response.sendRedirect(request.getContextPath() + "/staff/maintenance?action=edit&id=" + maintenanceId);
                return;
            }

            boolean conflict = maintenanceDAO.hasScheduleConflict(carId, startDate, endDate, maintenanceId);
            if (conflict) {
                request.getSession().setAttribute("error", "Xe đã có lịch thuê, hợp đồng hoặc lịch bảo dưỡng trùng trong khoảng thời gian này.");
                response.sendRedirect(request.getContextPath() + "/staff/maintenance?action=edit&id=" + maintenanceId);
                return;
            }

            MaintenanceModel maintenance = new MaintenanceModel();
            maintenance.setMaintenanceId(maintenanceId);
            maintenance.setCarId(carId);
            maintenance.setMaintenanceType(maintenanceType);
            maintenance.setStartDate(startDate);
            maintenance.setEndDate(endDate);
            maintenance.setMileageScheduled(Integer.parseInt(mileageRaw));
            maintenance.setDescription(description);

            if (estimatedCostRaw != null && !estimatedCostRaw.trim().isEmpty()) {
                maintenance.setEstimatedCost(new BigDecimal(estimatedCostRaw.trim()));
            } else {
                maintenance.setEstimatedCost(BigDecimal.ZERO);
            }

            maintenance.setStatus(status);

            boolean success = maintenanceDAO.update(maintenance);

            if (success) {
                if ("IN_PROGRESS".equalsIgnoreCase(status) || "SCHEDULED".equalsIgnoreCase(status)) {
                    carDAO.updateCarStatus(carId, "MAINTENANCE");
                } else if ("COMPLETED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status)) {
                    if (!maintenanceDAO.hasActiveMaintenanceForCar(carId, maintenanceId)) {
                        carDAO.updateCarStatus(carId, "AVAILABLE");
                    }
                }

                request.getSession().setAttribute("message", "Cập nhật lịch bảo dưỡng thành công.");
            } else {
                request.getSession().setAttribute("error", "Cập nhật lịch bảo dưỡng thất bại.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Dữ liệu cập nhật không hợp lệ.");
        }

        response.sendRedirect(request.getContextPath() + "/staff/maintenance");
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int maintenanceId = Integer.parseInt(request.getParameter("maintenanceId"));
            MaintenanceModel maintenance = maintenanceDAO.findById(maintenanceId);

            if (maintenance == null) {
                request.getSession().setAttribute("error", "Không tìm thấy lịch bảo dưỡng để xóa.");
                response.sendRedirect(request.getContextPath() + "/staff/maintenance");
                return;
            }

            int carId = maintenance.getCarId();
            boolean success = maintenanceDAO.delete(maintenanceId);

            if (success) {
                if (!maintenanceDAO.hasActiveMaintenanceForCar(carId, maintenanceId)) {
                    carDAO.updateCarStatus(carId, "AVAILABLE");
                }
                request.getSession().setAttribute("message", "Xóa lịch bảo dưỡng thành công.");
            } else {
                request.getSession().setAttribute("error", "Xóa lịch bảo dưỡng thất bại.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Không thể xóa lịch bảo dưỡng.");
        }

        response.sendRedirect(request.getContextPath() + "/staff/maintenance");
    }

    @Override
    public String getServletInfo() {
        return "Staff Maintenance Servlet";
    }
}