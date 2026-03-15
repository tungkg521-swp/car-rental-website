package service;

import java.util.List;

import DALs.CustomerDAO;
import DALs.DriverLicenseDAO;
import models.DriverLicenseModel;

public class DriverLicenseService {

    private DriverLicenseDAO dao = new DriverLicenseDAO();

    public DriverLicenseModel getByCustomerId(int customerId) {
        return dao.getByCustomerId(customerId);
    }

    public boolean saveOrUpdate(DriverLicenseModel dl) {

        try {
            DriverLicenseDAO dao = new DriverLicenseDAO();

            DriverLicenseModel existing
                    = dao.getByCustomerId(dl.getCustomerId());

            if (existing == null) {
                return dao.insert(dl) > 0;
            } else {
                return dao.update(dl) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
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

}
