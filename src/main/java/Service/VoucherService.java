package service;

import java.util.List;

import DALs.VoucherDAO;
import models.VoucherModel;

public class VoucherService {

    private VoucherDAO voucherDAO = new VoucherDAO();

    // Get all vouchers
    public List<VoucherModel> getVoucher() {
        return voucherDAO.getAllVouchers();
    }

    // Get voucher by ID
    public VoucherModel getVoucherById(int voucherId) {
        return voucherDAO.findById(voucherId);
    }

    // Get voucher by code
    public VoucherModel getVoucherByCode(String code) {
        return voucherDAO.findByCode(code);
    }

    // Create new voucher
    public boolean createVoucher(VoucherModel voucher) {
        return voucherDAO.insert(voucher);
    }

    // Update existing voucher
    public boolean updateVoucher(VoucherModel voucher) {
        return voucherDAO.update(voucher);
    }

    // Delete voucher
    public boolean deleteVoucher(int voucherId) {
        return voucherDAO.delete(voucherId);
    }
}
