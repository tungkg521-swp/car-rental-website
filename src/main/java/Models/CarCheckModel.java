package models;

import java.sql.Timestamp;

public class CarCheckModel {
    private int checkId;
    private int contractId;
    private int carId;
    private int checkedBy;
    private Timestamp checkTime;

    private String fuelLevel;
    private String exteriorNote;
    private String interiorNote;
    private String checkResult;
    private String note;
    
    private Integer odometerKm;

    public CarCheckModel() {
    }

    public int getCheckId() {
        return checkId;
    }

    public void setCheckId(int checkId) {
        this.checkId = checkId;
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public int getCheckedBy() {
        return checkedBy;
    }

    public void setCheckedBy(int checkedBy) {
        this.checkedBy = checkedBy;
    }

    public Timestamp getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Timestamp checkTime) {
        this.checkTime = checkTime;
    }

    public String getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(String fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public String getExteriorNote() {
        return exteriorNote;
    }

    public void setExteriorNote(String exteriorNote) {
        this.exteriorNote = exteriorNote;
    }

    public String getInteriorNote() {
        return interiorNote;
    }

    public void setInteriorNote(String interiorNote) {
        this.interiorNote = interiorNote;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getOdometerKm() {
        return odometerKm;
    }

    public void setOdometerKm(Integer odometerKm) {
        this.odometerKm = odometerKm;
    }
}