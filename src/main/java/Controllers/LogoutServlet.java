package Controllers;

import DALs.CustomerDAO;
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

        if (session != null) {

            CustomerModel customer = (CustomerModel) session.getAttribute("CUSTOMER");

            if (customer != null) {

                CustomerDAO dao = new CustomerDAO();
                dao.updateStatus(customer.getCustomerId(), "INACTIVE");

            }

            session.invalidate();
        }

        
        // quay về home (guest)
        response.sendRedirect(request.getContextPath() + "/home");
    }
}