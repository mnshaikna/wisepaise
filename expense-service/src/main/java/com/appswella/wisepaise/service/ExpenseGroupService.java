package com.appswella.wisepaise.service;

import com.appswella.wisepaise.exception.ResourceNotFoundException;
import com.appswella.wisepaise.model.Expense;
import com.appswella.wisepaise.model.ExpenseGroup;
import com.appswella.wisepaise.repository.ExpenseGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        Double totalExpenses = 0.0, totalIncome = 0.0;
        Map<String, BigDecimal> balances = new HashMap<>();


        for (Expense expense : expenses) {
            boolean isSettlement = expense.isSettlement();
            if ("income".equalsIgnoreCase(expense.getExpenseSpendType())) {
                totalIncome += expense.getExpenseAmount();
            } else {
                if (!isSettlement) {
                    totalExpenses += expense.getExpenseAmount();
                }
            }

            if (expenseGroup.isExGroupShared()) {
                BigDecimal share = BigDecimal.valueOf(expense.getExpenseAmount()).divide(
                        BigDecimal.valueOf(expense.getExpensePaidTo().size()),
                        2,
                        RoundingMode.HALF_UP
                );
                System.out.println("share:::" + share);
                balances.put(expense.getExpensePaidBy(), balances.getOrDefault(expense.getExpensePaidBy(), BigDecimal.ZERO).add(BigDecimal.valueOf(expense.getExpenseAmount())));


                for (String paidTo : expense.getExpensePaidTo()) {
                    BigDecimal newBalance = (balances.getOrDefault(paidTo, BigDecimal.ZERO).subtract(share));
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

    private List<Map<String, Object>> simplifyDebts(Map<String, BigDecimal> balances) {
        // Create a list of debtors (negative balance) and creditors (positive balance)
        List<Map.Entry<String, BigDecimal>> creditors = new ArrayList<>();
        List<Map.Entry<String, BigDecimal>> debtors = new ArrayList<>();

        for (var e : balances.entrySet()) {
            int cmp = e.getValue().compareTo(BigDecimal.ZERO);
            if (cmp > 0) creditors.add(new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue()));
            else if (cmp < 0) debtors.add(new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue().abs()));
            // store positive values for easier math
        }

        List<Map<String, Object>> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < creditors.size() && j < debtors.size()) {

            BigDecimal amountCreditorShouldReceive = creditors.get(i).getValue();
            BigDecimal amountDebtorShouldPay = debtors.get(j).getValue();

            BigDecimal settled = amountCreditorShouldReceive.min(amountDebtorShouldPay);

            // debtor pays creditor
            Map<String, Object> settlement = new HashMap<>();
            settlement.put("fromUserId", debtors.get(j).getKey());
            settlement.put("toUserId", creditors.get(i).getKey());
            settlement.put("amount", settled);
            result.add(settlement);

            // Update balances
            creditors.get(i).setValue(amountCreditorShouldReceive.subtract(settled));
            debtors.get(j).setValue(amountDebtorShouldPay.subtract(settled));

            // Move pointers
            if (creditors.get(i).getValue().compareTo(BigDecimal.ZERO) == 0) i++;
            if (debtors.get(j).getValue().compareTo(BigDecimal.ZERO) == 0) j++;
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