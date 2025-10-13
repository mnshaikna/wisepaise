package com.appswella.wisepaise.controller;

import com.appswella.wisepaise.model.ExpenseReminder;
import com.appswella.wisepaise.service.ExpenseReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenseReminder")
@Tag(name = "Expense Reminder", description = "Expense reminder API")
public class ExpenseReminderController {
    @Autowired
    private ExpenseReminderService expenseReminderService;

    @PostMapping("/create")
    @Operation(summary = "Create a new Reminder")
    public ExpenseReminder createExpenseReminder(@RequestBody ExpenseReminder expenseReminder) {
        return expenseReminderService.createExpenseReminder(expenseReminder);
    }

    @PutMapping("/update")
    @Operation(summary = "Update an existing reminder or throw error if unavailable")
    public ExpenseReminder updateExpenseReminder(@RequestBody ExpenseReminder expenseReminder) {
        return expenseReminderService.updateExpenseReminder(expenseReminder);
    }

    @DeleteMapping("/delete/{reminderId}")
    @Operation(summary = "Delete an existing reminder or throw error if unavailable")
    public ExpenseReminder deleteExpenseReminder(@PathVariable String reminderId) {
        return expenseReminderService.deleteExpenseReminder(reminderId);
    }

    @GetMapping("/all")
    public List<ExpenseReminder> getAllExpenseReminders() {
        return expenseReminderService.getAllExpenseReminders();
    }

    @GetMapping("/user/{id}")
    @Operation(summary = "Get all Reminder by UserId")
    public List<ExpenseReminder> getAllExpenseRemindersByUserId(@PathVariable String id) {
        return expenseReminderService.getAllExpenseRemindersByUserId(id);
    }
}