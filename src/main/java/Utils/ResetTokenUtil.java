package Utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class ResetTokenUtil {

    private static final String SECRET_KEY = "CarRental_Reset_Secret_2026";

    public static String generateToken(String email, String passwordHash, long expiredAtMillis) {
        String payload = email + "|" + expiredAtMillis;
        String signature = sha256(payload + "|" + passwordHash + "|" + SECRET_KEY);

        String rawToken = payload + "|" + signature;
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(rawToken.getBytes(StandardCharsets.UTF_8));
    }

    public static boolean isValidToken(String token,
                                       String email,
                                       String passwordHash) {
        try {
            String decoded = new String(
                    Base64.getUrlDecoder().decode(token),
                    StandardCharsets.UTF_8
            );

            String[] parts = decoded.split("\\|");
            if (parts.length != 3) {
                return false;
            }

            String tokenEmail = parts[0];
            long expiredAtMillis = Long.parseLong(parts[1]);
            String tokenSignature = parts[2];

            if (!tokenEmail.equalsIgnoreCase(email)) {
                return false;
            }

            if (System.currentTimeMillis() > expiredAtMillis) {
                return false;
            }

            String payload = tokenEmail + "|" + expiredAtMillis;
            String expectedSignature = sha256(payload + "|" + passwordHash + "|" + SECRET_KEY);

            return expectedSignature.equals(tokenSignature);

        } catch (Exception e) {
            return false;
        }
    }

    public static String extractEmail(String token) {
        try {
            String decoded = new String(
                    Base64.getUrlDecoder().decode(token),
                    StandardCharsets.UTF_8
            );
            String[] parts = decoded.split("\\|");
            if (parts.length != 3) {
                return null;
            }
            return parts[0];
        } catch (Exception e) {
            return null;
        }
    }

    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}