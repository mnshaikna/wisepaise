package com.appswella.wisepaise.repository;

import com.appswella.wisepaise.model.ExpenseGroup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;

import java.util.List;

@Repository
public interface ExpenseGroupRepository extends MongoRepository<ExpenseGroup, String> {

    @Query("{ 'exGroupOwnerId': ?0 }")
    List<ExpenseGroup> findByGroupOwnerId(String userId, Sort sort);

    @Query(value = "{ 'exGroupOwnerId': ?0 }", delete = true)
    void deleteByGroupOwnerId(String userId);

    @Query("{ 'exGroupMembers': ?0 }")
    List<ExpenseGroup> findByMemberUserId(String userId, Sort sort);
}