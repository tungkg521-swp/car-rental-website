/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;



import DALs.BookingDAO;
import DALs.CarDAO;
import DALs.PaymentDAO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import models.BookingModel;
import models.PaymentModel;

public class PaymentService {

    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final CarDAO carDAO = new CarDAO();
    private final VoucherService voucherService = new VoucherService();

    public BigDecimal calculateDeposit(BigDecimal totalAmount) {
        if (totalAmount == null) {
            return BigDecimal.ZERO;
        }

        return totalAmount.multiply(new BigDecimal("0.30"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateRemaining(BigDecimal totalAmount) {
        if (totalAmount == null) {
            return BigDecimal.ZERO;
        }

        return totalAmount.subtract(calculateDeposit(totalAmount))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public boolean processSandboxPayment(int bookingId, String paymentMethod, boolean success) {
        BookingModel booking = bookingDAO.getById(bookingId);

        if (booking == null) {
            return false;
        }

        if (!"PENDING_PAYMENT".equalsIgnoreCase(booking.getStatus())) {
            return false;
        }

        if (paymentDAO.hasSuccessfulDepositPayment(bookingId)) {
            return false;
        }

        BigDecimal totalAmount = booking.getTotalEstimatedPrice();
        BigDecimal depositAmount = calculateDeposit(totalAmount);
        Timestamp now = new Timestamp(System.currentTimeMillis());

        PaymentModel payment = new PaymentModel();
        payment.setBookingId(bookingId);
        payment.setAmount(depositAmount);
        payment.setPaymentType("DEPOSIT");
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus(success ? "SUCCESS" : "FAILED");
        payment.setGatewayOrderRef("SBX-BOOKING-" + bookingId + "-" + System.currentTimeMillis());
        payment.setGatewayTransactionId(success ? "TXN-" + System.currentTimeMillis() : null);
        payment.setProviderResponseCode(success ? "00" : "99");
        payment.setCallbackReceivedAt(now);
        payment.setChecksumVerified(true);
        payment.setPaidAt(success ? now : null);
        payment.setRawResponse(success
                ? "{status:'SUCCESS',message:'Sandbox payment success'}"
                : "{status:'FAILED',message:'Sandbox payment failed'}");

        boolean inserted = paymentDAO.insert(payment);
        if (!inserted) {
            return false;
        }

        if (success) {
            boolean updated = bookingDAO.updateStatus(bookingId, "DEPOSIT_PAID");

            if (updated && booking.getVoucherId() != null) {
                voucherService.updateVoucherQuantity(booking.getVoucherId());
            }

            return updated;
        } else {
            bookingDAO.updateStatus(bookingId, "CANCELLED");
            carDAO.updateStatus(booking.getCarId(), "AVAILABLE");
            return true;
        }
    }
}