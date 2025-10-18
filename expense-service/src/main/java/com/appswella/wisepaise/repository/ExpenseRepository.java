package com.appswella.wisepaise.repository;

import com.appswella.wisepaise.model.Expense;
import com.appswella.wisepaise.model.ExpenseReminder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;


public interface ExpenseRepository extends MongoRepository<Expense, String> {
    List<Expense> findByExpenseUserId(String id);
    void deleteByExpenseUserId(String userId);

}