package Utils;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class MailUtil {

    // Đổi theo email thật của bạn
    private static final String FROM_EMAIL = "your_email@gmail.com";
    private static final String APP_PASSWORD = "your_app_password";

    private MailUtil() {
    }

    public static void sendOtpEmail(String toEmail, String otp) throws MessagingException {
        String subject = "Password Reset OTP";
        String content = buildOtpHtml(otp);

        sendEmail(toEmail, subject, content);
    }

    public static void sendEmail(String toEmail, String subject, String htmlContent) throws MessagingException {
        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setContent(htmlContent, "text/html; charset=UTF-8");

        Transport.send(message);
    }

    private static String buildOtpHtml(String otp) {
        return "<div style='font-family: Arial, sans-serif; line-height: 1.6;'>"
                + "<h2>Password Reset Request</h2>"
                + "<p>Your OTP code is:</p>"
                + "<h1 style='color: #2563eb; letter-spacing: 4px;'>" + otp + "</h1>"
                + "<p>This OTP will expire in 5 minutes.</p>"
                + "<p>If you did not request a password reset, please ignore this email.</p>"
                + "</div>";
    }
}