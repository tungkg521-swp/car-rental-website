package DALs;

import Utils.DBContext;
import models.CarModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarDAO extends DBContext {

    public CarDAO() {
        super();
    }

public List<CarModel> findAllAvailableCars() {
    List<CarModel> list = new ArrayList<>();

    String sql = """
        SELECT
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
            c.image_folder,
            c.status
        FROM cars c
        LEFT JOIN brand b 
            ON c.brand_id = b.brand_id
        LEFT JOIN cars_type t 
            ON c.type_id = t.type_id
        LEFT JOIN cars_image i
            ON c.car_id = i.car_id
           AND i.is_primary = 1
        WHERE c.status = 'AVAILABLE'
    """;

    try (PreparedStatement ps = connection.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            list.add(new CarModel(
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
                rs.getString("image_folder"),
                null,
                rs.getString("status")
            ));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    System.out.println("CAR LIST SIZE = " + list.size());
    System.out.println("Connection = " + connection);
    return list;
}

    
    public CarModel findById(int carId) {
    String sql = """
        SELECT
            c.car_id,
            c.model_name,
            c.model_year,
            c.price_per_day,
            c.seat_count,
            c.fuel_type,
            c.transmission,
            b.brand_name,
            t.type_name,
            c.image_folder,
            c.description,
            c.status
        FROM cars c
        JOIN brand b 
            ON c.brand_id = b.brand_id
        JOIN cars_type t 
            ON c.type_id = t.type_id
        WHERE c.car_id = ?
    """;

    try (PreparedStatement ps = connection.prepareStatement(sql)) {

        ps.setInt(1, carId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new CarModel(
                rs.getInt("car_id"),
                rs.getString("model_name"),
                rs.getInt("model_year"),
                rs.getBigDecimal("price_per_day"),
                rs.getInt("seat_count"),
                rs.getString("fuel_type"),
                rs.getString("transmission"),
                rs.getString("brand_name"),
                rs.getString("type_name"),
                null,
                rs.getString("image_folder"),
                rs.getString("description"),
                rs.getString("status")
            );
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}
    
    public List<CarModel> findAllCars() {

    List<CarModel> list = new ArrayList<>();

    String sql = """
        SELECT
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
            c.image_folder,
            c.description,
            c.status
        FROM cars c
        LEFT JOIN brand b 
            ON c.brand_id = b.brand_id
        LEFT JOIN cars_type t 
            ON c.type_id = t.type_id
        LEFT JOIN cars_image i
            ON c.car_id = i.car_id
           AND i.is_primary = 1
    """;

    try (PreparedStatement ps = connection.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            list.add(new CarModel(
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
                rs.getString("image_folder"),
                rs.getString("description"),
                rs.getString("status")
            ));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

public List<CarModel> searchCars(String keyword) {
    List<CarModel> list = new ArrayList<>();
    System.out.println("===== SEARCH DEBUG =====");
    System.out.println("Keyword = " + keyword);
    System.out.println("Connection = " + connection);
    String sql = """
        SELECT
            c.car_id,
            c.model_name,
            c.model_year,
            c.price_per_day,
            c.seat_count,
            c.fuel_type,
            c.transmission,
            b.brand_name,
            ct.type_name,
            i.image_url,  -- THÊM: Lấy image_url
            c.image_folder,
            c.description,  -- THÊM: Nếu cần description (optional, nhưng constructor có)
            c.status
        FROM cars c  -- SỬA: cars lowercase để nhất quán
        JOIN brand b ON c.brand_id = b.brand_id
        JOIN cars_type ct ON c.type_id = ct.type_id
        LEFT JOIN cars_image i ON c.car_id = i.car_id AND i.is_primary = 1  -- THÊM: Join image giống findAll
        WHERE c.model_name LIKE ? 
          AND c.status = 'AVAILABLE'  -- THÊM: Filter available
    """;
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setString(1, "%" + keyword + "%");
        System.out.println("SQL param = %" + keyword + "%");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new CarModel(
                    rs.getInt("car_id"),
                    rs.getString("model_name"),
                    rs.getInt("model_year"),
                    rs.getBigDecimal("price_per_day"),
                    rs.getInt("seat_count"),
                    rs.getString("fuel_type"),
                    rs.getString("transmission"),
                    rs.getString("brand_name"),
                    rs.getString("type_name"),
                    rs.getString("image_url"),  // SỬA: Lấy từ rs thay null
                    rs.getString("image_folder"),
                    rs.getString("description"),  // SỬA: Lấy từ rs thay null (nếu không cần, bỏ select và dùng constructor khác)
                    rs.getString("status")
            ));
        }
        System.out.println("Result size = " + list.size());
    } catch (Exception e) {
        e.printStackTrace();
    }
    System.out.println("========================");
    return list;
}
}
