package Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import models.DriverLicenseModel;
import service.DriverLicenseService;

@WebServlet(name = "StaffLicenseServlet", urlPatterns = {"/staff/licenses"})
public class StaffLicenseServlet extends HttpServlet {

    private final DriverLicenseService driverLicenseService = new DriverLicenseService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "detail": {
                viewDetail(request, response);
                break;
            }
            case "list":
            default: {
                viewList(request, response);
                break;
            }
        }
    }

    private void viewList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<DriverLicenseModel> licenses = driverLicenseService.getRequestedLicenses();
        request.setAttribute("licenses", licenses);
        request.getRequestDispatcher("/views/staff-driver-licenses.jsp")
                .forward(request, response);
    }

    private void viewDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String licenseIdRaw = request.getParameter("licenseId");
        int licenseId;

        try {
            licenseId = Integer.parseInt(licenseIdRaw);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/staff/licenses");
            return;
        }

        DriverLicenseModel license = driverLicenseService.getLicenseDetail(licenseId);
        if (license == null) {
            response.sendRedirect(request.getContextPath() + "/staff/licenses");
            return;
        }

        request.setAttribute("license", license);
        request.getRequestDispatcher("/views/staff-driver-license-detail.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String licenseIdRaw = request.getParameter("licenseId");

        int licenseId;
        try {
            licenseId = Integer.parseInt(licenseIdRaw);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/staff/licenses");
            return;
        }

        boolean ok = false;

        if ("approve".equals(action)) {
            ok = driverLicenseService.approve(licenseId);
        } else if ("reject".equals(action)) {
            ok = driverLicenseService.reject(licenseId);
        }

        // quay về detail để thấy status mới (hoặc list cũng được)
        if (ok) {
            response.sendRedirect(request.getContextPath()
                    + "/staff/licenses?action=detail&licenseId=" + licenseId);
        } else {
            response.sendRedirect(request.getContextPath() + "/views/staff-driver-licenses");
        }
    }
}