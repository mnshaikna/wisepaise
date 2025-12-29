package com.appswella.wisepaise.service;

import com.appswella.wisepaise.model.RefreshToken;
import com.appswella.wisepaise.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepo;

    public RefreshToken createRefreshToken(String userId) {
        RefreshToken token = new RefreshToken();
        token.setUserId(userId);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusSeconds(30 * 24 * 60 * 60)); // 30 days
        refreshTokenRepo.save(token);
        return token;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepo.delete(token);
            throw new RuntimeException("Refresh token expired. Please login again.");
        }
        return token;
    }
}
