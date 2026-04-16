package service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class MailService {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    // ĐỔI THÀNH EMAIL THẬT CỦA BẠN
    private static final String FROM_EMAIL = "hathaidat62@gmail.com";

    // ĐỔI THÀNH APP PASSWORD 16 KÝ TỰ CỦA GMAIL
    private static final String APP_PASSWORD = "eectifdlhvsbclqa";

    public static boolean sendEmail(String toEmail, String subject, String content) {
        if (toEmail == null || toEmail.isBlank()) {
            return false;
        }

        if (FROM_EMAIL.startsWith("YOUR_") || APP_PASSWORD.startsWith("YOUR_")) {
            System.out.println("MailService is not configured yet. Please update FROM_EMAIL and APP_PASSWORD in MailService.java");
            return false;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}