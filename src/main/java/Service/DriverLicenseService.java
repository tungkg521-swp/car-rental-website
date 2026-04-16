package service;

import DALs.CustomerDAO;
import DALs.DriverLicenseDAO;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import models.DriverLicenseModel;

public class DriverLicenseService {

    private final DriverLicenseDAO dao = new DriverLicenseDAO();
    private static final String LICENSE_DRAFT_SESSION_KEY = "LICENSE_DRAFT";

    public DriverLicenseModel getByCustomerId(int customerId) {
        return dao.getByCustomerId(customerId);
    }

    public DriverLicenseModel getDraftFromSession(HttpSession session) {
        Object obj = session.getAttribute(LICENSE_DRAFT_SESSION_KEY);
        if (obj instanceof DriverLicenseModel) {
            return (DriverLicenseModel) obj;
        }
        return null;
    }

    public void saveDraftToSession(HttpSession session, DriverLicenseModel draft) {
        session.setAttribute(LICENSE_DRAFT_SESSION_KEY, draft);
    }

    public void clearDraftSession(HttpSession session) {
        session.removeAttribute(LICENSE_DRAFT_SESSION_KEY);
    }

    public void mergeImageFields(DriverLicenseModel target,
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

    public Map<String, String> validateDriverLicense(DriverLicenseModel license) {
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

    public String saveOrUpdate(DriverLicenseModel dl) {
        try {
            Map<String, String> errors = validateDriverLicense(dl);
            if (!errors.isEmpty()) {
                return "INVALID";
            }

            dl.setLicenseNumber(dl.getLicenseNumber().trim());
            dl.setFullName(normalizeFullName(dl.getFullName()));

            DriverLicenseModel existing = dao.getByCustomerId(dl.getCustomerId());

            if (existing == null) {
                return dao.insert(dl) > 0 ? "SUCCESS" : "ERROR";
            } else {
                return dao.update(dl) > 0 ? "SUCCESS" : "ERROR";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    public boolean updateStatus(int customerId, String status) {
        try {
            dao.updateStatusCus(customerId, status);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String requestVerification(int customerId) {
        try {
            DriverLicenseModel license = dao.getByCustomerId(customerId);

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

            dao.updateStatusCus(customerId, "REQUESTED");
            return "SUCCESS";

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    public List<DriverLicenseModel> getRequestedLicenses() {
        return dao.getRequestedLicenses();
    }

    public DriverLicenseModel getLicenseDetail(int licenseId) {
        return dao.getById(licenseId);
    }

    public boolean approve(int licenseId) {
        DriverLicenseModel license = dao.getById(licenseId);
        if (license == null) {
            return false;
        }

        boolean updated = dao.updateStatus(licenseId, "APPROVED");

        if (updated) {
            CustomerDAO customerDAO = new CustomerDAO();
            customerDAO.updateLicenseVerified(license.getCustomerId(), true);
        }

        return updated;
    }

    public boolean reject(int licenseId) {
        DriverLicenseModel license = dao.getById(licenseId);
        if (license == null) {
            return false;
        }

        boolean updated = dao.updateStatus(licenseId, "REJECTED");

        if (updated) {
            CustomerDAO customerDAO = new CustomerDAO();
            customerDAO.updateLicenseVerified(license.getCustomerId(), false);
        }

        return updated;
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