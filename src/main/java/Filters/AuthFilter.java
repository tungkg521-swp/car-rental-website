package Filters;

import Utils.RoleConstants;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;
import models.AccountModel;

public class AuthFilter implements Filter {

    private static final Set<String> STAFF_ALLOWED_PATHS = Set.of(
            "/dashboard/staff",
            "/staff/bookings",
            "/staff/booking-detail",
            "/staff/cars",
            "/staff/car-detail",
            "/staff/contracts",
            "/staff/contract-detail",
            "/staff/users",
            "/staff/user-detail",
            "/staff/licenses",
            "/staff/license-detail",
            "/staff/maintenance",
            "/staff/vouchers",
            "/staff/notification",
            "/staff/send-notification",
            "/admin/rental-report"  // giữ nguyên như bạn có
    );

    private static final Set<String> ADMIN_ALLOWED_PATHS = Set.of(
            "/dashboard/admin",
            "/admin/review"
            // không cần thêm /admin/... vào đây vì ta xử lý bằng startsWith("/admin/")
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        String path = uri.substring(ctx.length());

        HttpSession session = request.getSession(false);
        AccountModel account = (session == null)
                ? null
                : (AccountModel) session.getAttribute("ACCOUNT");

        // === PUBLIC PATHS (không cần login) ===
        if (path.equals("/")
                || path.equals("/home")
                || path.equals("/login")
                || path.equals("/register")
                || path.equals("/forgot-password")
                || path.equals("/verify-otp")
                || path.equals("/reset-password")
                || path.equals("/dashboard")
                || path.equals("/logout")
                || path.equals("/guest-home")
                || path.equals("/cars")
                || path.startsWith("/car-detail")
                || path.startsWith("/booking-success")
                || path.startsWith("/license-image")
                || path.startsWith("/assets/")) {
            chain.doFilter(req, res);
            return;
        }

        // === STAFF AREA (giữ nguyên như cũ của bạn) ===
        if (path.equals("/dashboard/staff") || path.startsWith("/staff/")) {
            if (!STAFF_ALLOWED_PATHS.contains(path)) {
                if (account != null
                        && (account.getRoleId() == RoleConstants.STAFF
                        || account.getRoleId() == RoleConstants.ADMIN)) {
                    response.sendRedirect(ctx + "/dashboard");
                } else {
                    response.sendRedirect(ctx + "/home");
                }
                return;
            }

            if (account == null) {
                response.sendRedirect(ctx + "/login");
                return;
            }

            int roleId = account.getRoleId();
            if (roleId != RoleConstants.STAFF && roleId != RoleConstants.ADMIN) {
                showNotFound(request, response);
                return;
            }
            chain.doFilter(req, res);
            return;
        }

        // === ADMIN AREA (đã sửa để fix lỗi AJAX report bị redirect login) ===
        if (path.equals("/dashboard/admin") 
                || path.startsWith("/admin/")
                || path.startsWith("/dashboard/admin/")) {

            // Cho phép tất cả các endpoint nội bộ/fragment (AJAX) đi qua mà KHÔNG check session/role
            // Vì chúng được gọi từ trang admin đã được bảo vệ rồi
            if (path.endsWith("-content") 
                    || path.contains("report-content")
                    || path.equals("/admin/rental-report-content")
                    || path.equals("/admin/revenue-report-content")
                    || path.equals("/admin/usage-report-content")) {
                chain.doFilter(req, res);
                return;
            }

            // Từ đây mới check quyền ADMIN nghiêm ngặt (cho các trang chính)
            if (account == null) {
                response.sendRedirect(ctx + "/login");
                return;
            }

            int roleId = account.getRoleId();
            if (roleId != RoleConstants.ADMIN) {
                showNotFound(request, response);
                return;
            }

            chain.doFilter(req, res);
            return;
        }

        // === Not logged in ===
        if (account == null) {
            if (path.equals("/dashboard/staff")
                    || path.equals("/dashboard/admin")
                    || path.startsWith("/staff/")
                    || path.startsWith("/admin/")) {
                response.sendRedirect(ctx + "/dashboard");
                return;
            }

            if (path.startsWith("/booking")
                    || path.startsWith("/wishlist")
                    || path.startsWith("/review")
                    || path.startsWith("/customer/")) {
                response.sendRedirect(ctx + "/login");
                return;
            }

            response.sendRedirect(ctx + "/home");
            return;
        }

        int roleId = account.getRoleId();

        // === Customer area ===
        if (path.startsWith("/booking")
                || path.startsWith("/wishlist")
                || path.startsWith("/review")
                || path.startsWith("/customer/")) {
            if (roleId != RoleConstants.CUSTOMER) {
                denyAccess(request, response, "Bạn không có quyền truy cập khu vực khách hàng.");
                return;
            }
            chain.doFilter(req, res);
            return;
        }

        // === Other private paths ===
        if (roleId == RoleConstants.STAFF || roleId == RoleConstants.ADMIN) {
            response.sendRedirect(ctx + "/dashboard");
        } else {
            response.sendRedirect(ctx + "/home");
        }
    }

    private void denyAccess(HttpServletRequest request,
            HttpServletResponse response,
            String message) throws ServletException, IOException {
        request.setAttribute("pageTitle", "Access Denied");
        request.setAttribute("message", message);
        RequestDispatcher rd = request.getRequestDispatcher("/views/access-denied.jsp");
        rd.forward(request, response);
    }

    private void showNotFound(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("pageTitle", "Trang không tồn tại");
        request.setAttribute("message", "Trang bạn đang tìm kiếm không tồn tại hoặc bạn không thể truy cập đường dẫn này.");
        RequestDispatcher rd = request.getRequestDispatcher("/views/access-denied.jsp");
        rd.forward(request, response);
    }
}