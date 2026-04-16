package models;

import java.math.BigDecimal;

public class CarModel {

    private int carId;
    private String modelName;
    private int modelYear;
    private BigDecimal pricePerDay; 
    private int seatCount;
    private String fuelType;
    private String transmission;
    private String brandName;
    private String typeName;
    private String plateNumber;

    // IMAGE
    private String imageUrl;      // list
    private String imageFolder;   // detail

    // DETAIL EXTRA
    private String description;
    private String status;
    
    public CarModel() {
}

    public CarModel(int carId, String modelName, int modelYear, BigDecimal pricePerDay, int seatCount, String fuelType, String transmission, String brandName, String typeName, String imageFolder, String status) {
        this.carId = carId;
        this.modelName = modelName;
        this.modelYear = modelYear;
        this.pricePerDay = pricePerDay;
        this.seatCount = seatCount;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.brandName = brandName;
        this.typeName = typeName;
        this.imageFolder = imageFolder;
        this.status = status;
    }

    public CarModel(int carId, String modelName, int modelYear,
                    BigDecimal pricePerDay, int seatCount,
                    String fuelType, String transmission,
                    String brandName, String typeName,
                    String imageUrl, String imageFolder,
                    String description, String status) {

        this.carId = carId;
        this.modelName = modelName;
        this.modelYear = modelYear;
        this.pricePerDay = pricePerDay;
        this.seatCount = seatCount;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.brandName = brandName;
        this.typeName = typeName;
        this.imageUrl = imageUrl;
        this.imageFolder = imageFolder;
        this.description = description;
        this.status = status;
    }

    // ===== GETTERS =====
    public int getCarId() { return carId; }
    public String getModelName() { return modelName; }
    public int getModelYear() { return modelYear; }
    public BigDecimal getPricePerDay() { return pricePerDay; }
    public int getSeatCount() { return seatCount; }
    public String getFuelType() { return fuelType; }
    public String getTransmission() { return transmission; }
    public String getBrandName() { return brandName; }
    public String getTypeName() { return typeName; }

    public String getImageUrl() { return imageUrl; }
    public String getImageFolder() { return imageFolder; }

    public String getDescription() { return description; }
    public String getStatus() { return status; }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setModelYear(int modelYear) {
        this.modelYear = modelYear;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public void setSeatCount(int seatCount) {
        this.seatCount = seatCount;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setImageFolder(String imageFolder) {
        this.imageFolder = imageFolder;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
    
}
