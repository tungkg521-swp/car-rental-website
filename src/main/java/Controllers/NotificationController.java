package Controllers;

import models.NotificationModel;
import service.NotificationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import models.AccountModel;

public class NotificationController extends HttpServlet {

    NotificationService service = new NotificationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        AccountModel acc = (AccountModel) request.getSession().getAttribute("ACCOUNT");
        if (acc == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        int accountId = acc.getAccountId();
        List<NotificationModel> list = service.getNotificationsByAccount(accountId);
        request.setAttribute("notifications", list);
        request.getRequestDispatcher("/views/notifications.jsp")
                .forward(request, response);
    }
}
