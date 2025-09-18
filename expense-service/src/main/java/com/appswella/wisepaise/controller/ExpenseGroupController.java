package com.appswella.wisepaise.controller;

import com.appswella.wisepaise.model.ExpenseGroup;
import com.appswella.wisepaise.service.ExpenseGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenseGroup")
@Tag(name = "ExpenseGroup", description = "The ExpenseGroup API")
public class ExpenseGroupController {
    @Autowired
    private ExpenseGroupService expenseGroupService;

    @PostMapping("/create")
    @Operation(summary = "Create a new expenseGroup")
    public ExpenseGroup createExpenseGroup(@RequestBody ExpenseGroup expenseGroup) {
        return expenseGroupService.createExpenseGroup(expenseGroup);
    }

    @PutMapping("/update")
    @Operation(summary = "Update an existing expenseGroup")
    public ExpenseGroup updateExpenseGroup(@RequestBody ExpenseGroup expenseGroup) {
        return expenseGroupService.updateExpenseGroup(expenseGroup);
    }

    @DeleteMapping("/delete/{exGroupId}")
    @Operation(summary = "Delete an existing expenseGroup")
    public ExpenseGroup deleteExpenseGroup(@PathVariable String exGroupId) {
        return expenseGroupService.deleteExpenseGroup(exGroupId);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all expenseGroups by user id")
    public List<ExpenseGroup> getAllExpenseGroupsByUserId(@PathVariable String userId) {
        return expenseGroupService.getAllExpenseGroupsByUserId(userId);
    }
}