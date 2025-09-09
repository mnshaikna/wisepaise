package com.appswella.wisepaise.service;

import com.appswella.wisepaise.model.ExpenseGroup;
import com.appswella.wisepaise.repository.ExpenseGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExpenseGroupService {

    @Autowired
    private ExpenseGroupRepository expenseGroupRepository;

    public ExpenseGroup createExpenseGroup(ExpenseGroup expenseGroup) {
        return expenseGroupRepository.save(expenseGroup);
    }

    public ExpenseGroup updateExpenseGroup(ExpenseGroup expenseGroup) {
        if (expenseGroup.getExGroupId() != null && 
            expenseGroupRepository.existsById(expenseGroup.getExGroupId())) {
            return expenseGroupRepository.save(expenseGroup);
        }
        throw new RuntimeException("ExpenseGroup not found with id: " + expenseGroup.getExGroupId());
    }

    public void deleteExpenseGroup(String exGroupId) {
        expenseGroupRepository.deleteById(exGroupId);
    }

    public Optional<ExpenseGroup> getAllExpenseGroupsByUserId(String userId) {
        return expenseGroupRepository.findByExGroupOwnerId(userId);
    }
}