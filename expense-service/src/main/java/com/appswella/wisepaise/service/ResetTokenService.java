package com.appswella.wisepaise.service;

import com.appswella.wisepaise.model.ResetToken;
import com.appswella.wisepaise.repository.ResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class ResetTokenService {

    @Autowired
    private ResetTokenRepository resetTokenRepository;

    public ResetToken createResetToken(ResetToken resetToken) {
        return resetTokenRepository.save(resetToken);
    }

    public ResetToken getResetToken(String token) {
        return resetTokenRepository.findByToken(token);
    }

    public void deleteResetToken(String token) {
        resetTokenRepository.deleteByToken(token);
    }
}
