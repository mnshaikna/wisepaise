package com.appswella.wisepaise.controller;

import com.appswella.wisepaise.model.SavingsGoal;
import com.appswella.wisepaise.service.SavingsGoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/savingsGoal")
@Tag(name = "Savings Goal", description = "APIs to manage Savings Goals")
public class SavingsGoalController {

    @Autowired
    SavingsGoalService savingsGoalService;

    @GetMapping("/user/{savingsGoalUserId}")
    @Operation(summary = "Get all Savings Goals by UserId")
    public List<SavingsGoal> getSavingsGoalByUserId(@PathVariable String savingsGoalUserId) {
        return savingsGoalService.getAllSavingsGoalByUserId(savingsGoalUserId);
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new Savings Goal")
    public SavingsGoal createSavingsGoal(@RequestBody SavingsGoal savingsGoal) {
        return savingsGoalService.createSavingsGoal(savingsGoal);
    }

    @DeleteMapping("/{savingsGoalId}")
    @Operation(summary = "Delete an existing Savings Goal or throw error if unavailable")
    public SavingsGoal deleteSavingsGoal(@PathVariable String savingsGoalId) {
        return savingsGoalService.deleteSavingsGoal(savingsGoalId);
    }

    @PutMapping("/update")
    @Operation(summary = "Update an existing Savings Goal or throw error if unavailable")
    public SavingsGoal updateSavingsGoal(@RequestBody SavingsGoal savingsGoal) {
        return savingsGoalService.updateSavingsGoal(savingsGoal);
    }
}
