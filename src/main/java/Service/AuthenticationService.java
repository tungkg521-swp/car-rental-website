package service;

import DALs.AccountDAO;
import models.AccountModel;

/**
 *
 * @author ADMIN
 */
public class AuthenticationService {

    private AccountDAO accountDAO = new AccountDAO();

    /**
     * Authenticate by email & password
     *
     * @param email
     * @param password
     * @return AccountModel nếu login thành công, null nếu thất bại
     */
    public AccountModel authenticate(String email, String password) {

        // 1. Tìm account theo email
        AccountModel account = accountDAO.findByEmail(email);
        if (account == null) {
            return null;
        }

        // 2. Check password (tạm thời plain text)
        if (!account.getPasswordHash().equals(password)) {
            return null;
        }

        // 3. Check status
        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            return null;
        }

        // 4. Login OK → trả về AccountModel
        return account;
    }
}
