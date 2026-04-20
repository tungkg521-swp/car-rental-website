/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.sql.Date;
import java.sql.Timestamp;

/**
 *
 * @author ADMIN
 */
public class ContractModel {

    private int contractId;
    private int bookingId;
    private int customerId;
    private int staffId;
    private int carId;

    private Timestamp contractStartTime;
    private Timestamp contractEndTime;

    private String contractStatus;

    private double dailyPrice;
    private double depositAmount;
    private double totalAmount;

    private Timestamp signedAt;
    private Timestamp createdAt;

    private String note;

    // Extra fields for display
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String carName;

    private Integer handoverCheckId;
    private Boolean customerConfirmed;
    private String customerConfirmNote;
    private Timestamp customerConfirmTime;
    private String noShowNote;

    public ContractModel() {
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public Timestamp getContractStartTime() {
        return contractStartTime;
    }

    public void setContractStartTime(Timestamp contractStartTime) {
        this.contractStartTime = contractStartTime;
    }

    public Timestamp getContractEndTime() {
        return contractEndTime;
    }

    public void setContractEndTime(Timestamp contractEndTime) {
        this.contractEndTime = contractEndTime;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public double getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(double dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public double getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(double depositAmount) {
        this.depositAmount = depositAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Timestamp getSignedAt() {
        return signedAt;
    }

    public void setSignedAt(Timestamp signedAt) {
        this.signedAt = signedAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public Integer getHandoverCheckId() {
        return handoverCheckId;
    }

    public void setHandoverCheckId(Integer handoverCheckId) {
        this.handoverCheckId = handoverCheckId;
    }

    public Boolean getCustomerConfirmed() {
        return customerConfirmed;
    }

    public void setCustomerConfirmed(Boolean customerConfirmed) {
        this.customerConfirmed = customerConfirmed;
    }

    public String getCustomerConfirmNote() {
        return customerConfirmNote;
    }

    public void setCustomerConfirmNote(String customerConfirmNote) {
        this.customerConfirmNote = customerConfirmNote;
    }

    public Timestamp getCustomerConfirmTime() {
        return customerConfirmTime;
    }

    public void setCustomerConfirmTime(Timestamp customerConfirmTime) {
        this.customerConfirmTime = customerConfirmTime;
    }

    public String getNoShowNote() {
        return noShowNote;
    }

    public void setNoShowNote(String noShowNote) {
        this.noShowNote = noShowNote;
    }
    
    
}
