package Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import models.NotificationModel;
import service.NotificationService;

@WebServlet(name = "SendNotificationController", urlPatterns = {"/SendNotificationController"})
public class SendNotificationController extends HttpServlet {

    // Mở trang gửi notification
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/views/send-notification.jsp")
                .forward(request, response);
    }

    // Xử lý gửi notification
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            NotificationService service = new NotificationService();
            int customerId = Integer.parseInt(request.getParameter("customerId"));
            String title = request.getParameter("title");
            String message = request.getParameter("message");

            NotificationModel notification = new NotificationModel();
            notification.setCustomerId(customerId);
            notification.setTitle(title);
            notification.setContent(message);
            notification.setIsRead(false);

            boolean result = service.sendNotification(notification);

            if (result) {
                request.setAttribute("success", "Notification sent successfully!");
            } else {
                request.setAttribute("error", "Send notification failed!");
            }

        } catch (Exception e) {
            request.setAttribute("error", "Invalid data!");
        }

        request.getRequestDispatcher("/views/send-notification.jsp")
                .forward(request, response);
    }
}
