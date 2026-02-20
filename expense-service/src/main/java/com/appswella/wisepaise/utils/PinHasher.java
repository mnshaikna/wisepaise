package com.appswella.wisepaise.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class PinHasher {

    public String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public String hashPin(String pin, String salt) throws Exception {

        int iterations = 100000;
        int keyLength = 256;

        PBEKeySpec spec = new PBEKeySpec(pin.toCharArray(), Base64.getDecoder().decode(salt), iterations, keyLength);

        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hash = skf.generateSecret(spec).getEncoded();

        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }
}
