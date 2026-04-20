package DALs;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Utils.DBContext;
import static Utils.DBContext.getConnection;
import java.sql.Timestamp;
import models.CarModel;

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
            c.status,
            c.plate_number
        FROM cars c
        LEFT JOIN brand b 
            ON c.brand_id = b.brand_id
        LEFT JOIN cars_type t 
            ON c.type_id = t.type_id
        LEFT JOIN cars_image i
            ON c.car_id = i.car_id
           AND i.is_primary = 1
        WHERE c.status = 'AVAILABLE'
        ORDER BY c.car_id DESC
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
                        rs.getString("status"),
                        rs.getString("plate_number")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

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

            i.image_url,

            c.image_folder,
            c.description,
            c.status,
            c.plate_number
               
        FROM cars c

        LEFT JOIN brand b 
            ON c.brand_id = b.brand_id
        LEFT JOIN cars_type t 
            ON c.type_id = t.type_id
        LEFT JOIN cars_image i
            ON c.car_id = i.car_id
           AND i.is_primary = 1
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
                        rs.getString("image_url"),
                        rs.getString("image_folder"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getString("plate_number")
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
            c.status,
            c.plate_number
        FROM cars c
        LEFT JOIN brand b 
            ON c.brand_id = b.brand_id
        LEFT JOIN cars_type t 
            ON c.type_id = t.type_id
        LEFT JOIN cars_image i
            ON c.car_id = i.car_id
           AND i.is_primary = 1
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
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getString("plate_number")
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
            c.status,
            c.plate_number
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
                        rs.getString("image_url"), // SỬA: Lấy từ rs thay null
                        rs.getString("image_folder"),
                        rs.getString("description"), // SỬA: Lấy từ rs thay null (nếu không cần, bỏ select và dùng constructor khác)

                        rs.getString("status"),
                        rs.getString("plate_number")
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

        StringBuilder sql = new StringBuilder("""
        SELECT 
            c.car_id, c.model_name, c.model_year, c.price_per_day, c.seat_count,
            c.fuel_type, c.transmission, b.brand_name, ct.type_name,
            i.image_url, c.image_folder, c.description, c.status,c.plate_number
        FROM cars c
        JOIN brand b ON c.brand_id = b.brand_id
        JOIN cars_type ct ON c.type_id = ct.type_id
        LEFT JOIN cars_image i ON c.car_id = i.car_id AND i.is_primary = 1
        WHERE 1=1
    """);

        List<Object> params = new ArrayList<>();

        // Thêm điều kiện động
        if (keyword != null && !keyword.trim().isEmpty()) {

            sql.append(" AND c.model_name LIKE ?");
            params.add("%" + keyword.trim() + "%");
        }

        if (availableOnly) {
            sql.append(" AND c.status = 'AVAILABLE'");
        }

        if (brands != null && brands.length > 0) {
            sql.append(" AND b.brand_name IN (");
            for (int i = 0; i < brands.length; i++) {
                sql.append(i == 0 ? "?" : ", ?");
                params.add(brands[i]);
            }
            sql.append(")");
        }

        if (types != null && types.length > 0) {
            sql.append(" AND ct.type_name IN (");
            for (int i = 0; i < types.length; i++) {
                sql.append(i == 0 ? "?" : ", ?");
                params.add(types[i]);
            }
            sql.append(")");
        }

        if (fuels != null && fuels.length > 0) {
            sql.append(" AND c.fuel_type IN (");
            for (int i = 0; i < fuels.length; i++) {
                sql.append(i == 0 ? "?" : ", ?");
                params.add(fuels[i]);
            }
            sql.append(")");
        }

        if (seats != null) {
            if (seats == 7) {
                sql.append(" AND c.seat_count >= ?");
                params.add(7);
            } else {
                sql.append(" AND c.seat_count = ?");
                params.add(seats);
            }
        }

        if (transmission != null && !"Any".equals(transmission) && !transmission.isEmpty()) {
            sql.append(" AND c.transmission = ?");
            params.add(transmission);
        }

        if (yearRange != null && !"Any".equals(yearRange) && !yearRange.isEmpty()) {
            if ("2024+".equals(yearRange)) {
                sql.append(" AND c.model_year >= 2024");
            } else if ("2020-2023".equals(yearRange)) {
                sql.append(" AND c.model_year BETWEEN 2020 AND 2023");
            } else if ("Before2020".equals(yearRange)) {
                sql.append(" AND c.model_year < 2020");
            }
        }

        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
            sql.append(" AND c.price_per_day <= ?");
            params.add(maxPrice);
        }

        sql.append(" ORDER BY c.price_per_day ASC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

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
                        rs.getString("status"),
                        rs.getString("plate_number")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean addCar(CarModel car) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            int brandId = getBrandId(conn, car.getBrandName());

            int typeId = getTypeId(conn, car.getTypeName());

            String plateNumber = "TEMP-" + System.currentTimeMillis();

            String sql = "INSERT INTO cars (brand_id, type_id, plate_number, model_name, model_year, "
                    + "price_per_day, status, created_at, seat_count, fuel_type, transmission, "
                    + "description, image_folder) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE(), ?, ?, ?, ?, ?)";

            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, brandId);
            ps.setInt(2, typeId);
            ps.setString(3, plateNumber);
            ps.setString(4, car.getModelName());
            ps.setInt(5, car.getModelYear());
            ps.setBigDecimal(6, car.getPricePerDay());
            ps.setString(7, car.getStatus() != null ? car.getStatus() : "AVAILABLE");
            ps.setInt(8, car.getSeatCount());
            ps.setString(9, car.getFuelType());
            ps.setString(10, car.getTransmission());
            ps.setString(11, car.getDescription() != null ? car.getDescription() : "");
            ps.setString(12, car.getImageFolder());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            rs = ps.getGeneratedKeys();
            int carId = 0;
            if (rs.next()) {
                carId = rs.getInt(1);
            }

            if (carId > 0 && car.getImageUrl() != null) {
                String imageSql = "INSERT INTO cars_image (car_id, image_url, is_primary, created_at) VALUES (?, ?, ?, GETDATE())";

                try (PreparedStatement imagePs = conn.prepareStatement(imageSql)) {

                    String imagePath = "assets/images/cars/" + car.getImageFolder() + "/" + car.getImageUrl();
                    System.out.println("Saving image: " + imagePath);

                    imagePs.setInt(1, carId);
                    imagePs.setString(2, imagePath);
                    imagePs.setBoolean(3, true);
                    imagePs.executeUpdate();
                }
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private int getBrandId(Connection conn, String brandName) throws SQLException {
        System.out.println("   getBrandId - brandName: " + brandName);

        String selectSql = "SELECT brand_id FROM brand WHERE brand_name = ?";
        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setString(1, brandName.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("brand_id");

                return id;
            }
        }

        String insertSql = "INSERT INTO brand (brand_name) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, brandName.trim());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);

                return id;
            }
        }

        throw new SQLException("Could not create brand: " + brandName);
    }

    private int getTypeId(Connection conn, String typeName) throws SQLException {

        String selectSql = "SELECT type_id FROM cars_type WHERE type_name = ?";
        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setString(1, typeName.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("type_id");

                return id;
            }
        }

        String insertSql = "INSERT INTO cars_type (type_name) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, typeName.trim());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);

                return id;
            }
        }

        throw new SQLException("Could not create type: " + typeName);
    }

    public boolean updateCar(CarModel car) {
        String sql = """
            UPDATE cars
            SET model_name = ?,
                model_year = ?,
                price_per_day = ?,
                seat_count = ?,
                fuel_type = ?,
                transmission = ?,
                brand_id = (SELECT brand_id FROM brand WHERE brand_name = ?),
                type_id = (SELECT type_id FROM cars_type WHERE type_name = ?),
                description = ?,
                status = ?
            WHERE car_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, car.getModelName());
            ps.setInt(2, car.getModelYear());
            ps.setBigDecimal(3, car.getPricePerDay());
            ps.setInt(4, car.getSeatCount());
            ps.setString(5, car.getFuelType());
            ps.setString(6, car.getTransmission());
            ps.setString(7, car.getBrandName());
            ps.setString(8, car.getTypeName());
            ps.setString(9, car.getDescription());
            ps.setString(10, car.getStatus());
            ps.setInt(11, car.getCarId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteCar(int carId) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            String deleteImagesSql = "DELETE FROM cars_image WHERE car_id = ?";
            ps = conn.prepareStatement(deleteImagesSql);
            ps.setInt(1, carId);
            ps.executeUpdate();
            ps.close();

            String deleteCarSql = "DELETE FROM cars WHERE car_id = ?";
            ps = conn.prepareStatement(deleteCarSql);
            ps.setInt(1, carId);

            int result = ps.executeUpdate();
            conn.commit();
            return result > 0;

        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean addCarWithImages(CarModel car, List<String> imageUrls) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            int brandId = getBrandId(conn, car.getBrandName());
            int typeId = getTypeId(conn, car.getTypeName());

            String plateNumber = "TEMP-" + System.currentTimeMillis();

            String sql = """
        INSERT INTO cars (
            brand_id, type_id, plate_number, model_name, model_year,
            price_per_day, status, created_at, seat_count, fuel_type,
            transmission, description, image_folder
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE(), ?, ?, ?, ?, ?)
    """;

            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, brandId);
            ps.setInt(2, typeId);
            ps.setString(3, car.getPlateNumber());
            ps.setString(4, car.getModelName());
            ps.setInt(5, car.getModelYear());
            ps.setBigDecimal(6, car.getPricePerDay());
            ps.setString(7, car.getStatus() != null ? car.getStatus() : "AVAILABLE");
            ps.setInt(8, car.getSeatCount());
            ps.setString(9, car.getFuelType());
            ps.setString(10, car.getTransmission());
            ps.setString(11, car.getDescription() != null ? car.getDescription() : "");
            ps.setString(12, car.getImageFolder());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();
                return false;
            }

            rs = ps.getGeneratedKeys();
            int carId = 0;
            if (rs.next()) {
                carId = rs.getInt(1);
            }

            if (carId <= 0) {
                conn.rollback();
                return false;
            }

            if (imageUrls != null && !imageUrls.isEmpty()) {
                String imageSql = """
            INSERT INTO cars_image (car_id, image_url, is_primary, created_at)
            VALUES (?, ?, ?, GETDATE())
        """;

                try (PreparedStatement imagePs = conn.prepareStatement(imageSql)) {
                    for (int i = 0; i < imageUrls.size(); i++) {
                        imagePs.setInt(1, carId);

                        // QUAN TRỌNG: Đảm bảo đường dẫn ảnh đúng format
                        String imagePath = imageUrls.get(i);

                        // Nếu imageUrls đã là đường dẫn đầy đủ từ controller thì giữ nguyên
                        // Nếu chỉ là tên file, cần tạo đường dẫn đầy đủ
                        if (!imagePath.startsWith("assets/")) {
                            imagePath = "assets/images/cars/" + car.getImageFolder() + "/" + imagePath;
                        }

                        imagePs.setString(2, imagePath);
                        imagePs.setBoolean(3, i == 0);
                        imagePs.addBatch();
                    }
                    imagePs.executeBatch();
                }
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean updateCarWithNewImages(CarModel car, List<String> newImageUrls) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            int brandId = getBrandId(conn, car.getBrandName());
            int typeId = getTypeId(conn, car.getTypeName());

            String updateSql = """
            UPDATE cars
            SET model_name = ?,
                model_year = ?,
                price_per_day = ?,
                seat_count = ?,
                fuel_type = ?,
                transmission = ?,
                brand_id = ?,
                type_id = ?,
                description = ?,
                status = ?,
                plate_number = ?
            WHERE car_id = ?
        """;

            ps = conn.prepareStatement(updateSql);
            ps.setString(1, car.getModelName());
            ps.setInt(2, car.getModelYear());
            ps.setBigDecimal(3, car.getPricePerDay());
            ps.setInt(4, car.getSeatCount());
            ps.setString(5, car.getFuelType());
            ps.setString(6, car.getTransmission());
            ps.setInt(7, brandId);
            ps.setInt(8, typeId);
            ps.setString(9, car.getDescription());
            ps.setString(10, car.getStatus());
            ps.setString(11, car.getPlateNumber());
            ps.setInt(12, car.getCarId());

            int updatedRows = ps.executeUpdate();
            ps.close();

            if (updatedRows <= 0) {
                conn.rollback();
                return false;
            }

            if (newImageUrls != null && !newImageUrls.isEmpty()) {
                boolean hasPrimary = hasPrimaryImage(conn, car.getCarId());

                String insertImageSql = """
                INSERT INTO cars_image (car_id, image_url, is_primary, created_at)
                VALUES (?, ?, ?, GETDATE())
            """;

                try (PreparedStatement imagePs = conn.prepareStatement(insertImageSql)) {
                    for (int i = 0; i < newImageUrls.size(); i++) {
                        imagePs.setInt(1, car.getCarId());
                        imagePs.setString(2, newImageUrls.get(i));

                        // Nếu xe chưa có ảnh primary nào thì ảnh đầu tiên upload mới sẽ là primary
                        boolean isPrimary = !hasPrimary && i == 0;
                        imagePs.setBoolean(3, isPrimary);

                        imagePs.addBatch();
                    }
                    imagePs.executeBatch();
                }
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getCarImagesByCarId(int carId) {
        List<String> images = new ArrayList<>();

        String sql = """
        SELECT image_url
        FROM cars_image
        WHERE car_id = ?
        ORDER BY is_primary DESC, image_id ASC
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, carId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                images.add(rs.getString("image_url"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return images;
    }

    public String getPrimaryImageByCarId(int carId) {
        String sql = """
        SELECT TOP 1 image_url
        FROM cars_image
        WHERE car_id = ?
        ORDER BY is_primary DESC, image_id ASC
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, carId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("image_url");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean hasPrimaryImage(Connection conn, int carId) throws SQLException {
        String sql = """
        SELECT COUNT(*) 
        FROM cars_image
        WHERE car_id = ? AND is_primary = 1
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }

        return false;
    }

    public boolean updateCarStatus(int carId, String status) {
        String sql = "UPDATE cars SET status = ? WHERE car_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, carId);
            int rows = ps.executeUpdate();
            System.out.println("Updated car " + carId + " status to " + status + " → " + rows + " row(s)");
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<CarModel> findAvailableCarsByDateRange(Timestamp startTime, Timestamp endTime) {
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
            c.status,
            c.plate_number
        FROM cars c
        LEFT JOIN brand b ON c.brand_id = b.brand_id
        LEFT JOIN cars_type t ON c.type_id = t.type_id
        LEFT JOIN cars_image i ON c.car_id = i.car_id AND i.is_primary = 1
        WHERE c.status <> 'MAINTENANCE'
          AND c.car_id NOT IN (
              SELECT bk.car_id
              FROM booking bk
              WHERE bk.status IN ('AWAITING_PAYMENT', 'PENDING_APPROVAL', 'CONFIRMED', 'ACTIVE')
                AND bk.start_time < ?
                AND bk.end_time > ?
          )
        ORDER BY c.car_id DESC
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, endTime);
            ps.setTimestamp(2, startTime);

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
                        null,
                        rs.getString("status"),
                        rs.getString("plate_number")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean isCarBookedInRange(int carId, Timestamp startTime, Timestamp endTime) {
        String sql = """
        SELECT 1
        FROM booking
        WHERE car_id = ?
          AND status IN ('AWAITING_PAYMENT', 'PENDING_APPROVAL', 'CONFIRMED', 'ACTIVE')
          AND start_time < ?
          AND end_time > ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, carId);
            ps.setTimestamp(2, endTime);
            ps.setTimestamp(3, startTime);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<CarModel> getAvailableReplacementCars(
            int oldCarId,
            String typeName,
            BigDecimal pricePerDay,
            Timestamp startTime,
            Timestamp endTime
    ) {
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
            c.plate_number,
            c.image_folder,
            c.description,
            c.status,
            c.plate_number
        FROM cars c
        JOIN brand b ON c.brand_id = b.brand_id
        JOIN cars_type t ON c.type_id = t.type_id
        WHERE c.car_id <> ?
          AND t.type_name = ?
          AND c.price_per_day = ?
          AND c.status = 'AVAILABLE'
          AND NOT EXISTS (
              SELECT 1
              FROM booking bk
              WHERE bk.car_id = c.car_id
                AND bk.status IN ('AWAITING_PAYMENT', 'PENDING_APPROVAL', 'CONFIRMED', 'ACTIVE')
                AND bk.start_time < ?
                AND bk.end_time > ?
          )
          AND NOT EXISTS (
              SELECT 1
              FROM maintenance_record m
              WHERE m.car_id = c.car_id
                AND m.start_time < ?
                AND ISNULL(m.end_time, '9999-12-31') >= ?
                AND m.status IN ('OPEN', 'IN_PROGRESS')
          )
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, oldCarId);
            ps.setString(2, typeName);
            ps.setBigDecimal(3, pricePerDay);
            ps.setTimestamp(4, endTime);
            ps.setTimestamp(5, startTime);
            ps.setTimestamp(6, endTime);
            ps.setTimestamp(7, startTime);

            try (ResultSet rs = ps.executeQuery()) {
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
                            null,
                            rs.getString("image_folder"),
                            rs.getString("description"),
                            rs.getString("status"),
                            rs.getString("plate_number")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean existsPlateNumber(String plateNumber) {
        String sql = "SELECT 1 FROM cars WHERE plate_number = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, plateNumber);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean existsPlateNumberExceptId(String plateNumber, int carId) {
        String sql = "SELECT 1 FROM cars WHERE plate_number = ? AND car_id <> ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, plateNumber);
            ps.setInt(2, carId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
