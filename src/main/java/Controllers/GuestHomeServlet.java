package Controllers;

import Utils.RoleConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import models.AccountModel;

public class GuestHomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            AccountModel account = (AccountModel) session.getAttribute("ACCOUNT");

            if (account != null
                    && (account.getRoleId() == RoleConstants.STAFF
                    || account.getRoleId() == RoleConstants.ADMIN)) {
                session.invalidate();
            }
        }

        response.sendRedirect(request.getContextPath() + "/home");
    }
}