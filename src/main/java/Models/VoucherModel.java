package models;

import java.math.BigDecimal;
import java.sql.Date;

public class VoucherModel {

    private int voucherId;
    private String code;
    private BigDecimal discount;
    private Date startDate;
    private Date endDate;
    private String status;

    // Constructor with all fields
    public VoucherModel(int voucherId, String code, BigDecimal discount,
            Date startDate, Date endDate, String status) {
        this.voucherId = voucherId;
        this.code = code;
        this.discount = discount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Constructor without voucherId (for insert)
    public VoucherModel(String code, BigDecimal discount,
            Date startDate, Date endDate, String status) {
        this.code = code;
        this.discount = discount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Getters
    public int getVoucherId() {
        return voucherId;
    }

    public String getCode() {
        return code;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
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

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "VoucherModel{"
                + "voucherId=" + voucherId
                + ", code='" + code + '\''
                + ", discount=" + discount
                + ", startDate=" + startDate
                + ", endDate=" + endDate
                + ", status='" + status + '\''
                + '}';
    }
}
