/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;


import DALs.AccountDAO;
import DALs.CustomerDAO;
import Utils.RoleConstants;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.time.LocalDate;
import java.time.Period;
import models.AccountModel;
import models.CustomerModel;


public class RegisterServlet extends HttpServlet {

    private final AccountDAO accountDAO = new AccountDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


     

        request.getRequestDispatcher("/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String dobStr = request.getParameter("dob");

        java.time.LocalDate dob = null;
        if (dobStr != null && !dobStr.isEmpty()) {
            dob = java.time.LocalDate.parse(dobStr);
        }


        try {
            validateRegisterInput(fullName, email, password, confirmPassword, phone, dob);

            AccountModel existed = accountDAO.findByEmail(email);
            if (existed != null) {
                throw new Exception("Email đã tồn tại.");
            }

            if (customerDAO.findByPhone(phone) != null) {
                throw new Exception("Số điện thoại đã tồn tại.");
            }

            AccountModel account = new AccountModel();
            account.setEmail(email.trim());
            account.setPasswordHash(password);
            account.setRoleId(RoleConstants.CUSTOMER);
            account.setStatus("ACTIVE");

            int accountId = accountDAO.insertAccount(account);
            if (accountId <= 0) {
                throw new Exception("Tạo tài khoản thất bại.");
            }

            CustomerModel customer = new CustomerModel();
            customer.setAccountId(accountId);
            customer.setFullName(fullName.trim());
            customer.setPhone(phone.trim());
            customer.setAddress(address != null ? address.trim() : "");
            customer.setDob(dob);
            customer.setStatus("ACTIVE");

            int result = customerDAO.insertCustomer(customer);
            if (result <= 0) {
                throw new Exception("Tạo hồ sơ khách hàng thất bại.");
            }


            response.sendRedirect("login");

        } catch (Exception e) {

            request.setAttribute("error", e.getMessage());
            refillRegisterForm(request, fullName, email, phone, address, dobStr);
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
        }
    }
    
    
        private void validateRegisterInput(String fullName, String email, String password,
            String confirmPassword, String phone, LocalDate dob) throws Exception {

        if (isBlank(fullName) || isBlank(email) || isBlank(password)
                || isBlank(confirmPassword) || isBlank(phone) || dob == null) {
            throw new Exception("Vui lòng nhập đầy đủ thông tin.");
        }

        if (!fullName.trim().matches("^[\\p{L} ]{2,100}$")) {
            throw new Exception("Họ tên không hợp lệ.");
        }

        if (!email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new Exception("Email không hợp lệ.");
        }

        if (!phone.trim().matches("^\\d{10,11}$")) {
            throw new Exception("Số điện thoại không hợp lệ.");
        }

        if (!password.equals(confirmPassword)) {
            throw new Exception("Xác nhận mật khẩu không khớp.");
        }

        String passwordError = validatePassword(password);
        if (passwordError != null) {
            throw new Exception(passwordError);
        }

        int age = Period.between(dob, LocalDate.now()).getYears();
        if (age < 18) {
            throw new Exception("Bạn phải từ 18 tuổi trở lên.");
        }
    }

    private String validatePassword(String password) {
        if (password == null || password.length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự.";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Mật khẩu phải có ít nhất 1 chữ in hoa.";
        }
        if (!password.matches(".*\\d.*")) {
            return "Mật khẩu phải có ít nhất 1 chữ số.";
        }
        return null;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private void refillRegisterForm(HttpServletRequest request, String fullName,
            String email, String phone, String address, String dobStr) {
        request.setAttribute("fullName", fullName);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);
        request.setAttribute("address", address);
        request.setAttribute("dob", dobStr);
    }

}
