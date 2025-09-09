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
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve expense reminders for user: " + e.getMessage());
        }
    }
}
