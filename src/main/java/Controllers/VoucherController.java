package Controllers;

import models.AccountModel;
import models.VoucherModel;
import Utils.RoleConstants;
import DALs.VoucherDAO;
import java.time.LocalDate;
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
        if (session == null) {
            return false;
        }
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

        voucherDAO.refreshVoucherStatus();
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
            VoucherModel voucher = voucherDAO.findById(voucherId);

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
            String maxUsesStr = request.getParameter("maxUses");
            String minBookingAmountStr = request.getParameter("minBookingAmount");

            if (code == null || code.trim().isEmpty()
                    || discountStr == null || discountStr.trim().isEmpty()
                    || type == null || type.isEmpty()
                    || expireDateStr == null || expireDateStr.isEmpty()
                    || maxUsesStr == null || maxUsesStr.isEmpty()
                    || minBookingAmountStr == null || minBookingAmountStr.isEmpty()) {

                request.setAttribute("error", "All fields are required");
                request.getRequestDispatcher("/views/voucher.jsp?action=create").forward(request, response);
                return;
            }

            Object existingVoucher = voucherDAO.findByCode(code.trim());
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

                if ("PERCENT".equalsIgnoreCase(type)
                        && discount.compareTo(new java.math.BigDecimal("100")) > 0) {
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
                LocalDate today = LocalDate.now();

                if (expireDate.toLocalDate().isBefore(today)) {
                    request.setAttribute("error", "Expire date must be today or in the future");
                    request.getRequestDispatcher("/views/voucher.jsp?action=create").forward(request, response);
                    return;
                }
            } catch (IllegalArgumentException e) {
                request.setAttribute("error", "Invalid date format. Please use YYYY-MM-DD");
                request.getRequestDispatcher("/views/voucher.jsp?action=create").forward(request, response);
                return;
            }

            // Mới tạo luôn active, DAO sẽ tự tính lại status chuẩn trước khi insert
            boolean status = true;

            VoucherModel newVoucher = new VoucherModel(
                    maxUses,
                    code.trim(),
                    discount,
                    type,
                    expireDate,
                    status,
                    minBookingAmount
            );

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
            String maxUsesStr = request.getParameter("maxUses");
            String minBookingAmountStr = request.getParameter("minBookingAmount");

            if (voucherIdStr == null || voucherIdStr.isEmpty()
                    || code == null || code.trim().isEmpty()
                    || discountStr == null || discountStr.trim().isEmpty()
                    || type == null || type.isEmpty()
                    || expireDateStr == null || expireDateStr.isEmpty()
                    || maxUsesStr == null || maxUsesStr.isEmpty()
                    || minBookingAmountStr == null || minBookingAmountStr.isEmpty()) {

                request.setAttribute("error", "All fields are required");
                request.getRequestDispatcher("/views/voucher.jsp?action=detail&voucherId=" + voucherIdStr).forward(request, response);
                return;
            }

            int voucherId = Integer.parseInt(voucherIdStr);

            VoucherModel currentVoucher = (VoucherModel) voucherDAO.findById(voucherId);
            if (currentVoucher == null) {
                request.getSession().setAttribute("error", "Voucher not found");
                response.sendRedirect(request.getContextPath() + "/staff/vouchers?action=list");
                return;
            }

            Object voucherByCode = voucherDAO.findByCode(code.trim());
            if (voucherByCode instanceof VoucherModel) {
                VoucherModel duplicateVoucher = (VoucherModel) voucherByCode;
                if (duplicateVoucher.getVoucherId() != voucherId) {
                    request.setAttribute("error", "Voucher code already exists. Please use a different code.");
                    request.getRequestDispatcher("/views/voucher.jsp?action=detail&voucherId=" + voucherId).forward(request, response);
                    return;
                }
            }

            java.math.BigDecimal discount;
            try {
                discount = new java.math.BigDecimal(discountStr);
                if (discount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                    request.setAttribute("error", "Discount value must be greater than 0");
                    request.getRequestDispatcher("/views/voucher.jsp?action=detail&voucherId=" + voucherId).forward(request, response);
                    return;
                }

                if ("PERCENT".equalsIgnoreCase(type)
                        && discount.compareTo(new java.math.BigDecimal("100")) > 0) {
                    request.setAttribute("error", "Percent discount cannot exceed 100%");
                    request.getRequestDispatcher("/views/voucher.jsp?action=detail&voucherId=" + voucherId).forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid discount value");
                request.getRequestDispatcher("/views/voucher.jsp?action=detail&voucherId=" + voucherId).forward(request, response);
                return;
            }

            int maxUses;
            try {
                maxUses = Integer.parseInt(maxUsesStr);
                if (maxUses <= 0) {
                    request.setAttribute("error", "Max uses must be greater than 0");
                    request.getRequestDispatcher("/views/voucher.jsp?action=detail&voucherId=" + voucherId).forward(request, response);
                    return;
                }

                if (maxUses < currentVoucher.getUsedCount()) {
                    request.setAttribute("error", "Max uses cannot be less than used count (" + currentVoucher.getUsedCount() + ")");
                    request.getRequestDispatcher("/views/voucher.jsp?action=detail&voucherId=" + voucherId).forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid max uses value");
                request.getRequestDispatcher("/views/voucher.jsp?action=detail&voucherId=" + voucherId).forward(request, response);
                return;
            }

            java.math.BigDecimal minBookingAmount;
            try {
                minBookingAmount = new java.math.BigDecimal(minBookingAmountStr);
                if (minBookingAmount.compareTo(java.math.BigDecimal.ZERO) < 0) {
                    request.setAttribute("error", "Min booking amount cannot be negative");
                    request.getRequestDispatcher("/views/voucher.jsp?action=detail&voucherId=" + voucherId).forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid min booking amount");
                request.getRequestDispatcher("/views/voucher.jsp?action=detail&voucherId=" + voucherId).forward(request, response);
                return;
            }

            Date expireDate;
            try {
                expireDate = Date.valueOf(expireDateStr);
            } catch (IllegalArgumentException e) {
                request.setAttribute("error", "Invalid date format. Please use YYYY-MM-DD");
                request.getRequestDispatcher("/views/voucher.jsp?action=detail&voucherId=" + voucherId).forward(request, response);
                return;
            }

            // DAO sẽ tự tính lại status theo: còn lượt => active, hết lượt => inactive
            boolean status = true;

            VoucherModel updatedVoucher = new VoucherModel(
                    voucherId,
                    code.trim(),
                    discount,
                    type,
                    expireDate,
                    status,
                    maxUses,
                    minBookingAmount,
                    currentVoucher.getCreatedDate()
            );

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
                request.getSession().setAttribute("message", "Xóa voucher thành công.");
            } else {
                request.getSession().setAttribute("error", "Không thể xóa voucher.");
            }

            response.sendRedirect(request.getContextPath() + "/staff/vouchers?action=list");

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/staff/vouchers?action=list");
        }
    }
}
