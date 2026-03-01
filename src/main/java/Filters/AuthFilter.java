package Filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import models.AccountModel;

public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String uri = request.getRequestURI();
        String ctx = request.getContextPath();

       // ===== 1) GUEST URLs =====
if (uri.equals(ctx + "/")
        || uri.startsWith(ctx + "/home")
        || uri.startsWith(ctx + "/login")
        || uri.startsWith(ctx + "/register")   // ← THÊM DÒNG NÀY
        || uri.startsWith(ctx + "/logout")
        || uri.startsWith(ctx + "/cars")
        || uri.startsWith(ctx + "/car-detail")
        || uri.startsWith(ctx + "/booking-success")
        || uri.contains("/assets/")) {

    chain.doFilter(req, res);
    return;
}
        // Cho staff vào trực tiếp (tạm thời)
        if (uri.contains("/staff")) {
            chain.doFilter(req, res);
            return;
        }

        // ===== 2) CHECK LOGIN =====
        HttpSession session = request.getSession(false);
        AccountModel account = (session == null)
                ? null
                : (AccountModel) session.getAttribute("ACCOUNT");

        if (account == null) {
            response.sendRedirect(ctx + "/login");
            return;
        }

        // ===== 3) LOGIN OK =====
        chain.doFilter(req, res);
    }
}
