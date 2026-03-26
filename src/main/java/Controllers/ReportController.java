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
    "/admin/rental-report-content",
    "/admin/usage-report-content",
    "/admin/revenue-report-content",
    "/admin/report-summary"   // ← mới: KPI overview
})
public class ReportController extends HttpServlet {

    private ReportService reportService = new ReportService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String servletPath = request.getServletPath();
        String startDate = request.getParameter("startDate");  // yyyy-MM-dd
        String endDate   = request.getParameter("endDate");

        List<ReportModel> reportList = null;
        String reportType = null;

        if (servletPath.equals("/admin/rental-report-content")) {
            reportList = reportService.getAllRentalReports(startDate, endDate);
            reportType = "RENTAL";
        } else if (servletPath.equals("/admin/usage-report-content")) {
            reportList = reportService.getVehicleUsageReports(startDate, endDate);
            reportType = "USAGE";
        } else if (servletPath.equals("/admin/revenue-report-content")) {
            reportList = reportService.getRevenueReports(startDate, endDate);
            reportType = "REVENUE";
        } else if (servletPath.equals("/admin/report-summary")) {
            // Trả về JSON summary cho KPI cards
            Map<String, Object> summary = reportService.getReportSummary(startDate, endDate);
            response.setContentType("application/json");
            response.getWriter().write(new Gson().toJson(summary)); // cần import com.google.gson.Gson
            return;
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("reportList", reportList);
        request.setAttribute("reportType", reportType);
        request.setAttribute("startDate", startDate);   // truyền lại filter cho JSP
        request.setAttribute("endDate", endDate);

        request.getRequestDispatcher("/views/admin-report.jsp").forward(request, response);
    }
}