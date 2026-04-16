/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import DALs.CarDAO;
import DALs.WishlistDAO;
import models.WishlistModel;
import java.util.List;
import models.CarModel;

/**
 *
 * @author Admin
 */
public class WishlistService {

    private WishlistDAO wishlistDao = new WishlistDAO();
    private CarDAO carDAO = new CarDAO();

    public List<WishlistModel> getWishlist(int customerId) {
        return wishlistDao.findByCustomerId(customerId);
    }

    public String addToWishlist(int customerId, int carId) {

        // 1. Kiểm tra xe tồn tại
        CarModel car = carDAO.findById(carId);
        if (car == null) {
            return "Car not found!";
        }

        // 2. Kiểm tra đã tồn tại trong wishlist chưa
        boolean exists = wishlistDao.exists(customerId, carId);
        if (exists) {
            return "Already in wishlist!";
        }

        // 3. Thêm mới
      
        boolean success = wishlistDao.create(customerId,carId);

        if (success) {
            return "Added to wishlist successfully!";
        }

        return "Something went wrong!";
    }
    
    public String removeFromWishlist(int customerId, int carId) {

    // 1. Kiểm tra tồn tại
    if (!wishlistDao.exists(customerId, carId)) {
        return "Item not found in wishlist!";
    }

    // 2. Xóa
    boolean success = wishlistDao.delete(customerId, carId);

    if (success) {
        return "Removed successfully!";
    }

    return "Remove failed!";
}
}
