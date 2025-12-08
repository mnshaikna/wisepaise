package com.appswella.wisepaise.service;

import com.appswella.wisepaise.exception.ResourceNotFoundException;
import com.appswella.wisepaise.model.Expense;
import com.appswella.wisepaise.model.ExpenseGroup;
import com.appswella.wisepaise.model.SavingsGoal;
import com.appswella.wisepaise.model.SavingsGoalTransaction;
import com.appswella.wisepaise.repository.ExpenseGroupRepository;
import com.appswella.wisepaise.repository.SavingsGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SavingsGoalService {

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    public SavingsGoal createSavingsGoal(SavingsGoal savingsGoal) {
        savingsGoal.setSavingsGoalId(null);
        if (savingsGoal.getSavingsGoalCurrentAmount() > 0) {
            Random rand = new Random();
            SavingsGoalTransaction initialTrx = new SavingsGoalTransaction();
            initialTrx.setSavingsGoalTrxId(Integer.toString(rand.nextInt(1000000)));
            initialTrx.setSavingsGoalTrxName("Initial Savings");
            initialTrx.setSavingsGoalTrxAmount(savingsGoal.getSavingsGoalCurrentAmount());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(new Date());
            initialTrx.setSavingsGoalTrxCreatedOn(dateStr);

            List<SavingsGoalTransaction> trxList;
            trxList = savingsGoal.getSavingsGoalTransactions();
            trxList.add(initialTrx);
            savingsGoal.setSavingsGoalTransactions(trxList);
        }
        try {
            return savingsGoalRepository.save(savingsGoal);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Savings Goal: " + e.getMessage());
        }
    }

    public SavingsGoal updateSavingsGoal(SavingsGoal savingsGoal) {
        System.out.println(savingsGoal.toString());
        if (savingsGoal.getSavingsGoalId() == null || !savingsGoalRepository.existsById(savingsGoal.getSavingsGoalId())) {
            throw new ResourceNotFoundException("SavingsGoal", "id", savingsGoal.getSavingsGoalId());
        }
        try {
            List<SavingsGoalTransaction> transactions = savingsGoal.getSavingsGoalTransactions();
            double totalSavings = 0;

            for (SavingsGoalTransaction transaction : transactions) {

                totalSavings += transaction.getSavingsGoalTrxAmount();

            }
            savingsGoal.setSavingsGoalCurrentAmount(totalSavings);
            savingsGoal.setSavingsGoalTransactions(transactions);

            return savingsGoalRepository.save(savingsGoal);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update Savings Goal: " + e.getMessage());
        }
    }

    public SavingsGoal deleteSavingsGoal(String savingsGoalId) {
        SavingsGoal savingsGoal = savingsGoalRepository.findById(savingsGoalId).orElseThrow(() -> new ResourceNotFoundException("SavingsGoal", "id", savingsGoalId));
        try {
            savingsGoalRepository.delete(savingsGoal);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete savingsGoal: " + e.getMessage());
        }
        return savingsGoal;
    }

    public List<SavingsGoal> getAllSavingsGoalByUserId(String savingsGoalUserId) {
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "savingsGoalCreatedOn");

            List<SavingsGoal> ownedGoals = savingsGoalRepository.findByUserId(savingsGoalUserId, sort);
            System.out.println("ownedGoals:::" + ownedGoals.size());

            if (ownedGoals.isEmpty()) {
                throw new ResourceNotFoundException("No Saving Goals found for user with id: " + savingsGoalUserId);
            }
            return ownedGoals;
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve Savings Goal for user: " + e.getMessage(), e);
        }
    }
}