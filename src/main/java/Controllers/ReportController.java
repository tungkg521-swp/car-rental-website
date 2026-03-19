package Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.RentalReportModel;
import service.ReportService;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ReportController", urlPatterns = {"/admin/rental-report"})
public class ReportController extends HttpServlet {

    private ReportService reportService = new ReportService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<RentalReportModel> list = reportService.getAllRentalReports();

        request.setAttribute("rentalList", list);

        request.getRequestDispatcher("/admin/rental-report.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}