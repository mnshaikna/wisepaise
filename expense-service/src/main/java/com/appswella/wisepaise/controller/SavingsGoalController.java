package com.appswella.wisepaise.controller;

import com.appswella.wisepaise.model.SavingsGoal;
import com.appswella.wisepaise.service.SavingsGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/savingsGoal")
public class SavingsGoalController {

    @Autowired
    SavingsGoalService savingsGoalService;

    @GetMapping("/user/{savingsGoalUserId}")
    public List<SavingsGoal> getSavingsGoalByUserId(@PathVariable String savingsGoalUserId) {
        return savingsGoalService.getAllSavingsGoalByUserId(savingsGoalUserId);
    }

    @PostMapping("/create")
    public SavingsGoal createSavingsGoal(@RequestBody SavingsGoal savingsGoal) {
        return savingsGoalService.createSavingsGoal(savingsGoal);
    }

    @DeleteMapping("/{savingsGoalId}")
    public SavingsGoal deleteSavingsGoal(@PathVariable String savingsGoalId) {
        return savingsGoalService.deleteSavingsGoal(savingsGoalId);
    }

    @PutMapping("/update")
    public SavingsGoal updateSavingsGoal(@RequestBody SavingsGoal savingsGoal) {
        return savingsGoalService.updateSavingsGoal(savingsGoal);
    }
}
