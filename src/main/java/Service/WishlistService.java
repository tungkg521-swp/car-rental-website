/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import DALs.WishlistDAO;
import Models.WishlistModel;
import java.util.List;

/**
 *
 * @author Admin
 */
public class WishlistService {
     private WishlistDAO dao = new WishlistDAO();

    public List<WishlistModel> getWishlist(int customerId) {
        return dao.findByCustomerId(customerId);
    }
}

