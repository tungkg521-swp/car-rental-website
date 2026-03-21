package Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.ReportModel;
import service.ReportService;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ReportController",
        urlPatterns = {
            "/admin/rental-report-content",
            "/admin/usage-report-content",
            "/admin/revenue-report-content" // thêm để JS gọi được
        })
public class ReportController extends HttpServlet {

    private ReportService reportService = new ReportService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String servletPath = request.getServletPath();
        List<ReportModel> reportList = null;
        String reportType = null;

        if (servletPath.equals("/admin/rental-report-content")) {
            reportList = reportService.getAllRentalReports();
            reportType = "RENTAL";
        } else if (servletPath.equals("/admin/usage-report-content")) {
            reportList = reportService.getVehicleUsageReports();
            reportType = "USAGE";
        } else if (servletPath.equals("/admin/revenue-report-content")) {
            reportList = reportService.getRevenueReports();
            reportType = "REVENUE";

        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid report endpoint");
            return;
        }

        request.setAttribute("reportList", reportList);
        request.setAttribute("reportType", reportType);

        // Forward đến JSP fragment chung
        request.getRequestDispatcher("/views/admin-report.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
