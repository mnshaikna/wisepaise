package com.appswella.wisepaise.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingsGoalTransaction {
    private String savingsGoalTrxId;
    private String savingsGoalTrxName;
    private double savingsGoalTrxAmount;
    private String savingsGoalTrxCreatedOn;
}