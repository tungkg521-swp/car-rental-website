package service;

import DALs.AccountDAO;
import DALs.CustomerDAO;
import Utils.DBContext;
import java.sql.Connection;
import java.time.LocalDate;
import models.AccountModel;
import models.CustomerModel;
import java.time.Period;

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

     

        return account;
    }

    // ================= REGISTER =================
   public void register(String fullName,
            String email,
            String password,
            String confirmPassword,
            String phone,
            String address,
            LocalDate dob) throws Exception {

        if (fullName == null || fullName.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()
                || confirmPassword == null || confirmPassword.isBlank()) {

            throw new Exception("All required fields must be filled!");
        }

        if (!fullName.matches("^[A-Za-zÀ-ỹ\\s]+$")) {
            throw new Exception("Invalid full name!");
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new Exception("Invalid email format!");
        }

        if (phone == null || !phone.matches("\\d{10}")) {
            throw new Exception("Phone number must be 10 digits!");
        }

        if (password.length() < 6) {
            throw new Exception("Password must be at least 6 characters!");
        }

        if (!password.equals(confirmPassword)) {
            throw new Exception("Password and Confirm Password do not match!");
        }

        if (accountDAO.findByEmail(email) != null) {
            throw new Exception("Email already exists!");
        }

        if (customerDAO.findByPhone(phone) != null) {
            throw new Exception("Phone already exists!");
        }

        if (dob != null) {

            if (dob.isAfter(LocalDate.now())) {
                throw new Exception("Date of birth cannot be in the future!");
            }

            int age = Period.between(dob, LocalDate.now()).getYears();

            if (age < 18) {
                throw new Exception("You must be at least 18 years old!");
            }
        }

        AccountModel account = new AccountModel();
        account.setEmail(email);
        account.setPasswordHash(password);
        account.setRoleId(1);
        account.setStatus("ACTIVE");

        int accountId = accountDAO.insertAccount(account);

        if (accountId == -1) {
            throw new Exception("Account creation failed!");
        }

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
