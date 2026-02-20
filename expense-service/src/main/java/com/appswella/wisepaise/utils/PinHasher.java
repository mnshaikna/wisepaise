package com.appswella.wisepaise.utils;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class PinHasher {

    public static String generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(salt);
    }

    public static String hashPin(String pin, String salt, int iterations) throws Exception {
        byte[] saltBytes = salt.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        PBEKeySpec spec = new PBEKeySpec(pin.toCharArray(), saltBytes, iterations, 256   // 32 bytes = 256 bits
        );
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = factory.generateSecret(spec).getEncoded();

        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }
}