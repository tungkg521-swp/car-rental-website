/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import DALs.BookingDAO;
import DALs.CarDAO;
import DALs.ContractDAO;
import models.ContractModel;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class ContractService {

    private final ContractDAO contractDAO = new ContractDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final CarDAO carDAO = new CarDAO();

    public boolean createContract(ContractModel contract) {
        return contractDAO.createContract(contract);
    }

    public List<ContractModel> findAllContracts() {
        return contractDAO.findAllContracts();
    }

    public ContractModel getContractById(int contractId) {
        return contractDAO.getContractById(contractId);
    }

    public boolean updateContractStatus(int contractId, String status) {

        ContractModel contract = contractDAO.getContractById(contractId);

        if (contract == null) {
            return false;
        }

        if (!"ACTIVE".equalsIgnoreCase(status)
                && !"COMPLETED".equalsIgnoreCase(status)
                && !"CANCELLED".equalsIgnoreCase(status)) {
            return false;
        }

        String currentStatus = contract.getContractStatus();

        if ("ACTIVE".equalsIgnoreCase(status)
                && !"CREATED".equalsIgnoreCase(currentStatus)) {
            return false;
        }

        if ("COMPLETED".equalsIgnoreCase(status)
                && !"ACTIVE".equalsIgnoreCase(currentStatus)) {
            return false;
        }

        if ("CANCELLED".equalsIgnoreCase(status)
                && !("CREATED".equalsIgnoreCase(currentStatus)
                || "ACTIVE".equalsIgnoreCase(currentStatus))) {
            return false;
        }

        boolean updated = contractDAO.updateContractStatus(contractId, status);

        if (!updated) {
            return false;
        }

        if ("ACTIVE".equalsIgnoreCase(status)) {
            carDAO.updateStatus(contract.getCarId(), "RENTING");
            bookingDAO.updateStatus(contract.getBookingId(), "ACTIVE");
        } else if ("COMPLETED".equalsIgnoreCase(status)) {
            // Trả xe xong
            carDAO.updateStatus(contract.getCarId(), "AVAILABLE");
            bookingDAO.updateStatus(contract.getBookingId(), "COMPLETED");
        } else if ("CANCELLED".equalsIgnoreCase(status)) {
            // Hủy hợp đồng -> mở xe lại
            carDAO.updateStatus(contract.getCarId(), "AVAILABLE");
            bookingDAO.updateStatus(contract.getBookingId(), "CANCELLED");
        }

        return true;
    }

    public boolean existsByBookingId(int bookingId) {
        return contractDAO.existsByBookingId(bookingId);
    }

    public boolean completeContract(int contractId, String carNextStatus) {

        ContractModel contract = contractDAO.getContractById(contractId);

        if (contract == null) {
            return false;
        }

        if (!"ACTIVE".equalsIgnoreCase(contract.getContractStatus())) {
            return false;
        }

        boolean updated = contractDAO.updateContractStatus(contractId, "COMPLETED");
        if (!updated) {
            return false;
        }

        bookingDAO.updateStatus(contract.getBookingId(), "COMPLETED");

        if ("MAINTENANCE".equalsIgnoreCase(carNextStatus)) {
            carDAO.updateStatus(contract.getCarId(), "MAINTENANCE");
        } else {
            carDAO.updateStatus(contract.getCarId(), "AVAILABLE");
        }

        return true;
    }
}
