/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DALs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Utils.DBContext;
import java.sql.Timestamp;
import models.ContractModel;

/**
 *
 * @author ADMIN
 */
public class ContractDAO extends DBContext {

    public boolean createContract(ContractModel contract) {
        String sql = "INSERT INTO rental_contract "
                + "(booking_id, customer_id, staff_id, car_id, contract_start_time, contract_end_time, "
                + "contract_status, daily_price, deposit_amount, total_amount, signed_at, created_at, note) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, contract.getBookingId());
            ps.setInt(2, contract.getCustomerId());
            ps.setInt(3, contract.getStaffId());
            ps.setInt(4, contract.getCarId());
            ps.setTimestamp(5, contract.getContractStartTime());
            ps.setTimestamp(6, contract.getContractEndTime());
            ps.setString(7, contract.getContractStatus());
            ps.setDouble(8, contract.getDailyPrice());
            ps.setDouble(9, contract.getDepositAmount());
            ps.setDouble(10, contract.getTotalAmount());
            ps.setTimestamp(11, contract.getSignedAt());
            ps.setString(12, contract.getNote());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ContractModel> findAllContracts() {
        List<ContractModel> list = new ArrayList<>();

        String sql = "SELECT rc.contract_id, rc.booking_id, rc.contract_start_time, rc.contract_end_time, "
                + "rc.contract_status, rc.total_amount, "
                + "c.full_name AS customer_name, "
                + "car.model_name AS car_name "
                + "FROM rental_contract rc "
                + "JOIN customer c ON rc.customer_id = c.customer_id "
                + "JOIN cars car ON rc.car_id = car.car_id "
                + "ORDER BY rc.contract_id DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ContractModel contract = new ContractModel();
                contract.setContractId(rs.getInt("contract_id"));
                contract.setBookingId(rs.getInt("booking_id"));
                contract.setContractStartTime(rs.getTimestamp("contract_start_time"));
                contract.setContractEndTime(rs.getTimestamp("contract_end_time"));
                contract.setContractStatus(rs.getString("contract_status"));
                contract.setTotalAmount(rs.getDouble("total_amount"));
                contract.setCustomerName(rs.getString("customer_name"));
                contract.setCarName(rs.getString("car_name"));

                list.add(contract);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public ContractModel getContractById(int contractId) {
        String sql = "SELECT rc.*, "
                + "c.full_name AS customer_name, c.email AS customer_email, c.phone AS customer_phone, "
                + "car.model_name AS car_name "
                + "FROM rental_contract rc "
                + "JOIN customer c ON rc.customer_id = c.customer_id "
                + "JOIN cars car ON rc.car_id = car.car_id "
                + "WHERE rc.contract_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, contractId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ContractModel contract = new ContractModel();

                    contract.setContractId(rs.getInt("contract_id"));
                    contract.setBookingId(rs.getInt("booking_id"));
                    contract.setCustomerId(rs.getInt("customer_id"));
                    contract.setStaffId(rs.getInt("staff_id"));
                    contract.setCarId(rs.getInt("car_id"));
                    contract.setContractStartTime(rs.getTimestamp("contract_start_time"));
                    contract.setContractEndTime(rs.getTimestamp("contract_end_time"));
                    contract.setContractStatus(rs.getString("contract_status"));
                    contract.setDailyPrice(rs.getDouble("daily_price"));
                    contract.setDepositAmount(rs.getDouble("deposit_amount"));
                    contract.setTotalAmount(rs.getDouble("total_amount"));
                    contract.setSignedAt(rs.getTimestamp("signed_at"));
                    contract.setCreatedAt(rs.getTimestamp("created_at"));
                    contract.setNote(rs.getString("note"));

                    contract.setCustomerName(rs.getString("customer_name"));
                    contract.setCustomerEmail(rs.getString("customer_email"));
                    contract.setCustomerPhone(rs.getString("customer_phone"));
                    contract.setCarName(rs.getString("car_name"));

                    contract.setHandoverCheckId((Integer) rs.getObject("handover_check_id"));
                    contract.setCustomerConfirmed((Boolean) rs.getObject("customer_confirmed"));
                    contract.setCustomerConfirmNote(rs.getString("customer_confirm_note"));
                    contract.setCustomerConfirmTime(rs.getTimestamp("customer_confirm_time"));
                    contract.setNoShowNote(rs.getString("no_show_note"));

                    contract.setAllowedKm((Integer) rs.getObject("allowed_km"));
                    contract.setActualKm((Integer) rs.getObject("actual_km"));
                    contract.setExtraKm((Integer) rs.getObject("extra_km"));

                    Object extraKmFeeObj = rs.getObject("extra_km_fee");
                    if (extraKmFeeObj != null) {
                        contract.setExtraKmFee(rs.getDouble("extra_km_fee"));
                    }
                    contract.setActualReturnTime(rs.getTimestamp("actual_return_time"));

                    return contract;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateContractStatus(int contractId, String status) {
        String sql = "UPDATE rental_contract SET contract_status = ? WHERE contract_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, contractId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean existsByBookingId(int bookingId) {
        String sql = "SELECT 1 FROM rental_contract WHERE booking_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public int getCarIdByContractId(int contractId) {
        String sql = "SELECT car_id FROM rental_contract WHERE contract_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("car_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public boolean updateCarId(int contractId, int newCarId) {
        String sql = "UPDATE rental_contract SET car_id = ? WHERE contract_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, newCarId);
            ps.setInt(2, contractId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ContractModel getContractByBookingId(int bookingId) {
        String sql = "SELECT * FROM rental_contract WHERE booking_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ContractModel contract = new ContractModel();
                    contract.setContractId(rs.getInt("contract_id"));
                    contract.setBookingId(rs.getInt("booking_id"));
                    contract.setCustomerId(rs.getInt("customer_id"));
                    contract.setStaffId(rs.getInt("staff_id"));
                    contract.setCarId(rs.getInt("car_id"));
                    contract.setContractStartTime(rs.getTimestamp("contract_start_time"));
                    contract.setContractEndTime(rs.getTimestamp("contract_end_time"));
                    contract.setContractStatus(rs.getString("contract_status"));
                    contract.setDailyPrice(rs.getDouble("daily_price"));
                    contract.setDepositAmount(rs.getDouble("deposit_amount"));
                    contract.setTotalAmount(rs.getDouble("total_amount"));
                    contract.setSignedAt(rs.getTimestamp("signed_at"));
                    contract.setCreatedAt(rs.getTimestamp("created_at"));
                    contract.setNote(rs.getString("note"));

                    contract.setHandoverCheckId((Integer) rs.getObject("handover_check_id"));
                    contract.setCustomerConfirmed((Boolean) rs.getObject("customer_confirmed"));
                    contract.setCustomerConfirmNote(rs.getString("customer_confirm_note"));
                    contract.setCustomerConfirmTime(rs.getTimestamp("customer_confirm_time"));
                    contract.setNoShowNote(rs.getString("no_show_note"));

                    contract.setAllowedKm((Integer) rs.getObject("allowed_km"));
                    contract.setActualKm((Integer) rs.getObject("actual_km"));
                    contract.setExtraKm((Integer) rs.getObject("extra_km"));

                    Object extraKmFeeObj = rs.getObject("extra_km_fee");
                    if (extraKmFeeObj != null) {
                        contract.setExtraKmFee(rs.getDouble("extra_km_fee"));
                    }
                    contract.setActualReturnTime(rs.getTimestamp("actual_return_time"));

                    return contract;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateContractForCustomerConfirm(int contractId, String status, int handoverCheckId) {
        String sql = "UPDATE rental_contract "
                + "SET contract_status = ?, handover_check_id = ?, customer_confirmed = NULL, "
                + "customer_confirm_note = NULL, customer_confirm_time = NULL "
                + "WHERE contract_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, handoverCheckId);
            ps.setInt(3, contractId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean confirmCustomerHandover(int contractId, String note) {
        String sql = "UPDATE rental_contract "
                + "SET customer_confirmed = ?, "
                + "    customer_confirm_note = ?, "
                + "    customer_confirm_time = CURRENT_TIMESTAMP "
                + "WHERE contract_id = ? "
                + "  AND contract_status = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, true);
            ps.setString(2, note);
            ps.setInt(3, contractId);
            ps.setString(4, "WAITING_CUSTOMER_CONFIRM");

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean rejectCustomerHandover(int contractId, String note) {
        String sql = "UPDATE rental_contract "
                + "SET customer_confirmed = ?, "
                + "    customer_confirm_note = ?, "
                + "    customer_confirm_time = CURRENT_TIMESTAMP, "
                + "    contract_status = ? "
                + "WHERE contract_id = ? "
                + "  AND contract_status = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, false);
            ps.setString(2, note);
            ps.setString(3, "CANCELLED");
            ps.setInt(4, contractId);
            ps.setString(5, "WAITING_CUSTOMER_CONFIRM");

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateMileageSummary(int contractId, int allowedKm, int actualKm, int extraKm, double extraKmFee) {
        String sql = "UPDATE rental_contract "
                + "SET allowed_km = ?, actual_km = ?, extra_km = ?, extra_km_fee = ? "
                + "WHERE contract_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, allowedKm);
            ps.setInt(2, actualKm);
            ps.setInt(3, extraKm);
            ps.setDouble(4, extraKmFee);
            ps.setInt(5, contractId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateActualReturnTime(int contractId, Timestamp actualReturnTime) {
        String sql = """
        UPDATE rental_contract
        SET actual_return_time = ?
        WHERE contract_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, actualReturnTime);
            ps.setInt(2, contractId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateFinalSettlement(int contractId,
            double extraFeeTotal,
            double finalAmount) {

        String sql = """
        UPDATE rental_contract
        SET extra_fee_total = ?,
            final_amount = ?
        WHERE contract_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, extraFeeTotal);
            ps.setDouble(2, finalAmount);
            ps.setInt(3, contractId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
