package com.appswella.wisepaise.service;

import com.appswella.wisepaise.exception.ResourceNotFoundException;
import com.appswella.wisepaise.model.Expense;
import com.appswella.wisepaise.model.ExpenseGroup;
import com.appswella.wisepaise.repository.ExpenseGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
                // expense.setExpenseId(null);
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
            Sort sort = Sort.by(Sort.Direction.DESC, "exGroupCreatedOn");

            // Groups where user is the owner
            List<ExpenseGroup> ownedGroups = expenseGroupRepository.findByGroupOwnerId(userId, sort);
            // Groups where user is a member
            List<ExpenseGroup> memberGroups = expenseGroupRepository.findByMemberUserId(userId, sort);

            //Combine both lists
            Set<ExpenseGroup> allGroupsSet = new LinkedHashSet<>();
            allGroupsSet.addAll(ownedGroups);
            allGroupsSet.addAll(memberGroups);

            List<ExpenseGroup> allGroups = new ArrayList<>(allGroupsSet);// avoids duplicates

            if (allGroups.isEmpty()) {
                throw new ResourceNotFoundException("No expense groups found for user with id: " + userId);
            }
            return allGroups;
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve expense groups for user: " + e.getMessage(), e);
        }
    }
}