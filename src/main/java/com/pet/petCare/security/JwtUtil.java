package com.pet.petCare.security;

import org.springframework.stereotype.Component;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {

    private final String secretKey = "secret";
    private final long expiration = 1000L * 60 * 60 * 24;

    public String generateToken(String username) {
        String payload = username + ":" + System.currentTimeMillis();
        return Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }

    public String extractUsername(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":");
            return parts[0];
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String token, String username) {
        try {
            String decodedUsername = extractUsername(token);
            return decodedUsername != null && decodedUsername.equals(username);
        } catch (Exception e) {
            return false;
        }
    }
}
