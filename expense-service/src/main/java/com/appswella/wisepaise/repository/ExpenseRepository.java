package com.appswella.wisepaise.repository;

import com.appswella.wisepaise.model.Expense;
import com.appswella.wisepaise.model.ExpenseReminder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;


public interface ExpenseRepository extends MongoRepository<Expense, String> {
    @Query(value = "{ 'expenseUserId': ?0 }")
    List<Expense> findByExpenseUserId(String id);

    @Query(value = "{ 'expenseUserId': ?0 }", delete = true)
    void deleteByExpenseUserId(String userId);

}