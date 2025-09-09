package com.appswella.wisepaise.repository;

import com.appswella.wisepaise.model.Expense;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface ExpenseRepository extends MongoRepository<Expense, String> {
    Optional<Expense> findByExpenseGroupId(String expenseGroupId);
}