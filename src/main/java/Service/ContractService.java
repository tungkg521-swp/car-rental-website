/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import DALs.ContractDAO;
import models.ContractModel;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class ContractService {

    private final ContractDAO contractDAO = new ContractDAO();

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
        return contractDAO.updateContractStatus(contractId, status);
    }

    public boolean existsByBookingId(int bookingId) {
        return contractDAO.existsByBookingId(bookingId);
    }
}
