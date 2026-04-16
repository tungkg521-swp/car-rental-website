package Controllers;

import DALs.AccountDAO;
import DALs.NotificationDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "SendNotificationController", urlPatterns = {"/staff/send-notification"})
public class SendNotificationController extends HttpServlet {

    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    // Mở trang gửi notification
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("customerList", notificationDAO.getAllCustomers());

        request.getRequestDispatcher("/views/send-notification.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String sendType = request.getParameter("sendType");
        String customerId = request.getParameter("customerId");

        boolean result;
        if ("all".equals(sendType)) {
            result = notificationDAO.insertToAll(title, content);
        } else {
            int customerIdInt = Integer.parseInt(customerId);
            result = notificationDAO.insertToCustomer(customerIdInt, title, content);
        }

        if (result) {
            request.setAttribute("success", "Notification sent successfully!");
        } else {
            request.setAttribute("error", "Send notification failed!");
        }

        request.setAttribute("customerList", notificationDAO.getAllCustomers());

        request.getRequestDispatcher("/views/send-notification.jsp")
                .forward(request, response);
    }
}
