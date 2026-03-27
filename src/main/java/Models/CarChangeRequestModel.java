package models;

import java.sql.Timestamp;

public class CarChangeRequestModel {

    private int requestId;
    private int bookingId;
    private int oldCarId;
    private int newCarId;
    private String requestedBy;
    private String status;
    private String reason;
    private Timestamp createdAt;
    private Timestamp resolvedAt;

    public CarChangeRequestModel() {
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getOldCarId() {
        return oldCarId;
    }

    public void setOldCarId(int oldCarId) {
        this.oldCarId = oldCarId;
    }

    public int getNewCarId() {
        return newCarId;
    }

    public void setNewCarId(int newCarId) {
        this.newCarId = newCarId;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Timestamp resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}