package service;

import DALs.AccountDAO;
import DALs.CustomerDAO;
import Utils.DBContext;
import java.sql.Connection;
import models.AccountModel;
import models.CustomerModel;

public class AuthenticationService extends DBContext {

    private AccountDAO accountDAO = new AccountDAO();
    private CustomerDAO customerDAO = new CustomerDAO();

    public AccountModel authenticate(String email, String password) {

        AccountModel account = accountDAO.findByEmail(email);

        if (account == null) {
            return null;
        }

        if (!account.getPasswordHash().equals(password)) {
            return null;
        }

        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            return null;
        }

        return account;
    }

    // ================= REGISTER =================
    public void register(String fullName,
            String email,
            String password,
            String confirmPassword,
            String phone,
            String address,
            java.time.LocalDate dob) throws Exception {

        // 1. Validate
        if (fullName == null || fullName.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()
                || confirmPassword == null || confirmPassword.isBlank()) {
            throw new Exception("All required fields must be filled!");
        }

        if (!password.equals(confirmPassword)) {
            throw new Exception("Password and Confirm Password do not match!");
        }

        if (accountDAO.findByEmail(email) != null) {
            throw new Exception("Email already exists!");
        }

        // 2. Create Account
        AccountModel account = new AccountModel();
        account.setEmail(email);
        account.setPasswordHash(password);
        account.setRoleId(2);
        account.setStatus("ACTIVE");

        int accountId = accountDAO.insertAccount(account);

        if (accountId == -1) {
            throw new Exception("Account creation failed!");
        }

        // 3. Create Customer
        CustomerModel customer = new CustomerModel();
        customer.setFullName(fullName);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setAddress(address);
        customer.setDob(dob);
        customer.setStatus("ACTIVE");
        customer.setAccountId(accountId);

        int result = customerDAO.insertCustomer(customer);

        if (result == 0) {
            throw new Exception("Customer creation failed!");
        }
    }
}
