package com.appswella.wisepaise.service;

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
        return expenseReminderRepository.save(expenseReminder);
    }

    public List<ExpenseReminder> getAllExpenseReminders() {
        return expenseReminderRepository.findAll();
    }

    public List<ExpenseReminder> getAllExpenseRemindersByUserId(String id) {
        return expenseReminderRepository.findByReminderUserId(id);
    }
}
