package com.appswella.wisepaise.repository;

import com.appswella.wisepaise.model.ExpenseGroup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseGroupRepository extends MongoRepository<ExpenseGroup, String> {

    List<ExpenseGroup> findByExGroupOwnerId(String userId);
}