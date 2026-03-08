package models;

import java.math.BigDecimal;
import java.sql.Date;

public class VoucherModel {

    private int voucherId;
    private String code;
    private BigDecimal discount;
    private String type; // 'PERCENT' or 'AMOUNT'
    private Date expireDate;
    private boolean status;
    private Date createdDate;
    private int maxUses;           // <-- Thêm field
    private int usedCount;         // <-- Thêm field
    private BigDecimal minBookingAmount; // <-- Thêm field

    // Constructor đầy đủ cho CREATE
    public VoucherModel(int maxUses, String code, BigDecimal discount, String type, Date expireDate, boolean status, BigDecimal minBookingAmount) {
        this.code = code;
        this.discount = discount;
        this.type = type;
        this.expireDate = expireDate;
        this.status = status;
        this.maxUses = maxUses;
        this.minBookingAmount = minBookingAmount;
        this.usedCount = 0; // Mới tạo thì usedCount = 0
    }

    // Constructor cho UPDATE
    public VoucherModel(int voucherId, String code, BigDecimal discount, String type,
            Date expireDate, boolean status, int maxUses, BigDecimal minBookingAmount, Date createdDate) {
        this.voucherId = voucherId;
        this.code = code;
        this.discount = discount;
        this.type = type;
        this.expireDate = expireDate;
        this.status = status;
        this.maxUses = maxUses;
        this.minBookingAmount = minBookingAmount;
        this.createdDate = createdDate;
    }

    // Constructor từ database (khi đọc từ DB)
    public VoucherModel(int voucherId, String code, BigDecimal discountValue, String discountType,
            Date startDate, Date endDate, String statusStr, Date createdDate, 
            int maxUses, int usedCount, BigDecimal minBookingAmount) {
        this.voucherId = voucherId;
        this.code = code;
        this.discount = discountValue;
        this.type = discountType;
        this.expireDate = endDate;
        this.status = "active".equalsIgnoreCase(statusStr) || "1".equals(statusStr);
        this.createdDate = createdDate != null ? createdDate : startDate;
        this.maxUses = maxUses;
        this.usedCount = usedCount;
        this.minBookingAmount = minBookingAmount;
    }

    // Getters và Setters cho các field mới
    public int getMaxUses() {
        return maxUses;
    }

    public void setMaxUses(int maxUses) {
        this.maxUses = maxUses;
    }

    public int getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }

    public BigDecimal getMinBookingAmount() {
        return minBookingAmount;
    }

    public void setMinBookingAmount(BigDecimal minBookingAmount) {
        this.minBookingAmount = minBookingAmount;
    }

  
    public int getVoucherId() {
        return voucherId;
    }

    public String getCode() {
        return code;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getDiscountValue() {
        return discount;
    }

    public String getType() {
        return type;
    }

    public String getDiscountType() {
        return type;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public boolean isStatus() {
        return status;
    }

    public String getStatus() {
        return status ? "active" : "inactive";
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    // For backward compatibility
    public Date getStartDate() {
        return createdDate;
    }

    public Date getEndDate() {
        return expireDate;
    }

    // Setters
    public void setVoucherId(int voucherId) {
        this.voucherId = voucherId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = "active".equalsIgnoreCase(status) || "1".equals(status);
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "VoucherModel{"
                + "voucherId=" + voucherId
                + ", code='" + code + '\''
                + ", discount=" + discount
                + ", type='" + type + '\''
                + ", expireDate=" + expireDate
                + ", status=" + status
                + ", createdDate=" + createdDate
                + '}';
    }
}
