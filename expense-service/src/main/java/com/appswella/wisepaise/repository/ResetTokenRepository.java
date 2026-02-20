package com.appswella.wisepaise.repository;

import com.appswella.wisepaise.model.ResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResetTokenRepository extends MongoRepository<ResetToken, String> {
    ResetToken findByToken(String token);

    ResetToken findByUserId(String userId);

    void deleteByToken(String token);

}
