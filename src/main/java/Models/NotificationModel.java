package models;

import java.util.Date;

public class NotificationModel {
    private int notificationId;
    private int customerId;
    private String title;
    private String content;
    private boolean isRead;
    private Date createdAt;

    public NotificationModel() {
    }

    public NotificationModel(int notificationId, int customerId, String title, String content, boolean isRead, Date createdAt) {
        this.notificationId = notificationId;
        this.customerId = customerId;
        this.title = title;
        this.content = content;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isIsRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "NotificationModel{" + "notificationId=" + notificationId + ", customerId=" + customerId + ", title=" + title + ", content=" + content + ", isRead=" + isRead + ", createdAt=" + createdAt + '}';
    }
    
}