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

@WebServlet(name = "ReportController", 
            urlPatterns = {
                "/admin/rental-report",           // nếu ai đó truy cập trực tiếp (full page)
                "/admin/rental-report-content"    // AJAX gọi từ JS
            })
public class ReportController extends HttpServlet {

    private ReportService reportService = new ReportService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy dữ liệu
        List<RentalReportModel> list = reportService.getAllRentalReports();
        request.setAttribute("rentalList", list);

        // Luôn forward về file fragment JSP (nội dung bảng)
        // Đảm bảo đường dẫn JSP đúng: /admin-rental-report.jsp (không có /admin/ ở đầu nếu file nằm ở root views)
        request.getRequestDispatcher("/views/admin-rental-report.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}