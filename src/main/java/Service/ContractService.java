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

        boolean updated = contractDAO.updateContractStatus(contractId, status);

        if (!updated) {
            return false;
        }

        // CANCEL CONTRACT
        if ("CANCELLED".equalsIgnoreCase(status)) {

            bookingDAO.updateStatus(contract.getBookingId(), "CANCELLED");

            carDAO.updateStatus(contract.getCarId(), "AVAILABLE");
        }

        // ACTIVATE CONTRACT (giao xe)
        if ("ACTIVE".equalsIgnoreCase(status)) {

            carDAO.updateStatus(contract.getCarId(), "BOOKED");
        }

        // RETURN CAR
        if ("COMPLETED".equalsIgnoreCase(status)) {

            carDAO.updateStatus(contract.getCarId(), "AVAILABLE");
        }

        return true;
    }

    public boolean existsByBookingId(int bookingId) {
        return contractDAO.existsByBookingId(bookingId);
    }
}
