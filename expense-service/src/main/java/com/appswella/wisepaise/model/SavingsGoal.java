package com.appswella.wisepaise.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Document(collection = "savingsGoal")
@NoArgsConstructor
@AllArgsConstructor
public class SavingsGoal {
    @Id
    private String savingsGoalId;
    private String savingsGoalName;
    private String savingsGoalImageUrl;
    private String savingsGoalUser;
    private double savingsGoalTargetAmount;
    private double savingsGoalCurrentAmount;
    private String savingsGoalTargetDate;
    private String savingsGoalCreatedOn;
    private List<SavingsGoalTransaction> savingsGoalTransactions;
}