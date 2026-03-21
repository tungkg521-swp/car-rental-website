package service;

import DALs.CarDAO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import models.CarModel;

public class CarService {

    private CarDAO carDAO = new CarDAO();

    public List<CarModel> findAllAvailableCars() {
        return carDAO.findAllAvailableCars();
    }

    public CarModel getCarById(int carId) {
        return carDAO.findById(carId);
    }

      public void updateCarStatus(int carId,String status) {
         carDAO.updateStatus(carId, status);
    }
    public List<CarModel> findAllCars() {
        return carDAO.findAllCars();
    }

    public List<CarModel> searchCars(String keyword) {
        return carDAO.searchCars(keyword);
    }

    public List<CarModel> filterCars(
            String keyword,
            boolean availableOnly,
            String[] brands,
            String[] types,
            String[] fuels,
            Integer seats,
            String transmission,
            String yearRange,
            BigDecimal maxPrice) {

        return carDAO.filterCars(keyword, availableOnly, brands, types, fuels,
                seats, transmission, yearRange, maxPrice);
    }

    public boolean addCar(CarModel car) {
        return carDAO.addCar(car);
    }

public boolean updateCar(CarModel car) {
    return updateCarWithNewImages(car, new ArrayList<>());
}

    public boolean deleteCar(int carId) {
        return carDAO.deleteCar(carId);
    }
    
        public boolean addCarWithImages(CarModel car, List<String> imageUrls) {
        if (car == null || imageUrls == null || imageUrls.isEmpty()) {
            return false;
        }
        return carDAO.addCarWithImages(car, imageUrls);
    }

    public boolean updateCarWithNewImages(CarModel car, List<String> newImageUrls) {
        if (car == null) {
            return false;
        }
        return carDAO.updateCarWithNewImages(car, newImageUrls);
    }

    public List<String> getCarImages(int carId) {
        return carDAO.getCarImagesByCarId(carId);
    }

    public String getPrimaryImage(int carId) {
        return carDAO.getPrimaryImageByCarId(carId);
    }
}