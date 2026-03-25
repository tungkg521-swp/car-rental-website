/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DALs;



import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import models.PaymentModel;

public class PaymentDAO extends DBContext {

    public boolean insert(PaymentModel payment) {
        String sql = """
            INSERT INTO payment
            (
                booking_id,
                amount,
                payment_type,
                payment_method,
                payment_status,
                gateway_transaction_id,
                gateway_order_ref,
                provider_response_code,
                callback_received_at,
                checksum_verified,
                paid_at,
                raw_response
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, payment.getBookingId());
            ps.setBigDecimal(2, payment.getAmount());
            ps.setString(3, payment.getPaymentType());
            ps.setString(4, payment.getPaymentMethod());
            ps.setString(5, payment.getPaymentStatus());
            ps.setString(6, payment.getGatewayTransactionId());
            ps.setString(7, payment.getGatewayOrderRef());
            ps.setString(8, payment.getProviderResponseCode());
            ps.setTimestamp(9, payment.getCallbackReceivedAt());
            ps.setBoolean(10, payment.isChecksumVerified());
            ps.setTimestamp(11, payment.getPaidAt());
            ps.setString(12, payment.getRawResponse());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean hasSuccessfulDepositPayment(int bookingId) {
        String sql = """
            SELECT 1
            FROM payment
            WHERE booking_id = ?
              AND payment_type = 'DEPOSIT'
              AND payment_status = 'SUCCESS'
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}