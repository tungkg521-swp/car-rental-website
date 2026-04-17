package Controllers;

import models.AccountModel;
import models.VoucherModel;
import Utils.RoleConstants;

import DALs.VoucherDAO;


import java.io.IOException;
import java.sql.Date;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "VoucherController", urlPatterns = {"/staff/vouchers"})
public class VoucherController extends HttpServlet {

    
    private final VoucherDAO voucherDAO = new VoucherDAO();


    
    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        AccountModel account = (AccountModel) session.getAttribute("ACCOUNT");
        return account != null && account.getRoleId() == RoleConstants.ADMIN;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null || action.equals("list")) {
            listVouchers(request, response);
        } else if (action.equals("create")) {
            if (!isAdmin(request)) {
                request.getSession().setAttribute("error", "Only admin can create vouchers.");
                response.sendRedirect(request.getContextPath() + "/staff/vouchers?action=list");
                return;
            }
            request.getRequestDispatcher("/views/voucher.jsp").forward(request, response);
        } else if (action.equals("detail")) {
            showVoucherDetail(request, response);
        } else {
            listVouchers(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action != null && action.equals("create")) {
            if (!isAdmin(request)) {
                request.getSession().setAttribute("error", "Only admin can create vouchers.");
                response.sendRedirect(request.getContextPath() + "/staff/vouchers?action=list");
                return;
            }
            createVoucher(request, response);
        } else if (action != null && action.equals("update")) {
            if (!isAdmin(request)) {
                request.getSession().setAttribute("error", "Only admin can update vouchers.");
                response.sendRedirect(request.getContextPath() + "/staff/vouchers?action=list");
                return;
            }
            updateVoucher(request, response);
        } else if (action != null && action.equals("delete")) {
            if (!isAdmin(request)) {
                request.getSession().setAttribute("error", "Only admin can delete vouchers.");
                response.sendRedirect(request.getContextPath() + "/staff/vouchers?action=list");
                return;
            }
            deleteVoucher(request, response);
        }
    }

    
    private void listVouchers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<VoucherModel> vouchers = voucherDAO.getAllVouchers();

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


            Object voucher = voucherDAO.findById(voucherId);



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

           
            if (code == null || code.trim().isEmpty() || discountStr == null || discountStr.trim().isEmpty()
                    || type == null || type.isEmpty() || expireDateStr == null || expireDateStr.isEmpty()
                    || maxUsesStr == null || maxUsesStr.isEmpty() || minBookingAmountStr == null || minBookingAmountStr.isEmpty()) {
                request.setAttribute("error", "All fields are required");
                request.getRequestDispatcher("/views/voucher.jsp?action=create").forward(request, response);
                return;
            }

            


            Object existingVoucher = voucherDAO.findByCode(code);


            if (existingVoucher != null) {
                request.setAttribute("error", "Voucher code already exists. Please use a different code.");
                request.getRequestDispatcher("/views/voucher.jsp?action=create").forward(request, response);
                return;
            }

            
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

            
            boolean status;
            if (statusStr == null || statusStr.isEmpty()) {
                status = true;
            } else {
                status = "ACTIVE".equalsIgnoreCase(statusStr) || "1".equals(statusStr);
            }

            
            VoucherModel newVoucher = new VoucherModel(maxUses, code, discount, type, expireDate, status, minBookingAmount);

            boolean success = voucherDAO.insert(newVoucher);


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
            
            
            int maxUses = 0;
            if (maxUsesStr != null && !maxUsesStr.isEmpty()) {
                maxUses = Integer.parseInt(maxUsesStr);
            }
            
            java.math.BigDecimal minBookingAmount = java.math.BigDecimal.ZERO;
            if (minBookingAmountStr != null && !minBookingAmountStr.isEmpty()) {
                minBookingAmount = new java.math.BigDecimal(minBookingAmountStr);
            }

            VoucherModel updatedVoucher = new VoucherModel(voucherId, code, discount, type, expireDate, status, maxUses, minBookingAmount, null);

            boolean success = voucherDAO.update(updatedVoucher);


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

            boolean success = voucherDAO.delete(voucherId);


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