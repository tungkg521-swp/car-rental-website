/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

public class MaintenanceModel {
    private int maintenanceId;
    private int carId;
    private String modelName;
    private String licensePlate;
    private String maintenanceType;
    private Date scheduledDate;
    private int mileageScheduled;
    private String description;
    private BigDecimal estimatedCost;
    private String status;
    private Integer createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ================== THÊM FIELD ẢNH ==================
    private String carImageUrl;

    public MaintenanceModel() {}

    // Constructor cũ giữ nguyên
    public MaintenanceModel(int maintenanceId, String modelName, String licensePlate, 
                            String maintenanceType, Date scheduledDate, String status) {
        this.maintenanceId = maintenanceId;
        this.modelName = modelName;
        this.licensePlate = licensePlate;
        this.maintenanceType = maintenanceType;
        this.scheduledDate = scheduledDate;
        this.status = status;
    }

    // ================== GETTER & SETTER MỚI ==================
    public String getCarImageUrl() { return carImageUrl; }
    public void setCarImageUrl(String carImageUrl) { this.carImageUrl = carImageUrl; }

    // Các getter/setter cũ giữ nguyên (không thay đổi)
    public int getMaintenanceId() { return maintenanceId; }
    public void setMaintenanceId(int maintenanceId) { this.maintenanceId = maintenanceId; }

    public int getCarId() { return carId; }
    public void setCarId(int carId) { this.carId = carId; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getMaintenanceType() { return maintenanceType; }
    public void setMaintenanceType(String maintenanceType) { this.maintenanceType = maintenanceType; }

    public Date getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(Date scheduledDate) { this.scheduledDate = scheduledDate; }

    public int getMileageScheduled() { return mileageScheduled; }
    public void setMileageScheduled(int mileageScheduled) { this.mileageScheduled = mileageScheduled; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}