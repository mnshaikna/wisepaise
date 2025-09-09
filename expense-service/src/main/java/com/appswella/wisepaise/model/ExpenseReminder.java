package com.appswella.wisepaise.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "expenseReminder")
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseReminder {
    @Id
    private String reminderId;
    private String reminderName;
    private String reminderDescription;
    private String reminderDate;
    private String reminderCreatedDate;
    private String reminderAmount;
    private boolean reminderActive;
    private String reminderUserId;
}
