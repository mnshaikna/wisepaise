package com.appswella.wisepaise.controller;

import com.appswella.wisepaise.model.Expense;
import com.appswella.wisepaise.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expense")
@Tag(name = "Expenses", description = "APIs to manage individual Expenses. Group Expenses are NOT managed with this.")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/create")
    @Operation(summary = "Create a new expense")
    public String createExpense(@RequestBody Expense expense) {
        return expenseService.createExpense(expense).getExpenseId();
    }

    @GetMapping("/get/{expenseId}")
    @Operation(summary = "Get an expense by id")
    public Expense getExpense(@PathVariable String expenseId) {
        return expenseService.getExpenseById(expenseId);
    }

    @DeleteMapping("/delete/{expenseId}")
    @Operation(summary = "Delete an expense by id")
    public String deleteExpense(@PathVariable String expenseId) {
        return expenseService.deleteExpense(expenseId);
    }

    @PutMapping("/update")
    @Operation(summary = "Update an expense")
    public Expense updateExpense(@RequestBody Expense expense) {
        return expenseService.updateExpense(expense);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all expenses by user id")
    public List<Expense> getAllExpensesByUserId(@PathVariable String userId) {
        return expenseService.getAllExpensesByUserId(userId);

    }

}