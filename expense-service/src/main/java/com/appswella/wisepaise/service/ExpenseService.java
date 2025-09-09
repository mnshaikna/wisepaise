package com.appswella.wisepaise.service;

import com.appswella.wisepaise.model.Expense;
import com.appswella.wisepaise.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public String deleteExpense(String expenseId) {
        if (!expenseRepository.existsById(expenseId)) {
            throw new RuntimeException("Expense not found with id: " + expenseId);
        }
        expenseRepository.deleteById(expenseId);
        return expenseId;
    }

    public Expense updateExpense(Expense expense) {

        if (!expenseRepository.existsById(expense.getExpenseId())) {
            throw new RuntimeException("Expense not found with id: " + expense.getExpenseId());
        }
        return expenseRepository.save(expense);

    }
    public Expense getExpenseById(String expenseId) {
        return expenseRepository.findById(expenseId).orElse(null);
    }

    public Optional<Expense> getExpensesByExGroupId(String expenseGroupId) {
        return expenseRepository.findByExpenseGroupId(expenseGroupId);
    }
}