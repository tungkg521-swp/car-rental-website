package service;

import DALs.AccountDAO;
import DALs.CustomerDAO;
import Utils.DBContext;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.Period;
import java.util.concurrent.ThreadLocalRandom;
import models.AccountModel;
import models.CustomerModel;


public class AuthenticationService extends DBContext {

    private static final String OTP_REGEX = "^\\d{6}$";
    private static final int OTP_EXPIRE_MINUTES = 5;
    private static final int MAX_OTP_ATTEMPTS = 5;

    private static final String FORGOT_ACCOUNT_ID = "FORGOT_ACCOUNT_ID";
    private static final String FORGOT_EMAIL = "FORGOT_EMAIL";
    private static final String FORGOT_OTP = "FORGOT_OTP";
    private static final String FORGOT_OTP_EXPIRED_AT = "FORGOT_OTP_EXPIRED_AT";
    private static final String FORGOT_OTP_VERIFIED = "FORGOT_OTP_VERIFIED";
    private static final String FORGOT_OTP_ATTEMPTS = "FORGOT_OTP_ATTEMPTS";
    private static final String FORGOT_MESSAGE = "FORGOT_MESSAGE";

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

    public String sendForgotPasswordOtp(String email, HttpSession session) {
        if (email == null || email.isBlank()) {
            return "Email cannot be empty.";
        }

        AccountModel account = accountDAO.findByEmail(email.trim());
        if (account == null) {
            return "If the email exists, OTP has been sent.";
        }

        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            return "This account is not active.";
        }

        clearForgotPasswordSession(session);

        String otp = generateOtp();
        long expiredAt = System.currentTimeMillis() + OTP_EXPIRE_MINUTES * 60L * 1000L;

        String subject = "[CarRental] Your OTP to reset password";
        String content = buildOtpEmailContent(otp);

        boolean sent = MailService.sendEmail(account.getEmail(), subject, content);
        if (!sent) {
            return "Failed to send OTP email. Please check your mail configuration.";
        }

        session.setAttribute(FORGOT_ACCOUNT_ID, account.getAccountId());
        session.setAttribute(FORGOT_EMAIL, account.getEmail());
        session.setAttribute(FORGOT_OTP, otp);
        session.setAttribute(FORGOT_OTP_EXPIRED_AT, expiredAt);
        session.setAttribute(FORGOT_OTP_VERIFIED, Boolean.FALSE);
        session.setAttribute(FORGOT_OTP_ATTEMPTS, 0);
        session.setAttribute(FORGOT_MESSAGE,
                "OTP has been sent to your email. Please enter the code within " + OTP_EXPIRE_MINUTES + " minutes.");

        return "SUCCESS";
    }

    public String verifyForgotPasswordOtp(String inputOtp, HttpSession session) {
        Object sessionOtp = session.getAttribute(FORGOT_OTP);
        Object sessionExpiry = session.getAttribute(FORGOT_OTP_EXPIRED_AT);
        Object sessionEmail = session.getAttribute(FORGOT_EMAIL);

        if (sessionOtp == null || sessionExpiry == null || sessionEmail == null) {
            return "Your OTP session has expired. Please request a new OTP.";
        }

        if (inputOtp == null || !inputOtp.matches(OTP_REGEX)) {
            return "OTP must contain exactly 6 digits.";
        }

        long expiredAt = (long) sessionExpiry;
        if (System.currentTimeMillis() > expiredAt) {
            clearForgotPasswordSession(session);
            return "OTP has expired. Please request a new OTP.";
        }

        int attempts = getOtpAttempts(session) + 1;
        session.setAttribute(FORGOT_OTP_ATTEMPTS, attempts);

        if (attempts > MAX_OTP_ATTEMPTS) {
            clearForgotPasswordSession(session);
            return "You entered the wrong OTP too many times. Please request a new OTP.";
        }

        if (!inputOtp.equals(sessionOtp.toString())) {
            int remaining = MAX_OTP_ATTEMPTS - attempts;
            return "Incorrect OTP. Remaining attempts: " + Math.max(remaining, 0) + ".";
        }

        session.setAttribute(FORGOT_OTP_VERIFIED, Boolean.TRUE);
        session.removeAttribute(FORGOT_OTP);
        session.removeAttribute(FORGOT_OTP_EXPIRED_AT);
        session.removeAttribute(FORGOT_OTP_ATTEMPTS);
        session.setAttribute(FORGOT_MESSAGE, "OTP verified successfully. Please set a new password.");

        return "SUCCESS";
    }

    public String resetPasswordAfterOtp(HttpSession session,
            String newPassword,
            String confirmPassword) {

        Object accountIdObj = session.getAttribute(FORGOT_ACCOUNT_ID);
        Object emailObj = session.getAttribute(FORGOT_EMAIL);
        Object verifiedObj = session.getAttribute(FORGOT_OTP_VERIFIED);

        if (accountIdObj == null || emailObj == null || !(verifiedObj instanceof Boolean) || !((Boolean) verifiedObj)) {
            return "You have not verified OTP yet.";
        }

        if (newPassword == null || newPassword.isBlank()
                || confirmPassword == null || confirmPassword.isBlank()) {
            return "All fields are required.";
        }

        if (!newPassword.equals(confirmPassword)) {
            return "Confirm password does not match.";
        }

        if (!newPassword.matches("^(?=.*[A-Z])(?=.*\\d).{8,16}$")) {
            return "Password must be 8-16 characters and contain at least 1 uppercase letter and 1 number!";
        }

        int accountId = Integer.parseInt(accountIdObj.toString());
        AccountModel account = accountDAO.findById(accountId);
        if (account == null) {
            clearForgotPasswordSession(session);
            return "Account not found.";
        }

        if (!account.getEmail().equalsIgnoreCase(emailObj.toString())) {
            clearForgotPasswordSession(session);
            return "OTP session is invalid.";
        }

        int updated = accountDAO.updatePasswordByAccountId(accountId, newPassword);
        if (updated <= 0) {
            return "Reset password failed.";
        }

        clearForgotPasswordSession(session);
        return "SUCCESS";
    }

    public String getForgotPasswordMessage(HttpSession session) {
        Object message = session.getAttribute(FORGOT_MESSAGE);
        if (message == null) {
            return null;
        }
        session.removeAttribute(FORGOT_MESSAGE);
        return message.toString();
    }

    public boolean hasForgotPasswordEmail(HttpSession session) {
        return session.getAttribute(FORGOT_EMAIL) != null;
    }

    public boolean isForgotOtpVerified(HttpSession session) {
        Object verified = session.getAttribute(FORGOT_OTP_VERIFIED);
        return verified instanceof Boolean && (Boolean) verified;
    }

    public String getForgotPasswordEmail(HttpSession session) {
        Object email = session.getAttribute(FORGOT_EMAIL);
        return email == null ? null : email.toString();
    }

    public void clearForgotPasswordSession(HttpSession session) {
        session.removeAttribute(FORGOT_ACCOUNT_ID);
        session.removeAttribute(FORGOT_EMAIL);
        session.removeAttribute(FORGOT_OTP);
        session.removeAttribute(FORGOT_OTP_EXPIRED_AT);
        session.removeAttribute(FORGOT_OTP_VERIFIED);
        session.removeAttribute(FORGOT_OTP_ATTEMPTS);
        session.removeAttribute(FORGOT_MESSAGE);
    }

    private int getOtpAttempts(HttpSession session) {
        Object attempts = session.getAttribute(FORGOT_OTP_ATTEMPTS);
        if (attempts instanceof Integer) {
            return (Integer) attempts;
        }
        return 0;
    }

    private String generateOtp() {
        int number = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(number);
    }

    private String buildOtpEmailContent(String otp) {
        return "Hello,\n\n"
                + "You requested to reset your CarRental password.\n"
                + "Your OTP code is: " + otp + "\n"
                + "This OTP will expire in " + OTP_EXPIRE_MINUTES + " minutes.\n\n"
                + "If you did not request this, please ignore this email.\n\n"
                + "Best regards,\nCarRental Team";
    }
}