/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DALs;

import models.CarTypeModel;
import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CarTypeDAO extends DBContext {

    public List<CarTypeModel> getAllTypes() {
        List<CarTypeModel> list = new ArrayList<>();

        String sql = """
                     SELECT type_id, type_name, status, created_at
                     FROM cars_type
                     ORDER BY created_at DESC, type_id DESC
                     """;

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CarTypeModel type = new CarTypeModel();
                type.setTypeId(rs.getInt(1));
                type.setTypeName(rs.getString(2));
                type.setStatus(rs.getString(3));
                type.setCreatedAt(rs.getDate(4));
                list.add(type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<CarTypeModel> searchTypes(String keyword) {
        List<CarTypeModel> list = new ArrayList<>();

        String sql = """
                     SELECT type_id, type_name, status, created_at
                     FROM cars_type
                     WHERE type_name LIKE ?
                     ORDER BY created_at DESC, type_id DESC
                     """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CarTypeModel type = new CarTypeModel();
                    type.setTypeId(rs.getInt(1));
                    type.setTypeName(rs.getString(2));
                    type.setStatus(rs.getString(3));
                    type.setCreatedAt(rs.getDate(4));
                    list.add(type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public CarTypeModel findById(int typeId) {
        String sql = """
                     SELECT type_id, type_name, status, created_at
                     FROM cars_type
                     WHERE type_id = ?
                     """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, typeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CarTypeModel type = new CarTypeModel();
                    type.setTypeId(rs.getInt(1));
                    type.setTypeName(rs.getString(2));
                    type.setStatus(rs.getString(3));
                    type.setCreatedAt(rs.getDate(4));
                    return type;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean existsTypeName(String typeName) {
        String sql = "SELECT 1 FROM cars_type WHERE LTRIM(RTRIM(type_name)) = LTRIM(RTRIM(?))";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, typeName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean existsTypeNameExceptId(String typeName, int typeId) {
        String sql = """
                     SELECT 1
                     FROM cars_type
                     WHERE LTRIM(RTRIM(type_name)) = LTRIM(RTRIM(?))
                       AND type_id <> ?
                     """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, typeName);
            ps.setInt(2, typeId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insert(String typeName, String status) {
        String sql = """
                     INSERT INTO cars_type (type_name, status, created_at)
                     VALUES (?, ?, SYSDATETIME())
                     """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, typeName);
            ps.setString(2, status);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(String typeName, String status, int typeId) {
        String sql = """
                     UPDATE cars_type
                     SET type_name = ?, status = ?
                     WHERE type_id = ?
                     """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, typeName);
            ps.setString(2, status);
            ps.setInt(3, typeId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // soft delete
    public boolean delete(int typeId) {
        String sql = "DELETE FROM cars_type WHERE type_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, typeId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public int countAllTypes() {
        String sql = "SELECT COUNT(*) FROM cars_type";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int countActiveTypes() {
        String sql = "SELECT COUNT(*) FROM cars_type WHERE status = 'ACTIVE'";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
