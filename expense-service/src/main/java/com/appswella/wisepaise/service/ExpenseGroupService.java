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
                System.out.println("share:::" + share);
                balances.put(expense.getExpensePaidBy(), balances.getOrDefault(expense.getExpensePaidBy(), 0.0) + expense.getExpenseAmount());


                for (String paidTo : expense.getExpensePaidTo()) {
                    double newBalance = Math.round(balances.getOrDefault(paidTo, 0.0) - share);
                    balances.put(paidTo, newBalance);
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
    }

    private List<Map<String, Object>> simplifyDebts(Map<String, Double> balances) {
        // Create a list of debtors (negative balance) and creditors (positive balance)
        List<Map.Entry<String, Double>> creditors = new ArrayList<>();
        List<Map.Entry<String, Double>> debtors = new ArrayList<>();

        for (var e : balances.entrySet()) {
            if (e.getValue() > 0) creditors.add(new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue()));
            else if (e.getValue() < 0) debtors.add(new AbstractMap.SimpleEntry<>(e.getKey(), -e.getValue()));
            // store positive values for easier math
        }

        List<Map<String, Object>> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < creditors.size() && j < debtors.size()) {

            double amountCreditorShouldReceive = creditors.get(i).getValue();
            double amountDebtorShouldPay = debtors.get(j).getValue();

            double settled = Math.min(amountCreditorShouldReceive, amountDebtorShouldPay);

            // debtor pays creditor
            Map<String, Object> settlement = new HashMap<>();
            settlement.put("fromUserId", debtors.get(j).getKey());
            settlement.put("toUserId", creditors.get(i).getKey());
            settlement.put("amount", settled);
            result.add(settlement);

            // Update balances
            creditors.get(i).setValue(amountCreditorShouldReceive - settled);
            debtors.get(j).setValue(amountDebtorShouldPay - settled);

            // Move pointers
            if (creditors.get(i).getValue() < 0.01) i++;
            if (debtors.get(j).getValue() < 0.01) j++;
        }

        System.out.println("results:::" + result.toString());

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