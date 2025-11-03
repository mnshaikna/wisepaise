package com.appswella.wisepaise.repository;

import com.appswella.wisepaise.model.ExpenseGroup;
import com.appswella.wisepaise.model.SavingsGoal;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface SavingsGoalRepository extends MongoRepository<SavingsGoal, String> {

    @Query("{ 'savingsGoalUser.userId': ?0 }")
    List<SavingsGoal> findByUserId(String userId, Sort sort);

    @Query(value = "{ 'savingsGoalUser.userId': ?0 }", delete = true)
    void deleteByGroupOwnerId(String userId);

}
