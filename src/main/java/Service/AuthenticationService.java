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
        account.setRoleId(1);
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

    public String validateNewPassword(String newPassword) {

        if (newPassword == null) {
            return "Password cannot be null";
        }

        // 1. Check length
        if (newPassword.length() < 8 || newPassword.length() > 12) {
            return "Password must be between 8 and 12 characters";
        }

        // 2. Check at least one uppercase letter
        if (!newPassword.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter";
        }

        // 3. Check at least one number
        if (!newPassword.matches(".*\\d.*")) {
            return "Password must contain at least one number";
        }

        return null; // null nghĩa là hợp lệ
    }
}
