package com.appswella.wisepaise.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "expenseGroup")
public class ExpenseGroup
{
    @Id
    private String exGroupId;
    private String exGroupName;
    private String exGroupDesc;
    private String exGroupType;
    private String exGroupImageURL;
    private String exGroupCreatedOn;
    private boolean exGroupShared;
    private List<User> exGroupMembers;
    private List<Expense> expenses;
    private String exGroupOwnerId;
    private double exGroupIncome;
    private double exGroupExpenses;
}