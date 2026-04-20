package DALs;

import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import models.CarCheckModel;

public class CarCheckDAO extends DBContext {

    public boolean addCheck(CarCheckModel check) {
        String sql = "INSERT INTO car_check "
                + "(contract_id, car_id, checked_by, check_time, fuel_level, "
                + "exterior_note, interior_note, check_result, note) "
                + "VALUES (?, ?, ?, GETDATE(), ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, check.getContractId());
            ps.setInt(2, check.getCarId());
            ps.setInt(3, check.getCheckedBy());
            ps.setString(4, check.getFuelLevel());
            ps.setString(5, check.getExteriorNote());
            ps.setString(6, check.getInteriorNote());
            ps.setString(7, check.getCheckResult());
            ps.setString(8, check.getNote());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public CarCheckModel getLatestCheckByContractId(int contractId) {
        String sql = "SELECT TOP 1 * FROM car_check "
                + "WHERE contract_id = ? "
                + "ORDER BY check_time DESC, check_id DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, contractId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean hasLatestCheckOk(int contractId) {
        CarCheckModel latest = getLatestCheckByContractId(contractId);
        return latest != null && "OK".equalsIgnoreCase(latest.getCheckResult());
    }

    public List<CarCheckModel> getChecksByContractId(int contractId) {
        List<CarCheckModel> list = new ArrayList<>();
        String sql = "SELECT * FROM car_check "
                + "WHERE contract_id = ? "
                + "ORDER BY check_time DESC, check_id DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, contractId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private CarCheckModel mapResultSet(ResultSet rs) throws Exception {
        CarCheckModel check = new CarCheckModel();
        check.setCheckId(rs.getInt("check_id"));
        check.setContractId(rs.getInt("contract_id"));
        check.setCarId(rs.getInt("car_id"));
        check.setCheckedBy(rs.getInt("checked_by"));
        check.setCheckTime(rs.getTimestamp("check_time"));
        check.setFuelLevel(rs.getString("fuel_level"));
        check.setExteriorNote(rs.getString("exterior_note"));
        check.setInteriorNote(rs.getString("interior_note"));
        check.setCheckResult(rs.getString("check_result"));
        check.setNote(rs.getString("note"));
        return check;
    }

    public boolean insert(CarCheckModel check) {
        String sql = "INSERT INTO car_check (contract_id, car_id, checked_by, check_time, fuel_level, exterior_note, interior_note, check_result, note) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, check.getContractId());
            ps.setInt(2, check.getCarId());
            ps.setInt(3, check.getCheckedBy());
            ps.setTimestamp(4, check.getCheckTime());
            ps.setString(5, check.getFuelLevel());
            ps.setString(6, check.getExteriorNote());
            ps.setString(7, check.getInteriorNote());
            ps.setString(8, check.getCheckResult());
            ps.setString(9, check.getNote());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public CarCheckModel getCheckById(int checkId) {
    String sql = "SELECT * FROM car_check WHERE check_id = ?";

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setInt(1, checkId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                CarCheckModel check = new CarCheckModel();

                check.setCheckId(rs.getInt("check_id"));
                check.setContractId(rs.getInt("contract_id"));
                check.setCarId(rs.getInt("car_id"));
                check.setCheckedBy(rs.getInt("checked_by"));
                check.setCheckTime(rs.getTimestamp("check_time"));
                check.setFuelLevel(rs.getString("fuel_level"));
                check.setExteriorNote(rs.getString("exterior_note"));
                check.setInteriorNote(rs.getString("interior_note"));
                check.setCheckResult(rs.getString("check_result"));
                check.setNote(rs.getString("note"));

                return check;
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}
}
