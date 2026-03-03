/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import DALs.CustomerDAO;
import java.util.List;
import models.CustomerModel;

/**
 *
 * @author ADMIN
 */
public class CustomerService {

    private CustomerDAO customerDAO = new CustomerDAO();

    public List<CustomerModel> findAllCustomers() {
        return customerDAO.findAllCustomers();
    }

    public CustomerModel getCustomerById(int id) {
        return customerDAO.findById(id);
    }

    public CustomerModel getProfileByAccountId(int accountId) {
        CustomerDAO dao = new CustomerDAO();
        return dao.getByAccountId(accountId);
    }

    public boolean updateProfile(int accountId,
                             String fullName,
                             String dob,
                             String phone,
                             String email) {

    CustomerDAO dao = new CustomerDAO();

    return dao.updateProfile(
            accountId,
            fullName,
            dob,
            phone,
            email
    ) > 0;
}
}
