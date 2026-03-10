/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

/**
 *
 * @author trant
 */
public class MaintenanceModel {
    private int maintenanceId;
    private int carId;
    private String modelName;           // từ join với cars (tiện hiển thị)
    private String maintenanceType;
    private Date scheduledDate;
    private Date actualCompletionDate;
    private int mileageScheduled;
    private Integer mileageCompleted;
    private String description;
    private BigDecimal estimatedCost;
    private BigDecimal actualCost;
    private String status;              // SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED, OVERDUE
    private Integer createdBy;          // account_id
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer completedBy;

    public MaintenanceModel() {
    }

    public MaintenanceModel(int maintenanceId, String modelName, String maintenanceType, Date scheduledDate, String status) {
        this.maintenanceId = maintenanceId;
        this.modelName = modelName;
        this.maintenanceType = maintenanceType;
        this.scheduledDate = scheduledDate;
        this.status = status;
    }

   

    public int getMaintenanceId() {
        return maintenanceId;
    }

    public void setMaintenanceId(int maintenanceId) {
        this.maintenanceId = maintenanceId;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(String maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public Date getActualCompletionDate() {
        return actualCompletionDate;
    }

    public void setActualCompletionDate(Date actualCompletionDate) {
        this.actualCompletionDate = actualCompletionDate;
    }

    public int getMileageScheduled() {
        return mileageScheduled;
    }

    public void setMileageScheduled(int mileageScheduled) {
        this.mileageScheduled = mileageScheduled;
    }

    public Integer getMileageCompleted() {
        return mileageCompleted;
    }

    public void setMileageCompleted(Integer mileageCompleted) {
        this.mileageCompleted = mileageCompleted;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(BigDecimal estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public BigDecimal getActualCost() {
        return actualCost;
    }

    public void setActualCost(BigDecimal actualCost) {
        this.actualCost = actualCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(Integer completedBy) {
        this.completedBy = completedBy;
    }
    
    
}
