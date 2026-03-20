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
            "/staff/notification",
            "/staff/send-notification",
            "/staff/vouchers"
    );

    private static final Set<String> ADMIN_ALLOWED_PATHS = Set.of(
            "/dashboard/admin",
            "/admin/review"
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

        // PUBLIC URLS
        if (path.equals("/")
                || path.equals("/home")
                || path.equals("/login")
                || path.equals("/register")
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
        }

        if (path.equals("/dashboard/admin") || path.startsWith("/admin/")) {
            if (!ADMIN_ALLOWED_PATHS.contains(path)) {
                if (account != null
                        && (account.getRoleId() == RoleConstants.STAFF
                        || account.getRoleId() == RoleConstants.ADMIN)) {
                    response.sendRedirect(ctx + "/dashboard");
                } else {
                    response.sendRedirect(ctx + "/home");
                }
                return;
            }
        }

        // Chưa login
        if (account == null) {

            // Khu dashboard staff/admin
            if (path.equals("/dashboard/staff")
                    || path.equals("/dashboard/admin")
                    || path.startsWith("/staff/")
                    || path.startsWith("/admin/")) {
                response.sendRedirect(ctx + "/dashboard");
                return;
            }

            // Khu bắt buộc customer phải login
            if (path.startsWith("/booking")
                    || path.startsWith("/wishlist")
                    || path.startsWith("/review")
                    || path.startsWith("/customer/")) {
                response.sendRedirect(ctx + "/login");
                return;
            }

            // URL lạ còn lại
            response.sendRedirect(ctx + "/home");
            return;
        }

        int roleId = account.getRoleId();

        // CUSTOMER URLS
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

        // STAFF URLS
        if (path.equals("/dashboard/staff") || path.startsWith("/staff/")) {
            if (roleId != RoleConstants.STAFF && roleId != RoleConstants.ADMIN) {
                denyAccess(request, response, "Bạn không có quyền truy cập khu vực nhân viên.");
                return;
            }

            chain.doFilter(req, res);
            return;
        }

        // ADMIN URLS
        if (path.equals("/dashboard/admin") || path.startsWith("/admin/")) {
            if (roleId != RoleConstants.ADMIN) {
                denyAccess(request, response, "Bạn không có quyền truy cập khu vực quản trị.");
                return;
            }

            chain.doFilter(req, res);
            return;
        }

        // URL lạ còn lại
        if (roleId == RoleConstants.STAFF || roleId == RoleConstants.ADMIN) {
            response.sendRedirect(ctx + "/dashboard");
        } else {
            response.sendRedirect(ctx + "/home");
        }
    }

    private void denyAccess(HttpServletRequest request,
            HttpServletResponse response,
            String message) throws ServletException, IOException {

        request.setAttribute("message", message);
        RequestDispatcher rd = request.getRequestDispatcher("/views/access-denied.jsp");
        rd.forward(request, response);
    }
}
