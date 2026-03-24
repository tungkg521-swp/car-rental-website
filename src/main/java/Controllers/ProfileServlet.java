package Controllers;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;
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
public class ProfileServlet extends HttpServlet {

    private final CustomerService customerService = new CustomerService();
    private final DriverLicenseService licenseService = new DriverLicenseService();

    // Sửa lại path này theo máy của bạn
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

        CustomerModel customer = customerService.getProfileByAccountId(account.getAccountId());

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setAttribute("CUSTOMER_PROFILE", customer);

        DriverLicenseModel license = licenseService.getByCustomerId(customer.getCustomerId());
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

            boolean success = customerService.updateProfile(
                    account.getAccountId(),
                    fullName,
                    dob,
                    phone,
                    email
            );

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

        CustomerModel customer = customerService.getProfileByAccountId(account.getAccountId());

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        DriverLicenseModel existing = licenseService.getByCustomerId(customer.getCustomerId());
        DriverLicenseModel draft = licenseService.getDraftFromSession(session);

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

        // merge ảnh: upload mới > draft session > DB cũ
        licenseService.mergeImageFields(dl, existing, draft);

        java.util.Map<String, String> errors = licenseService.validateDriverLicense(dl);

        if (!errors.isEmpty()) {
            licenseService.saveDraftToSession(session, dl);

            request.setAttribute("CUSTOMER_PROFILE", customer);
            request.setAttribute("LICENSE", existing);
            request.setAttribute("licenseForm", dl);
            request.setAttribute("licenseErrors", errors);
            request.setAttribute("enableLicenseEdit", true);
            request.getRequestDispatcher("/views/profile.jsp").forward(request, response);
            return;
        }

        String result = licenseService.saveOrUpdate(dl);

        if ("SUCCESS".equals(result)) {
            licenseService.clearDraftSession(session);
            response.sendRedirect(request.getContextPath() + "/customer/profile?msg=success");
        } else {
            licenseService.saveDraftToSession(session, dl);

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

        CustomerModel customer = customerService.getProfileByAccountId(account.getAccountId());

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String result = licenseService.requestVerification(customer.getCustomerId());
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

        ProfileService service = new ProfileService();

        String result = service.changePassword(
                accountId,
                oldPassword,
                newPassword,
                confirmPassword
        );

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
}
