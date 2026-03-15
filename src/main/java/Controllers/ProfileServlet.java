/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import service.ProfileService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import models.AccountModel;
import models.CustomerModel;
import models.DriverLicenseModel;
import service.CustomerService;
import service.DriverLicenseService;

@MultipartConfig

/**
 *
 * @author ADMIN
 */
public class ProfileServlet extends HttpServlet {

    private CustomerService customerService = new CustomerService();

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        AccountModel account
                = (AccountModel) session.getAttribute("ACCOUNT");

        if (account == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        // ===== CHANGE PASSWORD PAGE =====
        if ("changePassword".equals(action)) {
            request.getRequestDispatcher("/views/change-password.jsp")
                    .forward(request, response);
            return;
        }

        // ===== PROFILE PAGE =====
        CustomerModel customer
                = customerService.getProfileByAccountId(
                        account.getAccountId());

        request.setAttribute("CUSTOMER_PROFILE", customer);

        DriverLicenseService licenseService
                = new DriverLicenseService();

        DriverLicenseModel license
                = licenseService.getByCustomerId(
                        customer.getCustomerId());

        request.setAttribute("LICENSE", license);

        request.getRequestDispatcher("/views/profile.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("update".equals(action)) {
            updateProfile(request, response);
        } else if ("updateLicense".equals(action)) {
            updateLicense(request, response);
        } else if ("requestVerification".equals(action)) {
            requestVerification(request, response);
        } else if ("changePassword".equals(action)) {
            changePassword(request, response);
        }

    }

    /* ================= UPDATE PROFILE ================= */
    private void updateProfile(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        AccountModel account
                = (AccountModel) session.getAttribute("ACCOUNT");

        if (account == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String fullName = request.getParameter("fullName");
        LocalDate dob = LocalDate.parse(request.getParameter("dob"));

        String phone = request.getParameter("phone");
        String email = request.getParameter("email");

        boolean success = customerService.updateProfile(
                account.getAccountId(),
                fullName,
                dob,
                phone,
                email
        );

        if (success) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/customer/profile?msg=success"
            );
        } else {
            response.sendRedirect(
                    request.getContextPath()
                    + "/customer/profile?msg=error"
            );
        }
    }

    private void updateLicense(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            HttpSession session = request.getSession(false);

            if (session == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            AccountModel account
                    = (AccountModel) session.getAttribute("ACCOUNT");

            if (account == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            CustomerModel customer
                    = customerService.getProfileByAccountId(
                            account.getAccountId());

            if (customer == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            DriverLicenseService service
                    = new DriverLicenseService();

            // Lấy license cũ (để không mất ảnh nếu không upload mới)
            DriverLicenseModel existing
                    = service.getByCustomerId(customer.getCustomerId());

            DriverLicenseModel dl = new DriverLicenseModel();
            dl.setCustomerId(customer.getCustomerId());

            dl.setLicenseNumber(request.getParameter("licenseNumber"));
            dl.setFullName(request.getParameter("fullName"));

            // ===== DOB =====
            String dobStr = request.getParameter("dob");
            if (dobStr != null && !dobStr.trim().isEmpty()) {
                dl.setDob(LocalDate.parse(dobStr));
            }

            // ===== ISSUE DATE =====
            String issueStr = request.getParameter("issueDate");
            if (issueStr != null && !issueStr.trim().isEmpty()) {
                dl.setIssueDate(LocalDate.parse(issueStr));
            }

// ===== EXPIRY DATE =====
            String expiryStr = request.getParameter("expiryDate");
            if (expiryStr != null && !expiryStr.trim().isEmpty()) {
                dl.setExpiryDate(LocalDate.parse(expiryStr));
            }

            // ===== UPLOAD PATH =====
            String uploadPath = "C:/Users/ADMIN/Documents/SWP391/Project_License/license_images";

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // ===== IMAGE FRONT =====
            Part frontPart = request.getPart("imageFront");

            if (frontPart != null && frontPart.getSize() > 0) {

                String fileName
                        = System.currentTimeMillis() + "_front.jpg";

                System.out.println("UPLOAD PATH = " + uploadPath);
                System.out.println("FRONT SIZE = " + frontPart.getSize());

                frontPart.write(uploadPath + File.separator + fileName);

                dl.setImageFront(fileName);

            } else if (existing != null) {
                dl.setImageFront(existing.getImageFront());
            }

            // ===== IMAGE BACK =====
            Part backPart = request.getPart("imageBack");

            if (backPart != null && backPart.getSize() > 0) {

                String fileName
                        = System.currentTimeMillis() + "_back.jpg";

                backPart.write(uploadPath + File.separator + fileName);

                dl.setImageBack(fileName);

            } else if (existing != null) {
                dl.setImageBack(existing.getImageBack());
            }

            service.saveOrUpdate(dl);

            response.sendRedirect(
                    request.getContextPath()
                    + "/customer/profile?msg=success");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(
                    request.getContextPath()
                    + "/customer/profile?msg=error");
        }
    }

    private void requestVerification(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        AccountModel account
                = (AccountModel) session.getAttribute("ACCOUNT");

        if (account == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        CustomerModel customer
                = customerService.getProfileByAccountId(
                        account.getAccountId());

        DriverLicenseService service
                = new DriverLicenseService();

        service.updateStatus(
                customer.getCustomerId(),
                "REQUESTED");

        response.sendRedirect(
                request.getContextPath()
                + "/customer/profile?msg=success");
    }

    private void changePassword(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        AccountModel acc
                = (AccountModel) session.getAttribute("ACCOUNT");

        if (acc == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int accountId = acc.getAccountId();

        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        ProfileService service = new ProfileService();

        String result = service.changePassword(
                accountId,
                oldPassword,
                newPassword,
                confirmPassword
        );

        if ("SUCCESS".equals(result)) {

            request.getSession().setAttribute(
                    "success",
                    "Đổi mật khẩu thành công!"
            );
            response.sendRedirect(
                    request.getContextPath() + "/customer/profile?action=changePassword"
            );

            return;
        } else {

            request.setAttribute("error", result);

        }

        request.getRequestDispatcher("/views/change-password.jsp")
                .forward(request, response);
    }

    
    
}
