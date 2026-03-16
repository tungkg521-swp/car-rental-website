package Controllers;

import models.CustomerModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import service.NotificationService;

@WebServlet(name = "SendNotificationController", urlPatterns = {"/SendNotificationController"})
public class SendNotificationController extends HttpServlet {

    // Mở trang gửi notification
    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    NotificationService service = new NotificationService();

    request.setAttribute("customerList", service.getAllCustomers());

    request.getRequestDispatcher("/views/send-notification.jsp")
            .forward(request, response);
}
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    NotificationService service = new NotificationService();

    String title = request.getParameter("title");
    String content = request.getParameter("content");
    String sendType = request.getParameter("sendType");
    String customerId = request.getParameter("customerId");

    boolean result = service.sendNotification(title, content, sendType, customerId);

    if (result) {
        request.setAttribute("success", "Notification sent successfully!");
    } else {
        request.setAttribute("error", "Send notification failed!");
    }

    request.setAttribute("customerList", service.getAllCustomers());

    request.getRequestDispatcher("/views/send-notification.jsp")
            .forward(request, response);
}
}
