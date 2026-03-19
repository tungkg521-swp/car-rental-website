package Controllers;

import DALs.AccountDAO;
import DALs.StaffDAO;
import Utils.RoleConstants;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.AccountModel;
import models.StaffModel;
import service.AuthenticationService;

@WebServlet("/dashboard")
public class DashboardLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/dashboard-login.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        AuthenticationService authService = new AuthenticationService();
        AccountModel account = authService.authenticate(email, password);

        if (account == null) {
            request.setAttribute("error", "Email hoặc mật khẩu không đúng");
            request.getRequestDispatcher("/views/dashboard-login.jsp")
                    .forward(request, response);
            return;
        }

        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            request.setAttribute("error", "Tài khoản đã bị khóa");
            request.getRequestDispatcher("/views/dashboard-login.jsp")
                    .forward(request, response);
            return;
        }

        if (account.getRoleId() == RoleConstants.CUSTOMER) {
            request.setAttribute("error", "Tài khoản khách hàng không được phép đăng nhập Dashboard");
            request.getRequestDispatcher("/views/dashboard-login.jsp")
                    .forward(request, response);
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("ACCOUNT", account);

        AccountDAO accountDAO = new AccountDAO();
        accountDAO.updateLastLogin(account.getAccountId());

        if (account.getRoleId() == RoleConstants.STAFF) {
            StaffDAO staffDAO = new StaffDAO();
            StaffModel staff = staffDAO.findByAccountId(account.getAccountId());

            if (staff == null) {
                request.setAttribute("error", "Không tìm thấy hồ sơ nhân viên");
                request.getRequestDispatcher("/views/dashboard-login.jsp")
                        .forward(request, response);
                return;
            }

            session.setAttribute("STAFF", staff);
            response.sendRedirect(request.getContextPath() + "/dashboard/staff");
            return;
        }

        if (account.getRoleId() == RoleConstants.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/dashboard/admin");
            return;
        }

        request.setAttribute("error", "Vai trò tài khoản không hợp lệ");
        request.getRequestDispatcher("/views/dashboard-login.jsp")
                .forward(request, response);
    }
}