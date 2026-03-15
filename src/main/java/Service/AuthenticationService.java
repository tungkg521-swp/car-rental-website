package service;

import DALs.AccountDAO;
import DALs.CustomerDAO;
import java.time.LocalDate;
import java.time.Period;
import models.AccountModel;
import models.CustomerModel;

public class AuthenticationService {

    private AccountDAO accountDAO = new AccountDAO();
    private CustomerDAO customerDAO = new CustomerDAO();

    // LOGIN
    public AccountModel authenticate(String email, String password) {

        AccountModel acc = accountDAO.findByEmail(email);

        if (acc == null) {
            return null;
        }

        if (!acc.getPasswordHash().equals(password)) {
            return null;
        }

        // CHECK STATUS ONLY
        if (!"ACTIVE".equalsIgnoreCase(acc.getStatus())) {
            return null;
        }

        // UPDATE LAST LOGIN
        accountDAO.updateLastLogin(acc.getAccountId());

        // SET CUSTOMER STATUS = ACTIVE
        CustomerModel customer = customerDAO.getByAccountId(acc.getAccountId());

        if (customer != null) {
            customerDAO.updateStatus(customer.getCustomerId(), "ACTIVE");
        }

        return acc;
    }

    // REGISTER
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

        if (fullName.length() > 36) {
            throw new Exception("Full name cannot exceed 36 characters!");
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new Exception("Invalid email format!");
        }

        if (phone == null || !phone.matches("^0[98]\\d{8}$")) {
            throw new Exception("Phone number must start with 09 or 08 and contain 10 digits!");
        }

        if (!password.matches("^(?=.*[A-Z])(?=.*\\d).{8,16}$")) {
            throw new Exception("Password must be 8-16 characters and contain at least 1 uppercase letter and 1 number!");
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

            int day = dob.getDayOfMonth();
            int month = dob.getMonthValue();
            int year = dob.getYear();

            if (day < 1 || day > 31) {
                throw new Exception("Invalid day!");
            }

            if (month < 1 || month > 12) {
                throw new Exception("Invalid month!");
            }

            if (year < 1900 || year > LocalDate.now().getYear()) {
                throw new Exception("Invalid year!");
            }

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
