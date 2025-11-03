package com.appswella.wisepaise.service;

import com.appswella.wisepaise.exception.ResourceNotFoundException;
import com.appswella.wisepaise.model.Expense;
import com.appswella.wisepaise.model.ExpenseGroup;
import com.appswella.wisepaise.repository.ExpenseGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

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
            Map<String, Double> balances = new HashMap<>();

            for (Expense expense : expenses) {
                if ("income".equalsIgnoreCase(expense.getExpenseSpendType())) {
                    totalIncome += expense.getExpenseAmount();
                } else {
                    totalExpenses += expense.getExpenseAmount();
                }

                if (expenseGroup.isExGroupShared()) {
                    double share = expense.getExpenseAmount() / expense.getExpensePaidTo().size();
                    System.out.println("expense.getExpenseAmount():::" + expense.getExpenseAmount());
                    System.out.println("expense.getExpensePaidTo().size():::" + expense.getExpensePaidTo().size());
                    System.out.println("Share:::" + share);
                    balances.put(expense.getExpensePaidBy().get("userId").toString(), balances.getOrDefault(expense.getExpensePaidBy().get("userId").toString(), 0.0) + expense.getExpenseAmount());

                    for (Map<String, Object> paidTo : expense.getExpensePaidTo()) {
                        double newBalance = Math.round((balances.getOrDefault(paidTo.get("userId").toString(), 0.0) - share) * 100.0) / 100.0;
                        balances.put(paidTo.get("userId").toString(), newBalance);
                    }
                }
            }
            System.out.println("balances:::" + balances);
            expenseGroup.setExGroupIncome(totalIncome);
            expenseGroup.setExGroupExpenses(totalExpenses);
            expenseGroup.setExpenses(expenses);

            if (expenseGroup.isExGroupShared()) {
                expenseGroup.setExGroupMembersBalance(balances);
                System.out.println("expenseGroup1:::" + expenseGroup);
                List<Map<String, Object>> settlements = simplifyDebts(balances);
                System.out.println("expenseGroup2:::" + expenseGroup);
                expenseGroup.setExGroupMembersSettlements(settlements);
            }
            System.out.println("expenseGroup3:::" + expenseGroup);
            return expenseGroupRepository.save(expenseGroup);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update expense group: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> simplifyDebts(Map<String, Double> balances) {
        // Create a copy of the balances to avoid modifying the original
        Map<String, Double> balancesCopy = new HashMap<>(balances);

        List<Map.Entry<String, Double>> debtors = balancesCopy.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .toList();
        List<Map.Entry<String, Double>> creditors = balancesCopy.entrySet().stream()
                .filter(e -> e.getValue() < 0)
                .toList();

        List<Map<String, Object>> result = new ArrayList<>();

        int i = 0, j = 0;

        while (i < debtors.size() && j < creditors.size()) {
            double debit = debtors.get(i).getValue();
            double credit = -creditors.get(j).getValue();
            double settled = Math.min(debit, credit);

            Map<String, Object> res = new HashMap<>();
            res.put("fromUserId", creditors.get(i).getKey());
            res.put("toUserId", debtors.get(j).getKey());
            res.put("amount", settled);
            result.add(res);

            // Update the copy, not the original map
            debtors.get(i).setValue(debit - settled);
            creditors.get(j).setValue(settled - credit);

            if (Math.abs(debtors.get(i).getValue()) < 0.01) i++;
            if (Math.abs(creditors.get(j).getValue()) < 0.01) j++;
        }
        return result;
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