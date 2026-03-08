package DALs;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import Utils.DBContext;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.DriverLicenseModel;

public class DriverLicenseDAO extends DBContext {

    // ================= GET BY CUSTOMER =================
    public DriverLicenseModel getByCustomerId(int customerId) {

        String sql = "SELECT * FROM driver_license WHERE customer_id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                DriverLicenseModel dl = new DriverLicenseModel();

                dl.setLicenseId(rs.getInt("license_id"));
                dl.setCustomerId(customerId);
                dl.setLicenseNumber(rs.getString("license_number"));
                dl.setFullName(rs.getString("full_name"));

                // ===== DOB =====
                Date dob = rs.getDate("dob");
                if (dob != null) {
                    dl.setDob(dob.toLocalDate());
                }

                // ===== ISSUE DATE =====
                Date issue = rs.getDate("issue_date");
                if (issue != null) {
                    dl.setIssueDate(issue.toLocalDate());
                }

                // ===== EXPIRY DATE =====
                Date expiry = rs.getDate("expiry_date");
                if (expiry != null) {
                    dl.setExpiryDate(expiry.toLocalDate());
                }

                dl.setImageFront(rs.getString("image_front"));
                dl.setImageBack(rs.getString("image_back"));
                dl.setStatus(rs.getString("status"));

                return dl;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ================= INSERT =================
    public int insert(DriverLicenseModel dl) throws Exception {

        // ❌ BỎ status='PENDING'
        String sql = "INSERT INTO driver_license "
                + "(customer_id, license_number, full_name, dob, issue_date, expiry_date, image_front, image_back) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setInt(1, dl.getCustomerId());
        ps.setString(2, dl.getLicenseNumber());
        ps.setString(3, dl.getFullName());

        // ===== DOB =====
        if (dl.getDob() != null) {
            ps.setDate(4, Date.valueOf(dl.getDob()));
        } else {
            ps.setNull(4, java.sql.Types.DATE);
        }

        // ===== ISSUE DATE =====
        if (dl.getIssueDate() != null) {
            ps.setDate(5, Date.valueOf(dl.getIssueDate()));
        } else {
            ps.setNull(5, java.sql.Types.DATE);
        }

        // ===== EXPIRY DATE =====
        if (dl.getExpiryDate() != null) {
            ps.setDate(6, Date.valueOf(dl.getExpiryDate()));
        } else {
            ps.setNull(6, java.sql.Types.DATE);
        }

        ps.setString(7, dl.getImageFront());
        ps.setString(8, dl.getImageBack());

        return ps.executeUpdate();
    }

    // ================= UPDATE =================
    public int update(DriverLicenseModel dl) throws Exception {

        // ❌ BỎ status='PENDING'
        String sql = "UPDATE driver_license SET "
                + "license_number=?, full_name=?, dob=?, issue_date=?, expiry_date=?, "
                + "image_front=?, image_back=?, updated_at=GETDATE() "
                + "WHERE customer_id=?";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, dl.getLicenseNumber());
        ps.setString(2, dl.getFullName());

        // ===== DOB =====
        if (dl.getDob() != null) {
            ps.setDate(3, Date.valueOf(dl.getDob()));
        } else {
            ps.setNull(3, java.sql.Types.DATE);
        }

        // ===== ISSUE DATE =====
        if (dl.getIssueDate() != null) {
            ps.setDate(4, Date.valueOf(dl.getIssueDate()));
        } else {
            ps.setNull(4, java.sql.Types.DATE);
        }

        // ===== EXPIRY DATE =====
        if (dl.getExpiryDate() != null) {
            ps.setDate(5, Date.valueOf(dl.getExpiryDate()));
        } else {
            ps.setNull(5, java.sql.Types.DATE);
        }

        ps.setString(6, dl.getImageFront());
        ps.setString(7, dl.getImageBack());
        ps.setInt(8, dl.getCustomerId());

        return ps.executeUpdate();
    }

    // ================= UPDATE STATUS (NEW) =================
    public void updateStatusCus(int customerId, String status) throws Exception {

        String sql = "UPDATE driver_license "
                + "SET status=?, updated_at=GETDATE() "
                + "WHERE customer_id=?";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, status);
        ps.setInt(2, customerId);

        ps.executeUpdate();
    }
    
    // 1) List all REQUESTED
    public List<DriverLicenseModel> getRequestedLicenses() {

    List<DriverLicenseModel> list = new ArrayList<>();

    String sql = "SELECT license_id, customer_id, license_number, full_name, status, created_at "
            + "FROM driver_license "
            + "WHERE status = 'REQUESTED' "
            + "ORDER BY created_at DESC";

    try {
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            DriverLicenseModel dl = new DriverLicenseModel();
            dl.setLicenseId(rs.getInt("license_id"));
            dl.setCustomerId(rs.getInt("customer_id"));
            dl.setLicenseNumber(rs.getString("license_number"));
            dl.setFullName(rs.getString("full_name"));
            dl.setStatus(rs.getString("status"));
            java.sql.Timestamp ts = rs.getTimestamp("created_at");
if (ts != null) {
    dl.setCreatedAt(ts.toLocalDateTime());
}

            list.add(dl);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

    // 2) Get by id (detail)
    // 2) Get by id (detail)
    public DriverLicenseModel getById(int licenseId) {

    String sql = "SELECT * FROM driver_license WHERE license_id = ?";

    try {

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, licenseId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

            DriverLicenseModel dl = new DriverLicenseModel();

            dl.setLicenseId(rs.getInt("license_id"));
            dl.setCustomerId(rs.getInt("customer_id"));
            dl.setLicenseNumber(rs.getString("license_number"));
            dl.setFullName(rs.getString("full_name"));

            Date dob = rs.getDate("dob");
            if (dob != null) {
                dl.setDob(dob.toLocalDate());
            }

            Date issue = rs.getDate("issue_date");
            if (issue != null) {
                dl.setIssueDate(issue.toLocalDate());
            }

            Date expiry = rs.getDate("expiry_date");
            if (expiry != null) {
                dl.setExpiryDate(expiry.toLocalDate());
            }

            dl.setImageFront(rs.getString("image_front"));
            dl.setImageBack(rs.getString("image_back"));
            dl.setStatus(rs.getString("status"));
            java.sql.Timestamp created = rs.getTimestamp("created_at");
if (created != null) {
    dl.setCreatedAt(created.toLocalDateTime());
}

java.sql.Timestamp updated = rs.getTimestamp("updated_at");
if (updated != null) {
    dl.setUpdatedAt(updated.toLocalDateTime());
}

            return dl;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}

    // 3) Update status (Approve / Reject)
    public boolean updateStatus(int licenseId, String status) {
        String sql = """
            UPDATE driver_license
            SET status = ?, updated_at = GETDATE()
            WHERE license_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, licenseId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}