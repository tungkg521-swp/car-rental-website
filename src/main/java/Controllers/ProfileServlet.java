package Controllers;


import DALs.CustomerDAO;
import DALs.DriverLicenseDAO;
import DALs.ProfileDAO;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.time.Period;
import java.util.LinkedHashMap;
import java.util.Map;
import models.AccountModel;
import models.CustomerModel;
import models.DriverLicenseModel;



@MultipartConfig
public class ProfileServlet extends HttpServlet {



    private final ProfileDAO profileDAO = new ProfileDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final DriverLicenseDAO driverLicenseDAO = new DriverLicenseDAO();
    private static final String LICENSE_DRAFT_SESSION_KEY = "LICENSE_DRAFT";

  

    private static final String LICENSE_UPLOAD_PATH
            = "C:/Users/ADMIN/Documents/SWP391/Project_License/license_images";

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        AccountModel account = (AccountModel) session.getAttribute("ACCOUNT");

        if (account == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        if ("changePassword".equals(action)) {
            request.getRequestDispatcher("/views/change-password.jsp")
                    .forward(request, response);
            return;
        }


        CustomerModel customer = customerDAO.getByAccountId(account.getAccountId());


        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setAttribute("CUSTOMER_PROFILE", customer);


        DriverLicenseModel license = driverLicenseDAO.getByCustomerId(customer.getCustomerId());

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
        } else {
            response.sendRedirect(request.getContextPath() + "/customer/profile?msg=error");
        }
    }

    private void updateProfile(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        AccountModel account = (AccountModel) session.getAttribute("ACCOUNT");

        if (account == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String fullName = request.getParameter("fullName");
            String dobRaw = request.getParameter("dob");
            String phone = request.getParameter("phone");
            String email = request.getParameter("email");

            LocalDate dob = null;
            if (dobRaw != null && !dobRaw.trim().isEmpty()) {
                dob = LocalDate.parse(dobRaw);
            }


            boolean success = customerDAO.updateProfile(

                    account.getAccountId(),
                    fullName,
                    dob,
                    phone,
                    email

            ) > 0;


            if (success) {
                response.sendRedirect(request.getContextPath() + "/customer/profile?msg=success");
            } else {
                response.sendRedirect(request.getContextPath() + "/customer/profile?msg=error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/customer/profile?msg=error");
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

            AccountModel account = (AccountModel) session.getAttribute("ACCOUNT");

            if (account == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            CustomerModel customer = customerDAO.getByAccountId(account.getAccountId());

            if (customer == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            DriverLicenseModel existing = driverLicenseDAO.getByCustomerId(customer.getCustomerId());
            DriverLicenseModel draft = getDraftFromSession(session);

            DriverLicenseModel dl = new DriverLicenseModel();
            dl.setCustomerId(customer.getCustomerId());

            dl.setLicenseNumber(trimParam(request.getParameter("licenseNumber")));
            dl.setFullName(trimParam(request.getParameter("fullName")));

            String dobStr = request.getParameter("dob");
            if (dobStr != null && !dobStr.trim().isEmpty()) {
                dl.setDob(LocalDate.parse(dobStr));
            }

            String issueStr = request.getParameter("issueDate");
            if (issueStr != null && !issueStr.trim().isEmpty()) {
                dl.setIssueDate(LocalDate.parse(issueStr));
            }

            String expiryStr = request.getParameter("expiryDate");
            if (expiryStr != null && !expiryStr.trim().isEmpty()) {
                dl.setExpiryDate(LocalDate.parse(expiryStr));
            }

            File uploadDir = new File(LICENSE_UPLOAD_PATH);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            Part frontPart = request.getPart("imageFront");
            String frontFile = saveImage(frontPart, "gplx_front");
            dl.setImageFront(frontFile);

            Part backPart = request.getPart("imageBack");
            String backFile = saveImage(backPart, "gplx_back");
            dl.setImageBack(backFile);

            Part selfiePart = request.getPart("selfieImage");
            String selfieFile = saveImage(selfiePart, "selfie");
            dl.setSelfieImage(selfieFile);

            Part idFrontPart = request.getPart("nationalIdFront");
            String idFrontFile = saveImage(idFrontPart, "cccd_front");
            dl.setNationalIdFront(idFrontFile);

            Part idBackPart = request.getPart("nationalIdBack");
            String idBackFile = saveImage(idBackPart, "cccd_back");
            dl.setNationalIdBack(idBackFile);

          
            mergeImageFields(dl, existing, draft);

            Map<String, String> errors = validateDriverLicense(dl);

            if (!errors.isEmpty()) {
                saveDraftToSession(session, dl);

                request.setAttribute("CUSTOMER_PROFILE", customer);
                request.setAttribute("LICENSE", existing);
                request.setAttribute("licenseForm", dl);
                request.setAttribute("licenseErrors", errors);
                request.setAttribute("enableLicenseEdit", true);
                request.getRequestDispatcher("/views/profile.jsp").forward(request, response);
                return;
            }

            String result = saveOrUpdateLicense(dl);

            if ("SUCCESS".equals(result)) {
                clearDraftSession(session);
                response.sendRedirect(request.getContextPath() + "/customer/profile?msg=success");
            } else {
                saveDraftToSession(session, dl);

                request.setAttribute("CUSTOMER_PROFILE", customer);
                request.setAttribute("LICENSE", existing);
                request.setAttribute("licenseForm", dl);
                request.setAttribute("enableLicenseEdit", true);
                request.setAttribute("saveError", "Cập nhật GPLX thất bại.");
                request.getRequestDispatcher("/views/profile.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/customer/profile?msg=error");
        }
    }

    private void requestVerification(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        AccountModel account = (AccountModel) session.getAttribute("ACCOUNT");

        if (account == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }


        CustomerModel customer = customerDAO.getByAccountId(account.getAccountId());


        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }


        String result = requestLicenseVerification(customer.getCustomerId());

        response.sendRedirect(request.getContextPath() + "/customer/profile?msg=" + mapLicenseMessage(result));
    }

    private void changePassword(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        AccountModel acc = (AccountModel) session.getAttribute("ACCOUNT");

        if (acc == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int accountId = acc.getAccountId();

        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");


        String result = handleChangePassword(accountId, oldPassword, newPassword, confirmPassword);


        if ("SUCCESS".equals(result)) {
            request.getSession().setAttribute("success", "Đổi mật khẩu thành công!");
            response.sendRedirect(request.getContextPath() + "/customer/profile?action=changePassword");
            return;
        } else {
            request.setAttribute("error", result);
        }

        request.getRequestDispatcher("/views/change-password.jsp")
                .forward(request, response);
    }

    private String trimParam(String value) {
        return value == null ? null : value.trim();
    }

    private String saveImage(Part part, String prefix) throws IOException {
        if (part == null || part.getSize() <= 0) {
            return null;
        }

        String submittedName = part.getSubmittedFileName();
        String extension = getExtension(submittedName);

        String fileName = prefix + "_" + UUID.randomUUID() + extension;
        part.write(LICENSE_UPLOAD_PATH + File.separator + fileName);

        return fileName;
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ".jpg";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private String mapLicenseMessage(String result) {
        switch (result) {
            case "SUCCESS":
                return "success";

            case "VERIFY_SUCCESS":
                return "verify_success";

            case "NOT_FOUND":
                return "license_not_found";

            case "ALREADY_REQUESTED":
                return "already_requested";

            case "ALREADY_APPROVED":
                return "already_approved";

            case "LICENSE_NUMBER_REQUIRED":
                return "license_number_required";

            case "LICENSE_NUMBER_INVALID":
                return "license_number_invalid";

            case "FULL_NAME_REQUIRED":
                return "full_name_required";

            case "FULL_NAME_INVALID":
                return "full_name_invalid";

            case "DOB_REQUIRED":
                return "dob_required";

            case "DOB_UNDER_18":
                return "dob_under_18";

            case "ISSUE_DATE_REQUIRED":
                return "issue_date_required";

            case "ISSUE_DATE_INVALID":
                return "issue_date_invalid";

            case "EXPIRY_DATE_REQUIRED":
                return "expiry_date_required";

            case "EXPIRY_DATE_INVALID":
                return "expiry_date_invalid";

            case "EXPIRY_DATE_OVER_10_YEARS":
                return "expiry_date_over_10_years";

            case "IMAGE_FRONT_REQUIRED":
                return "image_front_required";

            case "IMAGE_BACK_REQUIRED":
                return "image_back_required";

            case "SELFIE_IMAGE_REQUIRED":
                return "selfie_image_required";

            case "NATIONAL_ID_FRONT_REQUIRED":
                return "national_id_front_required";

            case "NATIONAL_ID_BACK_REQUIRED":
                return "national_id_back_required";

            default:
                return "error";
        }
    }


    private String handleChangePassword(int accountId, String oldPassword,
            String newPassword, String confirmPassword) {

        if (oldPassword != null && oldPassword.equals(newPassword)) {
            return "New password cannot be the same as current password!";
        }

        if (newPassword == null || !newPassword.equals(confirmPassword)) {
            return "Confirm password does not match!";
        }

        String validate = validateNewPassword(newPassword);
        if (validate != null) {
            return validate;
        }

        try {
            int result = profileDAO.changePassword(accountId, oldPassword, newPassword);
            if (result == 1) {
                return "SUCCESS";
            } else {
                return "Old password is incorrect!";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "System error!";
        }
    }

    private String validateNewPassword(String newPassword) {
        if (newPassword == null) {
            return "Password cannot be null";
        }

        if (newPassword.length() < 6) {
            return "The password must have at least 6 characters.";
        }

        if (!newPassword.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter";
        }

        if (!newPassword.matches(".*\\d.*")) {
            return "Password must contain at least one number";
        }

        return null;
    }

    private DriverLicenseModel getDraftFromSession(HttpSession session) {
        Object obj = session.getAttribute(LICENSE_DRAFT_SESSION_KEY);
        if (obj instanceof DriverLicenseModel) {
            return (DriverLicenseModel) obj;
        }
        return null;
    }

    private void saveDraftToSession(HttpSession session, DriverLicenseModel draft) {
        session.setAttribute(LICENSE_DRAFT_SESSION_KEY, draft);
    }

    private void clearDraftSession(HttpSession session) {
        session.removeAttribute(LICENSE_DRAFT_SESSION_KEY);
    }

    private void mergeImageFields(DriverLicenseModel target,
            DriverLicenseModel existing,
            DriverLicenseModel draft) {

        target.setImageFront(firstNotBlank(
                target.getImageFront(),
                draft != null ? draft.getImageFront() : null,
                existing != null ? existing.getImageFront() : null
        ));

        target.setImageBack(firstNotBlank(
                target.getImageBack(),
                draft != null ? draft.getImageBack() : null,
                existing != null ? existing.getImageBack() : null
        ));

        target.setSelfieImage(firstNotBlank(
                target.getSelfieImage(),
                draft != null ? draft.getSelfieImage() : null,
                existing != null ? existing.getSelfieImage() : null
        ));

        target.setNationalIdFront(firstNotBlank(
                target.getNationalIdFront(),
                draft != null ? draft.getNationalIdFront() : null,
                existing != null ? existing.getNationalIdFront() : null
        ));

        target.setNationalIdBack(firstNotBlank(
                target.getNationalIdBack(),
                draft != null ? draft.getNationalIdBack() : null,
                existing != null ? existing.getNationalIdBack() : null
        ));
    }

    private String firstNotBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private Map<String, String> validateDriverLicense(DriverLicenseModel license) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (license == null) {
            errors.put("general", "Dữ liệu GPLX không hợp lệ.");
            return errors;
        }

        if (isBlank(license.getLicenseNumber())) {
            errors.put("licenseNumber", "Số GPLX không được để trống.");
        } else {
            String licenseNumber = license.getLicenseNumber().trim();
            if (!licenseNumber.matches("\\d{12}")) {
                errors.put("licenseNumber", "Số GPLX phải gồm đúng 12 chữ số.");
            }
        }

        if (isBlank(license.getFullName())) {
            errors.put("fullName", "Họ và tên không được để trống.");
        } else {
            String fullName = normalizeSpaces(license.getFullName());
            if (!isValidFullName(fullName)) {
                errors.put("fullName", "Họ và tên không đúng định dạng. Ví dụ: Le Van Tinh.");
            }
        }

        if (license.getDob() == null) {
            errors.put("dob", "Ngày sinh không được để trống.");
        } else {
            if (license.getDob().isAfter(LocalDate.now())) {
                errors.put("dob", "Ngày sinh không được lớn hơn ngày hiện tại.");
            } else if (!isEnough18YearsOld(license.getDob())) {
                errors.put("dob", "Người dùng phải từ đủ 18 tuổi trở lên.");
            }
        }

        if (license.getIssueDate() == null) {
            errors.put("issueDate", "Ngày cấp không được để trống.");
        } else {
            if (license.getIssueDate().isAfter(LocalDate.now())) {
                errors.put("issueDate", "Ngày cấp không được lớn hơn ngày hiện tại.");
            }

            if (license.getDob() != null) {
                LocalDate minIssueDate = license.getDob().plusYears(18);
                if (license.getIssueDate().isBefore(minIssueDate)) {
                    errors.put("issueDate", "Ngày cấp phải từ thời điểm người dùng đủ 18 tuổi trở lên.");
                }
            }
        }

        if (license.getExpiryDate() == null) {
            errors.put("expiryDate", "Ngày hết hạn không được để trống.");
        } else if (license.getIssueDate() != null) {
            if (license.getExpiryDate().isBefore(license.getIssueDate())) {
                errors.put("expiryDate", "Ngày hết hạn phải lớn hơn hoặc bằng ngày cấp.");
            } else if (license.getExpiryDate().isAfter(license.getIssueDate().plusYears(10))) {
                errors.put("expiryDate", "Ngày hết hạn không được quá 10 năm kể từ ngày cấp.");
            }
        }

        if (isBlank(license.getImageFront())) {
            errors.put("imageFront", "Vui lòng tải ảnh GPLX mặt trước.");
        }
        if (isBlank(license.getImageBack())) {
            errors.put("imageBack", "Vui lòng tải ảnh GPLX mặt sau.");
        }
        if (isBlank(license.getSelfieImage())) {
            errors.put("selfieImage", "Vui lòng tải ảnh selfie cầm giấy tờ.");
        }
        if (isBlank(license.getNationalIdFront())) {
            errors.put("nationalIdFront", "Vui lòng tải ảnh CCCD mặt trước.");
        }
        if (isBlank(license.getNationalIdBack())) {
            errors.put("nationalIdBack", "Vui lòng tải ảnh CCCD mặt sau.");
        }

        return errors;
    }

    private String saveOrUpdateLicense(DriverLicenseModel dl) {
        try {
            Map<String, String> errors = validateDriverLicense(dl);
            if (!errors.isEmpty()) {
                return "INVALID";
            }

            dl.setLicenseNumber(dl.getLicenseNumber().trim());
            dl.setFullName(normalizeFullName(dl.getFullName()));

            DriverLicenseModel existing = driverLicenseDAO.getByCustomerId(dl.getCustomerId());

            if (existing == null) {
                return driverLicenseDAO.insert(dl) > 0 ? "SUCCESS" : "ERROR";
            } else {
                return driverLicenseDAO.update(dl) > 0 ? "SUCCESS" : "ERROR";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    private String requestLicenseVerification(int customerId) {
        try {
            DriverLicenseModel license = driverLicenseDAO.getByCustomerId(customerId);

            if (license == null) {
                return "NOT_FOUND";
            }

            if ("APPROVED".equalsIgnoreCase(license.getStatus())) {
                return "ALREADY_APPROVED";
            }

            if ("REQUESTED".equalsIgnoreCase(license.getStatus())) {
                return "ALREADY_REQUESTED";
            }

            Map<String, String> errors = validateDriverLicense(license);
            if (!errors.isEmpty()) {
                return "INVALID";
            }

            driverLicenseDAO.updateStatusCus(customerId, "REQUESTED");
            return "SUCCESS";

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    private boolean isEnough18YearsOld(LocalDate dob) {
        return Period.between(dob, LocalDate.now()).getYears() >= 18;
    }

    private boolean isValidFullName(String fullName) {
        String normalized = normalizeSpaces(fullName);
        return normalized.matches("^[A-ZÀ-Ỹ][a-zà-ỹ]*(\\s+[A-ZÀ-Ỹ][a-zà-ỹ]*)+$");
    }

    private String normalizeFullName(String fullName) {
        String[] words = normalizeSpaces(fullName).split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }

            String lower = word.toLowerCase();
            String normalizedWord = Character.toUpperCase(lower.charAt(0)) + lower.substring(1);

            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(normalizedWord);
        }

        return result.toString();
    }

    private String normalizeSpaces(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}
