package Controllers;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.ReportModel;
import service.ReportService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ReportController", urlPatterns = {
    "/admin/report-summary",
    "/admin/trip-detail",
    "/admin/revenue-chart", // ← Endpoint cho Revenue Line Chart
    "/admin/rental-report-content",
    "/admin/usage-report-content",
    "/admin/revenue-report-content",
    "/admin/vehicle-utilization"
})
public class ReportController extends HttpServlet {

    private final ReportService reportService = new ReportService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        try {
            // ==================== JSON ENDPOINTS (cho Dashboard) ====================
            if ("/admin/report-summary".equals(path)) {
                Map<String, Object> summary = reportService.getReportSummary(startDate, endDate);
                sendJsonResponse(response, summary);

            } else if ("/admin/trip-detail".equals(path)) {
                List<ReportModel> trips = reportService.getAllRentalReports(startDate, endDate);
                sendJsonResponse(response, trips);

            } else if ("/admin/revenue-chart".equals(path)) {
                List<ReportModel> revenueData = reportService.getRevenueByDate(startDate, endDate);
                sendJsonResponse(response, revenueData);

            } else if ("/admin/vehicle-utilization".equals(path)) {
                Map<String, Object> utilizationData = reportService.getVehicleUtilization(startDate, endDate);
                sendJsonResponse(response, utilizationData);
                // ==================== JSP FORWARD ENDPOINTS ====================
            } else if ("/admin/rental-report-content".equals(path)) {
                List<ReportModel> list = reportService.getAllRentalReports(startDate, endDate);
                forwardToJSP(request, response, list, "RENTAL");

            } else if ("/admin/usage-report-content".equals(path)) {
                List<ReportModel> list = reportService.getVehicleUsageReports(startDate, endDate);
                forwardToJSP(request, response, list, "USAGE");

            } else if ("/admin/revenue-report-content".equals(path)) {
                List<ReportModel> list = reportService.getRevenueReports(startDate, endDate);
                forwardToJSP(request, response, list, "REVENUE");

            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Report endpoint not found: " + path);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonResponse(response, Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    // Helper: Trả về JSON
    private void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(data));
    }

    // Helper: Forward sang JSP
    private void forwardToJSP(HttpServletRequest request, HttpServletResponse response,
            List<ReportModel> list, String reportType) throws ServletException, IOException {
        request.setAttribute("reportList", list);
        request.setAttribute("reportType", reportType);
        request.getRequestDispatcher("/views/admin-report.jsp").forward(request, response);
    }
}
