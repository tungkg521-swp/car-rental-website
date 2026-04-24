/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DALs;

import models.BrandModel;
import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BrandDAO extends DBContext {

    public List<BrandModel> getAllBrands() {
        List<BrandModel> list = new ArrayList<>();

        String sql = """
                     SELECT brand_id, brand_name, status, created_at
                     FROM brand
                     ORDER BY created_at DESC, brand_id DESC
                     """;

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                BrandModel brand = new BrandModel();
                brand.setBrandId(rs.getInt(1));
                brand.setBrandName(rs.getString(2));
                brand.setStatus(rs.getString(3));
                brand.setCreatedAt(rs.getDate(4));
                list.add(brand);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<BrandModel> searchBrands(String keyword) {
        List<BrandModel> list = new ArrayList<>();

        String sql = """
                     SELECT brand_id, brand_name, status, created_at
                     FROM brand
                     WHERE brand_name LIKE ?
                     ORDER BY created_at DESC, brand_id DESC
                     """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BrandModel brand = new BrandModel();
                    brand.setBrandId(rs.getInt(1));
                    brand.setBrandName(rs.getString(2));
                    brand.setStatus(rs.getString(3));
                    brand.setCreatedAt(rs.getDate(4));
                    list.add(brand);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public BrandModel findById(int brandId) {
        String sql = """
                     SELECT brand_id, brand_name, status, created_at
                     FROM brand
                     WHERE brand_id = ?
                     """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, brandId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BrandModel brand = new BrandModel();
                    brand.setBrandId(rs.getInt(1));
                    brand.setBrandName(rs.getString(2));
                    brand.setStatus(rs.getString(3));
                    brand.setCreatedAt(rs.getDate(4));
                    return brand;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean existsBrandName(String brandName) {
        String sql = "SELECT 1 FROM brand WHERE LTRIM(RTRIM(brand_name)) = LTRIM(RTRIM(?))";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, brandName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean existsBrandNameExceptId(String brandName, int brandId) {
        String sql = """
                     SELECT 1
                     FROM brand
                     WHERE LTRIM(RTRIM(brand_name)) = LTRIM(RTRIM(?))
                       AND brand_id <> ?
                     """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, brandName);
            ps.setInt(2, brandId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insert(String brandName,String status) {
        String sql = """
                     INSERT INTO brand (brand_name, status, created_at)
                     VALUES (?, ?, SYSDATETIME())
                     """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, brandName);
            ps.setString(2, status);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(String brandName,String status, int brandId) {
        String sql = """
                     UPDATE brand
                     SET brand_name = ?, status = ?
                     WHERE brand_id = ?
                     """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, brandName);
            ps.setString(2, status);
            ps.setInt(3, brandId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public boolean delete(int brandId) {
        String sql = "DELETE brand WHERE brand_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, brandId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public int countAllBrands() {
        String sql = "SELECT COUNT(*) FROM brand";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int countActiveBrands() {
        String sql = "SELECT COUNT(*) FROM brand WHERE status = 'ACTIVE'";

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
