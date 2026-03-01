package service;

import DALs.CarDAO;
import DALs.CarDAO;
import models.CarModel;
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

}
