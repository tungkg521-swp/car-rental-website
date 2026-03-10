package DALs;

import Utils.DBContext;
import java.math.BigDecimal;
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

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

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

    public boolean updateStatus(int carId, String status) {

    String sql = "UPDATE cars SET status = ? WHERE car_id = ?";

    try (PreparedStatement ps = connection.prepareStatement(sql)) {

        ps.setString(1, status);
        ps.setInt(2, carId);

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}

public List<CarModel> searchCars(String keyword) {
    List<CarModel> list = new ArrayList<>();

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
    return list;
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

        List<CarModel> list = new ArrayList<>();

        System.out.println("===== FILTER DEBUG =====");
        System.out.println("Keyword: " + keyword);
        System.out.println("Available only: " + availableOnly);
        System.out.println("Brands: " + (brands != null ? String.join(", ", brands) : "none"));
        System.out.println("Types: " + (types != null ? String.join(", ", types) : "none"));
        System.out.println("Fuels: " + (fuels != null ? String.join(", ", fuels) : "none"));
        System.out.println("Seats: " + seats);
        System.out.println("Transmission: " + transmission);
        System.out.println("Year range: " + yearRange);
        System.out.println("Max price: " + maxPrice);
        System.out.println("Connection = " + connection);

        String sql = """
        SELECT 
            c.car_id, c.model_name, c.model_year, c.price_per_day, c.seat_count,
            c.fuel_type, c.transmission, b.brand_name, ct.type_name,
            i.image_url, c.image_folder, c.description, c.status
        FROM cars c
        JOIN brand b ON c.brand_id = b.brand_id
        JOIN cars_type ct ON c.type_id = ct.type_id
        LEFT JOIN cars_image i ON c.car_id = i.car_id AND i.is_primary = 1
        WHERE 1=1
    """;

        // Chuẩn bị các điều kiện động
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " AND c.model_name LIKE ?";
}
        if (availableOnly) {
            sql += " AND c.status = 'AVAILABLE'";
        }
        if (brands != null && brands.length > 0) {
            sql += " AND b.brand_name IN (";
            for (int i = 0; i < brands.length; i++) {
                sql += "?";
                if (i < brands.length - 1) {
                    sql += ", ";
                }
            }
            sql += ")";
        }
        if (types != null && types.length > 0) {
            sql += " AND ct.type_name IN (";
            for (int i = 0; i < types.length; i++) {
                sql += "?";
                if (i < types.length - 1) {
                    sql += ", ";
                }
            }
            sql += ")";
        }
        if (fuels != null && fuels.length > 0) {
            sql += " AND c.fuel_type IN (";
            for (int i = 0; i < fuels.length; i++) {
                sql += "?";
                if (i < fuels.length - 1) {
                    sql += ", ";
                }
            }
            sql += ")";
        }
        if (seats != null) {
            if (seats == 7) {
                sql += " AND c.seat_count >= ?";
            } else {
                sql += " AND c.seat_count = ?";
            }
        }
        if (transmission != null && !"Any".equals(transmission)) {
            sql += " AND c.transmission = ?";
        }
        if (yearRange != null && !"Any".equals(yearRange)) {
            if ("2024+".equals(yearRange)) {
                sql += " AND c.model_year >= 2024";
            } else if ("2020-2023".equals(yearRange)) {
                sql += " AND c.model_year BETWEEN 2020 AND 2023";
            } else if ("Before2020".equals(yearRange)) {
                sql += " AND c.model_year < 2020";
            }
        }
        if (maxPrice != null) {
            sql += " AND c.price_per_day <= ?";
        }

        sql += " ORDER BY c.price_per_day ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword.trim() + "%");
            }
            if (brands != null && brands.length > 0) {
                for (String brand : brands) {
                    ps.setString(paramIndex++, brand);
                }
            }
            if (types != null && types.length > 0) {
                for (String type : types) {
                    ps.setString(paramIndex++, type);
                }
            }
            if (fuels != null && fuels.length > 0) {
                for (String fuel : fuels) {
                    ps.setString(paramIndex++, fuel);
                }
            }
            if (seats != null) {
                ps.setInt(paramIndex++, seats == 7 ? 7 : seats);
            }
            if (transmission != null && !"Any".equals(transmission)) {
                ps.setString(paramIndex++, transmission);
            }
            if (maxPrice != null) {
                ps.setBigDecimal(paramIndex++, maxPrice);
            }

            System.out.println("Final SQL: " + sql);
            System.out.println("Param count: " + (paramIndex - 1));

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
                        rs.getString("image_url"),
                        rs.getString("image_folder"),
                        rs.getString("description"),
                        rs.getString("status")
                ));
            }
            System.out.println("Filter result size = " + list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("========================\n");
        return list;
    }
}
