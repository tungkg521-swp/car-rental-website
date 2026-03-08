/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DALs;

import models.ContractModel;
import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class ContractDAO extends DBContext{

    public boolean createContract(ContractModel contract) {
        String sql = "INSERT INTO rental_contract "
                + "(booking_id, customer_id, staff_id, car_id, contract_start_date, contract_end_date, "
                + "contract_status, daily_price, deposit_amount, total_amount, signed_at, created_at, note) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, contract.getBookingId());
            ps.setInt(2, contract.getCustomerId());
            ps.setInt(3, contract.getStaffId());
            ps.setInt(4, contract.getCarId());
            ps.setDate(5, contract.getContractStartDate());
            ps.setDate(6, contract.getContractEndDate());
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

        String sql = "SELECT rc.contract_id, rc.booking_id, rc.contract_start_date, rc.contract_end_date, "
                + "rc.contract_status, rc.total_amount, "
                + "c.full_name AS customer_name, "
                + "car.model_name AS car_name "
                + "FROM rental_contract rc "
                + "JOIN customer c ON rc.customer_id = c.customer_id "
                + "JOIN cars car ON rc.car_id = car.car_id "
                + "ORDER BY rc.contract_id DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ContractModel contract = new ContractModel();
                contract.setContractId(rs.getInt("contract_id"));
                contract.setBookingId(rs.getInt("booking_id"));
                contract.setContractStartDate(rs.getDate("contract_start_date"));
                contract.setContractEndDate(rs.getDate("contract_end_date"));
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
                    contract.setContractStartDate(rs.getDate("contract_start_date"));
                    contract.setContractEndDate(rs.getDate("contract_end_date"));
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
}