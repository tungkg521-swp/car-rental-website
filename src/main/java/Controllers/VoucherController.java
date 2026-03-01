package Controllers;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.VoucherModel;
import service.VoucherService;

/**
 * Servlet for handling voucher operations
 */
@WebServlet(name = "VoucherController", urlPatterns = {"/voucher"})
public class VoucherController extends HttpServlet {

    private VoucherService voucherService = new VoucherService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null || action.equals("list")) {
            listVouchers(request, response);
        } else if (action.equals("detail")) {
            showVoucherDetail(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action != null && action.equals("create")) {
            createVoucher(request, response);
        } else if (action != null && action.equals("update")) {
            updateVoucher(request, response);
        } else if (action != null && action.equals("delete")) {
            deleteVoucher(request, response);
        }
    }

    // List all vouchers
    private void listVouchers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<VoucherModel> vouchers = voucherService.getVoucher();
        request.setAttribute("vouchers", vouchers);
        request.getRequestDispatcher("/views/voucher-list.jsp").forward(request, response);
    }

    // Show voucher detail
    private void showVoucherDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String voucherIdRaw = request.getParameter("voucherId");

        if (voucherIdRaw == null || voucherIdRaw.isEmpty()) {
            response.sendRedirect("/voucher?action=list");
            return;
        }

        try {
            int voucherId = Integer.parseInt(voucherIdRaw);
            VoucherModel voucher = voucherService.getVoucherById(voucherId);

            if (voucher != null) {
                request.setAttribute("voucher", voucher);
                request.getRequestDispatcher("/views/voucher-detail.jsp").forward(request, response);
            } else {
                response.sendRedirect("/voucher?action=list");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("/voucher?action=list");
        }
    }

    // Create new voucher
    private void createVoucher(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String code = request.getParameter("code");
            String discountStr = request.getParameter("discount");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            String status = request.getParameter("status");

            // Validate input
            if (code == null || code.isEmpty() || discountStr == null || discountStr.isEmpty()
                    || startDateStr == null || startDateStr.isEmpty() || endDateStr == null || endDateStr.isEmpty()) {
                request.setAttribute("error", "All fields are required");
                request.getRequestDispatcher("/views/voucher-create.jsp").forward(request, response);
                return;
            }

            java.math.BigDecimal discount = new java.math.BigDecimal(discountStr);
            Date startDate = Date.valueOf(startDateStr);
            Date endDate = Date.valueOf(endDateStr);

            if (status == null || status.isEmpty()) {
                status = "ACTIVE";
            }

            VoucherModel newVoucher = new VoucherModel(code, discount, startDate, endDate, status);
            boolean success = voucherService.createVoucher(newVoucher);

            if (success) {
                request.setAttribute("message", "Voucher created successfully");
                response.sendRedirect("/voucher?action=list");
            } else {
                request.setAttribute("error", "Failed to create voucher");
                request.getRequestDispatcher("/views/voucher-create.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/views/voucher-create.jsp").forward(request, response);
        }
    }

    // Update existing voucher
    private void updateVoucher(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String voucherIdStr = request.getParameter("voucherId");
            String code = request.getParameter("code");
            String discountStr = request.getParameter("discount");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            String status = request.getParameter("status");

            // Validate input
            if (voucherIdStr == null || voucherIdStr.isEmpty() || code == null || code.isEmpty()
                    || discountStr == null || discountStr.isEmpty() || startDateStr == null || startDateStr.isEmpty()
                    || endDateStr == null || endDateStr.isEmpty()) {
                request.setAttribute("error", "All fields are required");
                request.getRequestDispatcher("/views/voucher-detail.jsp").forward(request, response);
                return;
            }

            int voucherId = Integer.parseInt(voucherIdStr);
            java.math.BigDecimal discount = new java.math.BigDecimal(discountStr);
            Date startDate = Date.valueOf(startDateStr);
            Date endDate = Date.valueOf(endDateStr);

            if (status == null || status.isEmpty()) {
                status = "ACTIVE";
            }

            VoucherModel updatedVoucher = new VoucherModel(voucherId, code, discount, startDate, endDate, status);
            boolean success = voucherService.updateVoucher(updatedVoucher);

            if (success) {
                request.setAttribute("message", "Voucher updated successfully");
                response.sendRedirect("/voucher?action=detail&voucherId=" + voucherId);
            } else {
                request.setAttribute("error", "Failed to update voucher");
                request.getRequestDispatcher("/views/voucher-detail.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/views/voucher-detail.jsp").forward(request, response);
        }
    }

    // Delete voucher
    private void deleteVoucher(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String voucherIdStr = request.getParameter("voucherId");

            if (voucherIdStr == null || voucherIdStr.isEmpty()) {
                response.sendRedirect("/voucher?action=list");
                return;
            }

            int voucherId = Integer.parseInt(voucherIdStr);
            boolean success = voucherService.deleteVoucher(voucherId);

            if (success) {
                request.setAttribute("message", "Voucher deleted successfully");
            } else {
                request.setAttribute("error", "Failed to delete voucher");
            }

            response.sendRedirect("/voucher?action=list");

        } catch (NumberFormatException e) {
            response.sendRedirect("/voucher?action=list");
        }
    }
}
