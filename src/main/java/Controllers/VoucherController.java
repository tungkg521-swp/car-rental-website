package Controllers;


import models.VoucherModel;
import service.VoucherService;

import java.io.IOException;
import java.sql.Date;
import java.util.List;




import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet for handling voucher operations
 */
@WebServlet(name = "VoucherController", urlPatterns = {"/staff/vouchers"})
public class VoucherController extends HttpServlet {

    private final VoucherService voucherService = new VoucherService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null || action.equals("list")) {
            listVouchers(request, response);
        } else if (action.equals("create")) {
            // Chuyển đến cùng 1 file JSP đã gộp
            request.getRequestDispatcher("/views/voucher.jsp").forward(request, response);
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
        request.getRequestDispatcher("/views/voucher.jsp").forward(request, response);
    }

    private void showVoucherDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String voucherIdRaw = request.getParameter("voucherId");

        if (voucherIdRaw == null || voucherIdRaw.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/staff/vouchers?action=list");
            return;
        }

        try {
            int voucherId = Integer.parseInt(voucherIdRaw);

            Object voucher = voucherService.getVoucherById(voucherId);


            if (voucher != null) {
                request.setAttribute("voucher", voucher);
                request.getRequestDispatcher("/views/voucher.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/staff/vouchers?action=list");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/staff/vouchers?action=list");
        }
    }

    // Create new voucher
    private void createVoucher(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String code = request.getParameter("code");
            String discountStr = request.getParameter("discount");
            String type = request.getParameter("type");
            String expireDateStr = request.getParameter("expireDate");
            String statusStr = request.getParameter("status");
            String maxUsesStr = request.getParameter("maxUses");
            String minBookingAmountStr = request.getParameter("minBookingAmount");

            // Validate input
            if (code == null || code.trim().isEmpty() || discountStr == null || discountStr.trim().isEmpty()
                    || type == null || type.isEmpty() || expireDateStr == null || expireDateStr.isEmpty()
                    || maxUsesStr == null || maxUsesStr.isEmpty() || minBookingAmountStr == null || minBookingAmountStr.isEmpty()) {
                request.setAttribute("error", "All fields are required");
                request.getRequestDispatcher("/views/voucher.jsp?action=create").forward(request, response);
                return;
            }

            // Kiểm tra code đã tồn tại

            Object existingVoucher = voucherService.getVoucherByCode(code);

            if (existingVoucher != null) {
                request.setAttribute("error", "Voucher code already exists. Please use a different code.");
                request.getRequestDispatcher("/views/voucher.jsp?action=create").forward(request, response);
                return;
            }

            // Parse discount
            java.math.BigDecimal discount;
            try {
                discount = new java.math.BigDecimal(discountStr);
                if (discount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                    request.setAttribute("error", "Discount value must be greater than 0");
                    request.getRequestDispatcher("/views/voucher.jsp?action=create").forward(request, response);
                    return;
                }
                
                if ("PERCENT".equals(type) && discount.compareTo(new java.math.BigDecimal("100")) > 0) {
                    request.setAttribute("error", "Percent discount cannot exceed 100%");
                    request.getRequestDispatcher("/views/voucher.jsp?action=create").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid discount value");
                request.getRequestDispatcher("/views/voucher.jsp?action=create").forward(request, response);
                return;
            }

            // Parse maxUses
            int maxUses;
            try {
                maxUses = Integer.parseInt(maxUsesStr);
                if (maxUses <= 0) {
                    request.setAttribute("error", "Max uses must be greater than 0");
                    request.getRequestDispatcher("/views/voucher.jsp?action=create").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid max uses value");
                request.getRequestDispatcher("/views/voucher.jsp?action=create").forward(request, response);
                return;
            }

            // Parse minBookingAmount
            java.math.BigDecimal minBookingAmount;
            try {
                minBookingAmount = new java.math.BigDecimal(minBookingAmountStr);
                if (minBookingAmount.compareTo(java.math.BigDecimal.ZERO) < 0) {
                    request.setAttribute("error", "Min booking amount cannot be negative");
                    request.getRequestDispatcher("/views/voucher.jsp?action=create").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid min booking amount");
                request.getRequestDispatcher("/views/voucher.jsp?action=create").forward(request, response);
                return;
            }

            // Parse date
            Date expireDate;
            try {
                expireDate = Date.valueOf(expireDateStr);
                Date today = new Date(System.currentTimeMillis());
                if (expireDate.before(today)) {
                    request.setAttribute("error", "Expire date must be in the future");
                    request.getRequestDispatcher("/views/voucher.jsp?action=create").forward(request, response);
                    return;
                }
            } catch (IllegalArgumentException e) {
                request.setAttribute("error", "Invalid date format. Please use YYYY-MM-DD");
                request.getRequestDispatcher("/views/voucher.jsp?action=create").forward(request, response);
                return;
            }

            // Parse status
            boolean status;
            if (statusStr == null || statusStr.isEmpty()) {
                status = true;
            } else {
                status = "ACTIVE".equalsIgnoreCase(statusStr) || "1".equals(statusStr);
            }

            // Tạo voucher với đầy đủ thông tin
            VoucherModel newVoucher = new VoucherModel(maxUses, code, discount, type, expireDate, status, minBookingAmount);
            boolean success = voucherService.createVoucher(newVoucher);

            if (success) {
                request.getSession().setAttribute("message", "Voucher created successfully");
                response.sendRedirect(request.getContextPath() + "/staff/vouchers?action=list");
            } else {
                request.setAttribute("error", "Failed to create voucher. Please check database connection.");
                request.getRequestDispatcher("/views/voucher.jsp?action=create").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "System error: " + e.getMessage());
            request.getRequestDispatcher("/views/voucher.jsp?action=create").forward(request, response);
        }
    }

    // Update existing voucher
    private void updateVoucher(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String voucherIdStr = request.getParameter("voucherId");
            String code = request.getParameter("code");
            String discountStr = request.getParameter("discount");
            String type = request.getParameter("type");
            String expireDateStr = request.getParameter("expireDate");
            String statusStr = request.getParameter("status");
            String maxUsesStr = request.getParameter("maxUses");
            String minBookingAmountStr = request.getParameter("minBookingAmount");

            // Validate input
            if (voucherIdStr == null || voucherIdStr.isEmpty() || code == null || code.isEmpty()
                    || discountStr == null || discountStr.isEmpty() || type == null || type.isEmpty()
                    || expireDateStr == null || expireDateStr.isEmpty()) {
                request.setAttribute("error", "All fields are required");
                request.getRequestDispatcher("/views/voucher.jsp?action=detail&voucherId=" + voucherIdStr).forward(request, response);
                return;
            }

            int voucherId = Integer.parseInt(voucherIdStr);
            java.math.BigDecimal discount = new java.math.BigDecimal(discountStr);
            Date expireDate = Date.valueOf(expireDateStr);
            boolean status = "ACTIVE".equalsIgnoreCase(statusStr) || "1".equals(statusStr);
            
            // Parse maxUses và minBookingAmount (có thể null khi update)
            int maxUses = 0;
            if (maxUsesStr != null && !maxUsesStr.isEmpty()) {
                maxUses = Integer.parseInt(maxUsesStr);
            }
            
            java.math.BigDecimal minBookingAmount = java.math.BigDecimal.ZERO;
            if (minBookingAmountStr != null && !minBookingAmountStr.isEmpty()) {
                minBookingAmount = new java.math.BigDecimal(minBookingAmountStr);
            }

            VoucherModel updatedVoucher = new VoucherModel(voucherId, code, discount, type, expireDate, status, maxUses, minBookingAmount, null);
            boolean success = voucherService.updateVoucher(updatedVoucher);

            if (success) {
                request.getSession().setAttribute("message", "Voucher updated successfully");
                response.sendRedirect(request.getContextPath() + "/staff/vouchers?action=detail&voucherId=" + voucherId);
            } else {
                request.setAttribute("error", "Failed to update voucher");
                request.getRequestDispatcher("/views/voucher.jsp?action=detail&voucherId=" + voucherId).forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/views/voucher.jsp?action=detail&voucherId=" + request.getParameter("voucherId")).forward(request, response);
        }
    }

    private void deleteVoucher(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String voucherIdStr = request.getParameter("voucherId");

            if (voucherIdStr == null || voucherIdStr.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/staff/vouchers?action=list");
                return;
            }

            int voucherId = Integer.parseInt(voucherIdStr);
            boolean success = voucherService.deleteVoucher(voucherId);

            if (success) {
                request.getSession().setAttribute("message", "Voucher deleted successfully");
            } else {
                request.getSession().setAttribute("error", "Failed to delete voucher");
            }

            response.sendRedirect(request.getContextPath() + "/staff/vouchers?action=list");

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/staff/vouchers?action=list");
        }
    }
}