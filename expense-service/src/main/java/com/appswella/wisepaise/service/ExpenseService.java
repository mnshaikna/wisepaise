package com.appswella.wisepaise.service;

import com.appswella.wisepaise.exception.ResourceNotFoundException;
import com.appswella.wisepaise.model.Expense;
import com.appswella.wisepaise.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    public Expense createExpense(Expense expense) {
        try {
            expense.setExpenseId(null);
            return expenseRepository.save(expense);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to create expense : " + e.getMessage());
        }
    }

    public String deleteExpense(String expenseId) {
        if (!expenseRepository.existsById(expenseId)) {
            throw new ResourceNotFoundException("Expense not found with id: " + expenseId);
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

    public List<Expense> getAllExpensesByUserId(String id) {
        try {
            List<Expense> expenses = expenseRepository.findByExpenseUserId(id);
            if (expenses.isEmpty()) {
                throw new ResourceNotFoundException("Expenses", "userId", id);
            }
            return expenses;
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve expenses for user: " + e.getMessage());
        }
    }
}