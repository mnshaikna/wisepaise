package com.appswella.wisepaise.repository;

import com.appswella.wisepaise.model.Expense;
import com.appswella.wisepaise.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUserEmail(String emailId);

    Optional<User> findByUserId(String userId);
}
