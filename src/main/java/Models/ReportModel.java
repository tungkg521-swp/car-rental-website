package models;

import java.math.BigDecimal;
import java.sql.Date;

public class ReportModel {

    private Integer contractId;       // từ rental_contract
    private Integer bookingId;        // fallback nếu chưa có contract
    private Integer carId;
    private String plateNumber;
    private String modelName;
    private String brandName;
    private String typeName;
    private Integer customerId;
    private String customerName;
    private String customerPhone;
    private Date startDate;
    private Date endDate;
    private int rentalDays;
    private String status;
    private BigDecimal totalPrice;
    private String staffName;
    private String note;
    private Long rentalCount;       // số lần thuê
    private Integer totalRentalDays; // tổng số ngày đã thuê
    private BigDecimal totalRevenue; // tổng doanh thu từ xe đó (tùy chọn)
    private Date lastRentalDate;    // lần thuê gần nhất
    private Integer currentMileage; // mileage hiện tại
    private Date lastMaintenanceDate;
    private Date revenueDate;

    // Constructor rỗng + full constructor + getters/setters
    public ReportModel() {
    }

    // getters & setters (tạo bằng NetBeans: Right-click → Insert Code → Getter and Setter)
    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getRentalDays() {
        return rentalDays;
    }

    public void setRentalDays(int rentalDays) {
        this.rentalDays = rentalDays;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getRentalCount() {
        return rentalCount;
    }

    public void setRentalCount(Long rentalCount) {
        this.rentalCount = rentalCount;
    }

    public Integer getTotalRentalDays() {
        return totalRentalDays;
    }

    public void setTotalRentalDays(Integer totalRentalDays) {
        this.totalRentalDays = totalRentalDays;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Date getLastRentalDate() {
        return lastRentalDate;
    }

    public void setLastRentalDate(Date lastRentalDate) {
        this.lastRentalDate = lastRentalDate;
    }

    public Integer getCurrentMileage() {
        return currentMileage;
    }

    public void setCurrentMileage(Integer currentMileage) {
        this.currentMileage = currentMileage;
    }

    public Date getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }

    public void setLastMaintenanceDate(Date lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    public Date getRevenueDate() {
        return revenueDate;
    }

    public void setRevenueDate(Date revenueDate) {
        this.revenueDate = revenueDate;
    }
    

}
