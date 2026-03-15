package service;

import DALs.NotificationDAO;
import models.NotificationModel;
import java.util.List;

public class NotificationService {

    NotificationDAO dao = new NotificationDAO();

    public List<NotificationModel> getNotifications(int customerId) {
        return dao.findByCustomerId(customerId);
    }

    public List<NotificationModel> getStaffNotifications() {
        return dao.findForStaff();
    }

    public boolean sendNotification(NotificationModel n) {
        return dao.insert(n);
    }
}
