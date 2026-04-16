package models;

import java.util.Date;

public class NotificationModel {

    private int notificationId;
    private int customerId;
    private String title;
    private String content;
    private boolean isRead;
    private Date createdAt;
    private int referenceId;
    private String referenceType;

    public NotificationModel() {
    }

    public NotificationModel(int notificationId, int customerId, String title, String content, boolean isRead, Date createdAt, int referenceId, String referenceType) {
        this.notificationId = notificationId;
        this.customerId = customerId;
        this.title = title;
        this.content = content;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.referenceId = referenceId;
        this.referenceType = referenceType;
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

    public int getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(int referenceId) {
        this.referenceId = referenceId;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    @Override
    public String toString() {
        return "NotificationModel{" + "notificationId=" + notificationId + ", customerId=" + customerId + ", title=" + title + ", content=" + content + ", isRead=" + isRead + ", createdAt=" + createdAt + '}';
    }

}
