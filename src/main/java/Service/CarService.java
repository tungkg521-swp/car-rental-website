package service;

import DALs.CarDAO;
import java.math.BigDecimal;
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
}
