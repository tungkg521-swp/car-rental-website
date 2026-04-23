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

        if ("add".equals(action)) {
            List<CarModel> carList = carDAO.findAllCars();
            request.setAttribute("carList", carList);

            request.setAttribute("error", request.getSession().getAttribute("error"));
            request.setAttribute("formCarId", request.getSession().getAttribute("formCarId"));
            request.setAttribute("formMaintenanceType", request.getSession().getAttribute("formMaintenanceType"));
            request.setAttribute("formStartDate", request.getSession().getAttribute("formStartDate"));
            request.setAttribute("formEndDate", request.getSession().getAttribute("formEndDate"));
            request.setAttribute("formMileageScheduled", request.getSession().getAttribute("formMileageScheduled"));
            request.setAttribute("formDescription", request.getSession().getAttribute("formDescription"));
            request.setAttribute("formEstimatedCost", request.getSession().getAttribute("formEstimatedCost"));

            request.getSession().removeAttribute("error");
            request.getSession().removeAttribute("formCarId");
            request.getSession().removeAttribute("formMaintenanceType");
            request.getSession().removeAttribute("formStartDate");
            request.getSession().removeAttribute("formEndDate");
            request.getSession().removeAttribute("formMileageScheduled");
            request.getSession().removeAttribute("formDescription");
            request.getSession().removeAttribute("formEstimatedCost");

            request.getRequestDispatcher("/views/maintenance-form.jsp").forward(request, response);
            return;
        }

        if ("edit".equals(action)) {
            try {
                int maintenanceId = Integer.parseInt(request.getParameter("id"));
                MaintenanceModel maintenance = maintenanceDAO.findById(maintenanceId);
                List<CarModel> carList = carDAO.findAllCars();

                request.setAttribute("maintenance", maintenance);
                request.setAttribute("carList", carList);

                request.setAttribute("error", request.getSession().getAttribute("error"));
                request.getSession().removeAttribute("error");

                request.getRequestDispatcher("/views/maintenance-form.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("error", "Không tìm thấy lịch bảo dưỡng.");
                response.sendRedirect(request.getContextPath() + "/staff/maintenance");
            }
            return;
        }

        if ("detail".equals(action)) {
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

            String carIdRaw = request.getParameter("carId");
            String maintenanceType = request.getParameter("maintenanceType");
            String startDateRaw = request.getParameter("startDate");
            String endDateRaw = request.getParameter("endDate");
            String mileageRaw = request.getParameter("mileageScheduled");
            String description = request.getParameter("description");
            String estimatedCostRaw = request.getParameter("estimatedCost");

            request.getSession().setAttribute("formCarId", carIdRaw);
            request.getSession().setAttribute("formMaintenanceType", maintenanceType);
            request.getSession().setAttribute("formStartDate", startDateRaw);
            request.getSession().setAttribute("formEndDate", endDateRaw);
            request.getSession().setAttribute("formMileageScheduled", mileageRaw);
            request.getSession().setAttribute("formDescription", description);
            request.getSession().setAttribute("formEstimatedCost", estimatedCostRaw);

            if (carIdRaw == null || carIdRaw.trim().isEmpty()
                    || maintenanceType == null || maintenanceType.trim().isEmpty()
                    || startDateRaw == null || startDateRaw.trim().isEmpty()
                    || endDateRaw == null || endDateRaw.trim().isEmpty()
                    || mileageRaw == null || mileageRaw.trim().isEmpty()
                    || estimatedCostRaw == null || estimatedCostRaw.trim().isEmpty()) {

                request.getSession().setAttribute("error", "Vui lòng nhập đầy đủ thông tin.");
                response.sendRedirect(request.getContextPath() + "/staff/maintenance?action=add");
                return;
            }

            int carId = Integer.parseInt(carIdRaw);
            Date startDate = Date.valueOf(startDateRaw);
            Date endDate = Date.valueOf(endDateRaw);
            int mileageScheduled = Integer.parseInt(mileageRaw);
            BigDecimal estimatedCost = new BigDecimal(estimatedCostRaw.trim());

            if (endDate.before(startDate)) {
                request.getSession().setAttribute("error", "Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu.");
                response.sendRedirect(request.getContextPath() + "/staff/maintenance?action=add");
                return;
            }

            if (mileageScheduled < 0) {
                request.getSession().setAttribute("error", "Số km lên lịch không hợp lệ.");
                response.sendRedirect(request.getContextPath() + "/staff/maintenance?action=add");
                return;
            }

            if (estimatedCost.compareTo(BigDecimal.ZERO) < 0) {
                request.getSession().setAttribute("error", "Chi phí ước tính không hợp lệ.");
                response.sendRedirect(request.getContextPath() + "/staff/maintenance?action=add");
                return;
            }

            boolean conflict = maintenanceDAO.hasScheduleConflict(carId, startDate, endDate, null);
            if (conflict) {
                request.getSession().setAttribute("error",
                        "Xe đã có lịch thuê, hợp đồng hoặc lịch bảo dưỡng trùng trong khoảng thời gian này.");
                response.sendRedirect(request.getContextPath() + "/staff/maintenance?action=add");
                return;
            }

            maintenance.setCarId(carId);
            maintenance.setMaintenanceType(maintenanceType);
            maintenance.setStartDate(startDate);
            maintenance.setEndDate(endDate);
            maintenance.setMileageScheduled(mileageScheduled);
            maintenance.setDescription(description);
            maintenance.setEstimatedCost(estimatedCost);
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
                request.getSession().removeAttribute("formCarId");
                request.getSession().removeAttribute("formMaintenanceType");
                request.getSession().removeAttribute("formStartDate");
                request.getSession().removeAttribute("formEndDate");
                request.getSession().removeAttribute("formMileageScheduled");
                request.getSession().removeAttribute("formDescription");
                request.getSession().removeAttribute("formEstimatedCost");
                request.getSession().removeAttribute("error");

                carDAO.updateCarStatus(carId, "MAINTENANCE");
                request.getSession().setAttribute("message",
                        "Tạo lịch bảo dưỡng thành công.");
                response.sendRedirect(request.getContextPath() + "/staff/maintenance");
            } else {
                request.getSession().setAttribute("error", "Tạo lịch bảo dưỡng thất bại.");
                response.sendRedirect(request.getContextPath() + "/staff/maintenance?action=add");
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Dữ liệu số không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/staff/maintenance?action=add");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Ngày nhập không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/staff/maintenance?action=add");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Đã xảy ra lỗi khi tạo lịch bảo dưỡng.");
            response.sendRedirect(request.getContextPath() + "/staff/maintenance?action=add");
        }
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