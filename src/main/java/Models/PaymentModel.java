/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;



import java.math.BigDecimal;
import java.sql.Timestamp;

public class PaymentModel {

    private int paymentId;
    private int bookingId;
    private BigDecimal amount;
    private String paymentType;
    private String paymentMethod;
    private String paymentStatus;
    private String gatewayTransactionId;
    private String gatewayOrderRef;
    private String providerResponseCode;
    private Timestamp callbackReceivedAt;
    private boolean checksumVerified;
    private Timestamp paidAt;
    private Timestamp createdAt;
    private String rawResponse;

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getGatewayTransactionId() {
        return gatewayTransactionId;
    }

    public void setGatewayTransactionId(String gatewayTransactionId) {
        this.gatewayTransactionId = gatewayTransactionId;
    }

    public String getGatewayOrderRef() {
        return gatewayOrderRef;
    }

    public void setGatewayOrderRef(String gatewayOrderRef) {
        this.gatewayOrderRef = gatewayOrderRef;
    }

    public String getProviderResponseCode() {
        return providerResponseCode;
    }

    public void setProviderResponseCode(String providerResponseCode) {
        this.providerResponseCode = providerResponseCode;
    }

    public Timestamp getCallbackReceivedAt() {
        return callbackReceivedAt;
    }

    public void setCallbackReceivedAt(Timestamp callbackReceivedAt) {
        this.callbackReceivedAt = callbackReceivedAt;
    }

    public boolean isChecksumVerified() {
        return checksumVerified;
    }

    public void setChecksumVerified(boolean checksumVerified) {
        this.checksumVerified = checksumVerified;
    }

    public Timestamp getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Timestamp paidAt) {
        this.paidAt = paidAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }
}