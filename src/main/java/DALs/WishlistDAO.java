/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DALs;


import Utils.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import Models.WishlistModel;

/**
 *
 * @author Admin
 */
public class WishlistDAO extends DBContext{
    public List<WishlistModel> findByCustomerId(int customerId) {

    List<WishlistModel> list = new ArrayList<>();

    String sql = """
        SELECT w.wishlist_id,
               c.car_id,
               c.model_name,
               c.model_year,
               c.price_per_day,
               c.seat_count,
               c.fuel_type,
               c.transmission,
               b.brand_name,
               t.type_name,
               i.image_url,
               c.image_folder        
        FROM cars c
        LEFT JOIN wish_list w ON c.car_id = w.car_id
        LEFT JOIN brand b ON c.brand_id = b.brand_id
        LEFT JOIN cars_type t ON c.type_id = t.type_id
        LEFT JOIN cars_image i ON c.car_id = i.car_id
                               AND i.is_primary = 1
        WHERE w.customer_id = ?
    """;

    try (PreparedStatement ps = connection.prepareStatement(sql)) {

        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            WishlistModel w = new WishlistModel(
                    rs.getInt("wishlist_id"),               
                    rs.getInt("car_id"),
                    rs.getString("model_name"),
                    rs.getInt("model_year"),
                    rs.getBigDecimal("price_per_day"),
                    rs.getInt("seat_count"),
                    rs.getString("fuel_type"),
                    rs.getString("transmission"),
                    rs.getString("brand_name"),
                    rs.getString("type_name"),
                    rs.getString("image_url"),
                    rs.getString("image_folder")
            );

            list.add(w);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}
}
