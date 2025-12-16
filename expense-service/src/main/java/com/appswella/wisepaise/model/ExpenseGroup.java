package com.appswella.wisepaise.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "expenseGroup")
public class ExpenseGroup {
    @Id
    private String exGroupId;
    private String exGroupName;
    private String exGroupDesc;
    private String exGroupType;
    private String exGroupImageURL;
    private String exGroupCreatedOn;
    private boolean exGroupShared;
    private List<String> exGroupMembers;
    private List<Expense> expenses;
    private String exGroupOwnerId;
    private BigDecimal exGroupIncome;
    private BigDecimal exGroupExpenses;
    private Map<String, BigDecimal> exGroupMembersBalance;
    private List<Map<String, Object>> exGroupMembersSettlements;
}