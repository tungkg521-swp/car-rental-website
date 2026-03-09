package models;

import java.util.Date;

public class ReviewModel {

    private int reviewId;
    private int customerId;
    private int carId;
    private int bookingId;
    private int rating;
    private String comment;
    private Date createdAt;

    // thêm biến để lưu tên khách hàng
    private String customerName;

    public ReviewModel() {
    }

    public ReviewModel(int customerId, int carId, int bookingId, int rating, String comment) {
        this.customerId = customerId;
        this.carId = carId;
        this.bookingId = bookingId;
        this.rating = rating;
        this.comment = comment;
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // getter setter cho customerName
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}