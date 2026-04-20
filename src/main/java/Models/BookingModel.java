package models;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class BookingModel {

    private int bookingId;
    private int customerId;
    private Integer staffId;          // nullable
    private int carId;
    private Integer voucherId;        // nullable

    private Timestamp bookingDate;
    private Timestamp startTime;
    private Timestamp endTime;

    private String status;
    private String note;
    private BigDecimal totalEstimatedPrice;

    private String carName;
    private String plateNumber;
    private String imageFolder;

    private String customerName;
    private String customerEmail;
    private String customerPhone;

    private BigDecimal pricePerDay;

    private String contractStatus;
    private BigDecimal depositAmount;
    private BigDecimal remainingAmount;
    private Timestamp paymentDeadline;
    
    
    private String customerCheckStatus;
private String customerCheckReason;
private String customerCheckNote;
private Timestamp customerCheckedAt;


// getter + setter
    // ===== CONSTRUCTOR =====

    public BookingModel() {
    }

    // ===== GETTERS & SETTERS =====
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public Integer getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Integer voucherId) {
        this.voucherId = voucherId;
    }

    public Timestamp getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Timestamp bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BigDecimal getTotalEstimatedPrice() {
        return totalEstimatedPrice;
    }

    public void setTotalEstimatedPrice(BigDecimal totalEstimatedPrice) {
        this.totalEstimatedPrice = totalEstimatedPrice;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getImageFolder() {
        return imageFolder;
    }

    public void setImageFolder(String imageFolder) {
        this.imageFolder = imageFolder;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public Timestamp getPaymentDeadline() {
        return paymentDeadline;
    }

    public void setPaymentDeadline(Timestamp paymentDeadline) {
        this.paymentDeadline = paymentDeadline;
    }

    public String getCustomerCheckStatus() {
        return customerCheckStatus;
    }

    public void setCustomerCheckStatus(String customerCheckStatus) {
        this.customerCheckStatus = customerCheckStatus;
    }

    public String getCustomerCheckReason() {
        return customerCheckReason;
    }

    public void setCustomerCheckReason(String customerCheckReason) {
        this.customerCheckReason = customerCheckReason;
    }

    public String getCustomerCheckNote() {
        return customerCheckNote;
    }

    public void setCustomerCheckNote(String customerCheckNote) {
        this.customerCheckNote = customerCheckNote;
    }

    public Timestamp getCustomerCheckedAt() {
        return customerCheckedAt;
    }

    public void setCustomerCheckedAt(Timestamp customerCheckedAt) {
        this.customerCheckedAt = customerCheckedAt;
    }

    
}
