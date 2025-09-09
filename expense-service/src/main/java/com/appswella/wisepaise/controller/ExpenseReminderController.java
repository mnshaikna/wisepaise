package com.appswella.wisepaise.controller;

import com.appswella.wisepaise.model.ExpenseReminder;
import com.appswella.wisepaise.service.ExpenseReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenseReminder")
public class ExpenseReminderController {
    @Autowired
    private ExpenseReminderService expenseReminderService;

    @PostMapping("/create")
    public ExpenseReminder createExpenseReminder(@RequestBody ExpenseReminder expenseReminder) {
        return expenseReminderService.createExpenseReminder(expenseReminder);
    }

    @GetMapping("/all")
    public List<ExpenseReminder> getAllExpenseReminders() {
        return expenseReminderService.getAllExpenseReminders();
    }

    @GetMapping("/user/{id}")
    public List<ExpenseReminder> getAllExpenseRemindersByUserId(@PathVariable String id) {
        return expenseReminderService.getAllExpenseRemindersByUserId(id);
    }
}