package com.appswella.wisepaise.repository;

import com.appswella.wisepaise.model.SavingsGoal;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingsGoalRepository extends MongoRepository<SavingsGoal, String> {

    @Query("{ 'savingsGoalUser': ?0 }")
    List<SavingsGoal> findByUserId(String userId, Sort sort);

    @Query(value = "{ 'savingsGoalUser': ?0 }", delete = true)
    void deleteByGroupOwnerId(String userId);

}
