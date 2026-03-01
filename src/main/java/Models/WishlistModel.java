/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.math.BigDecimal;

/**
 *
 * @author Admin
 */
public class WishlistModel {
    private int wishlistId;
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

    public WishlistModel(int wishlistId, int carId, String modelName, int modelYear, BigDecimal pricePerDay, int seatCount, String fuelType, String transmission, String brandName, String typeName, String imageUrl, String imageFolder) {
        this.wishlistId = wishlistId;
       
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
    }

 
    public int getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(int wishlistId) {
        this.wishlistId = wishlistId;
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

    public int getModelYear() {
        return modelYear;
    }

    public void setModelYear(int modelYear) {
        this.modelYear = modelYear;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(int seatCount) {
        this.seatCount = seatCount;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageFolder() {
        return imageFolder;
    }

    public void setImageFolder(String imageFolder) {
        this.imageFolder = imageFolder;
    }

    
    
}
