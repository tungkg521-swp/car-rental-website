package service;

import DALs.ProfileDAO;
import java.security.MessageDigest;

public class ProfileService {

    ProfileDAO dao = new ProfileDAO();

    public String changePassword(int account_id, String oldPassword,
            String newPassword, String confirmPassword) {

        if (!newPassword.equals(confirmPassword)) {
            return "Confirm password does not match!";
        }

        String validate = validateNewPassword(newPassword);
        if (validate != null) {
            return validate;
        }

        try {

            int result = dao.changePassword(account_id, oldPassword, newPassword);

            if (result == 1) {
                return "SUCCESS";
            } else {
                return "Email or old password is incorrect!";
            }

        } catch (Exception e) {
            return "System error!";
        }
    }

    public String validateNewPassword(String newPassword) {

        if (newPassword == null) {
            return "Password cannot be null";
        }

        if (newPassword.length() < 8 || newPassword.length() > 12) {
            return "Password must be between 8 and 12 characters";
        }

        if (!newPassword.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter";
        }

        if (!newPassword.matches(".*\\d.*")) {
            return "Password must contain at least one number";
        }

        return null;
    }
}
