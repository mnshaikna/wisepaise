package com.appswella.wisepaise.service;

import com.appswella.wisepaise.exception.ResourceNotFoundException;
import com.appswella.wisepaise.model.Expense;
import com.appswella.wisepaise.model.ExpenseGroup;
import com.appswella.wisepaise.repository.ExpenseGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseGroupService {

    @Autowired
    private ExpenseGroupRepository expenseGroupRepository;

    public ExpenseGroup createExpenseGroup(ExpenseGroup expenseGroup) {
        expenseGroup.setExGroupId(null);
        try {
            return expenseGroupRepository.save(expenseGroup);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create expense group: " + e.getMessage());
        }
    }

    public ExpenseGroup updateExpenseGroup(ExpenseGroup expenseGroup) {
        System.out.println(expenseGroup.toString());
        if (expenseGroup.getExGroupId() == null || !expenseGroupRepository.existsById(expenseGroup.getExGroupId())) {
            throw new ResourceNotFoundException("ExpenseGroup", "id", expenseGroup.getExGroupId());
        }
        try {
            List<Expense> expenses = expenseGroup.getExpenses();
            double totalExpenses = 0, totalIncome = 0;
            System.out.println("totalExpenses:::" + totalExpenses);
            System.out.println("totalIncome:::" + totalIncome);
            for (Expense expense : expenses) {
                expense.setExpenseId(null);
                if ("income".equalsIgnoreCase(expense.getExpenseSpendType())) {
                    totalIncome += expense.getExpenseAmount();
                } else {
                    totalExpenses += expense.getExpenseAmount();
                }
            }
            System.out.println("totalIncome After:::" + totalIncome);
            System.out.println("totalExpenses After:::" + totalExpenses);
            expenseGroup.setExGroupIncome(totalIncome);
            expenseGroup.setExGroupExpenses(totalExpenses);
            expenseGroup.setExpenses(expenses);
            return expenseGroupRepository.save(expenseGroup);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update expense group: " + e.getMessage());
        }
    }

    public ExpenseGroup deleteExpenseGroup(String exGroupId) {
        ExpenseGroup expenseGroup = expenseGroupRepository.findById(exGroupId).orElseThrow(() -> new ResourceNotFoundException("ExpenseGroup", "id", exGroupId));
        try {
            expenseGroupRepository.delete(expenseGroup);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete expense group: " + e.getMessage());
        }
        return expenseGroup;
    }

    public List<ExpenseGroup> getAllExpenseGroupsByUserId(String userId) {
        try {
            List<ExpenseGroup> groups = expenseGroupRepository.findByExGroupOwnerId(userId);
            System.out.println("List Size:::" + groups.size());
            if (groups.isEmpty()) {
                System.out.println("Empty List - Throwing RNF Error");
                throw new ResourceNotFoundException("No expense groups found for user with id: " + userId);
            }
            return groups;
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve expense groups for user: " + e.getMessage(), e);
        }
    }
}