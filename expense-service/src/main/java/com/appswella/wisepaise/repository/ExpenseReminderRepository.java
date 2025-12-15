package com.appswella.wisepaise.repository;

import com.appswella.wisepaise.model.ExpenseReminder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ExpenseReminderRepository extends MongoRepository<ExpenseReminder, String> {
    @Query(value = "{ 'reminderUserId': ?0 }")
    List<ExpenseReminder> findByReminderUserId(String id);

    @Query(value = "{ 'reminderUserId': ?0 }", delete = true)
    void deleteByReminderUserId(String userId);
}