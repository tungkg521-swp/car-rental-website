package Controllers;

import DALs.CustomerDAO;
import Utils.RoleConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import models.AccountModel;
import models.CustomerModel;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String redirectUrl = request.getContextPath() + "/home";

        if (session != null) {

            AccountModel account = (AccountModel) session.getAttribute("ACCOUNT");
            CustomerModel customer = (CustomerModel) session.getAttribute("CUSTOMER");

            if (customer != null) {
                CustomerDAO dao = new CustomerDAO();
                dao.updateStatus(customer.getCustomerId(), "INACTIVE");
            }

            if (account != null) {
                if (account.getRoleId() == RoleConstants.STAFF
                        || account.getRoleId() == RoleConstants.ADMIN) {
                    redirectUrl = request.getContextPath() + "/dashboard";
                }
            }

            session.invalidate();
        }

        response.sendRedirect(redirectUrl);
    }
}