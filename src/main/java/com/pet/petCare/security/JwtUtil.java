package com.pet.petCare.security;

import org.springframework.stereotype.Component;
import java.util.Base64;

@Component
public class JwtUtil {

    private final String secretKey = "secret";
    private final long expiration = 1000L * 60 * 60 * 24;

    public String generateToken(String username) {
        String payload = username + ":" + System.currentTimeMillis();
        return Base64.getEncoder().encodeToString(payload.getBytes());
    }
}
