package com.appswella.wisepaise.repository;

import com.appswella.wisepaise.model.ExpenseReminder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExpenseReminderRepository extends MongoRepository<ExpenseReminder, String> {

    List<ExpenseReminder> findByReminderUserId(String id);
}