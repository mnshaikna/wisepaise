package com.appswella.wisepaise.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "expense")

public class Expense {
    @Id
    private String expenseId;
    private String expenseTitle;
    private String expenseNote;
    private double expenseAmount;
    private String expenseSpendType;
    private String expenseDate;
    private String expenseCategory;
    private String expenseSubCategory;
    private String expensePaymentMethod;
    private String expensePaidBy;
    private List<String> expensePaidTo;
    private String expenseReceiptURL;
    private String expenseUserId;
}
