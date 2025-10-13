package com.appswella.wisepaise.service;

import com.appswella.wisepaise.exception.ResourceNotFoundException;
import com.appswella.wisepaise.model.ExpenseReminder;
import com.appswella.wisepaise.repository.ExpenseReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseReminderService {

    @Autowired
    private ExpenseReminderRepository expenseReminderRepository;

    public ExpenseReminder createExpenseReminder(ExpenseReminder expenseReminder) {
        try {
            expenseReminder.setReminderId(null);
            return expenseReminderRepository.save(expenseReminder);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create expense reminder: " + e.getMessage());
        }
    }

    public List<ExpenseReminder> getAllExpenseReminders() {
        try {
            List<ExpenseReminder> reminders = expenseReminderRepository.findAll();
            if (reminders.isEmpty()) {
                throw new ResourceNotFoundException("No expense reminders found");
            }
            return reminders;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve expense reminders: " + e.getMessage());
        }
    }

    public List<ExpenseReminder> getAllExpenseRemindersByUserId(String id) {
        try {
            List<ExpenseReminder> reminders = expenseReminderRepository.findByReminderUserId(id);
            if (reminders.isEmpty()) {
                throw new ResourceNotFoundException("ExpenseReminder", "userId", id);
            }
            return reminders;
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve expense reminders for user: " + e.getMessage());
        }
    }

    public ExpenseReminder deleteExpenseReminder(String reminderId) {
        try {
            ExpenseReminder expenseReminder = expenseReminderRepository.findById(reminderId).orElseThrow(() -> new ResourceNotFoundException("ExpenseReminder", "id", reminderId));
            expenseReminderRepository.delete(expenseReminder);
            return expenseReminder;
        } catch (ResourceNotFoundException ex) {
            throw ex;
        }
    }

    public ExpenseReminder updateExpenseReminder(ExpenseReminder expenseReminder) {
        expenseReminderRepository.findById(expenseReminder.getReminderId()).orElseThrow(() -> new ResourceNotFoundException("ExpenseReminder", "id", expenseReminder.getReminderId()));

        return expenseReminderRepository.save(expenseReminder);
    }
}
