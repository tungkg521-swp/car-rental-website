/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import DALs.CustomerDAO;
import java.time.LocalDate;
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

        return customerDAO.getByAccountId(accountId);
    }

    public boolean updateProfile(int accountId,
            String fullName,
            LocalDate dob,
            String phone,
            String email) {

        return customerDAO.updateProfile(
                accountId,
                fullName,
                dob,
                phone,
                email
        ) > 0;
    }

//    public List<CustomerModel> searchCustomer(String fullname, String status) {
//    return customerDAO.searchCustomer(fullname, status);
//}
    
    
}
