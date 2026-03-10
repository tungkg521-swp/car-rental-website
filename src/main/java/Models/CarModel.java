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

    // IMAGE
    private String imageUrl;      // list
    private String imageFolder;   // detail

    // DETAIL EXTRA
    private String description;
    private String status;

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
}
