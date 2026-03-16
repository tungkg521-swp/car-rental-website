package service;

import DALs.NotificationDAO;
import models.NotificationModel;
import java.util.List;
import models.CustomerModel;

public class NotificationService {

    NotificationDAO dao = new NotificationDAO();

    public List<NotificationModel> getNotificationsByAccount(int accountId) {

        int customerId = dao.getCustomerIdByAccountId(accountId);

        if (customerId == -1) {
            return List.of(); // không có customer
        }

        return dao.findByCustomerId(customerId);
    }

    public List<NotificationModel> getStaffNotifications() {
        return dao.findForStaff();
    }

    public boolean sendNotification(String title, String content, String sendType, String customerIdStr) {

        if ("all".equals(sendType)) {

            return dao.insertToAll(title, content);

        } else {

            int customerId = Integer.parseInt(customerIdStr);
            return dao.insertToCustomer(customerId, title, content);

        }
    }

    public List<CustomerModel> getAllCustomers() {
        return dao.getAllCustomers();
    }

}
