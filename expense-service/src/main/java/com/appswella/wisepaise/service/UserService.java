package com.appswella.wisepaise.service;

import com.appswella.wisepaise.exception.ResourceNotFoundException;
import com.appswella.wisepaise.model.User;
import com.appswella.wisepaise.repository.ExpenseGroupRepository;
import com.appswella.wisepaise.repository.ExpenseReminderRepository;
import com.appswella.wisepaise.repository.ExpenseRepository;
import com.appswella.wisepaise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ExpenseRepository expenseRepo;

    @Autowired
    private ExpenseGroupRepository expenseGroupRepo;

    @Autowired
    private ExpenseReminderRepository expenseReminderRepo;

    public User createUser(User user) {
        System.out.println("UserData:::" + user.toString());
        if (userRepo.existsById(user.getUserId())) {
            System.out.println("User Exists");
            return user;
        }
        System.out.println("User does NOT Exist");
        return userRepo.save(user);
    }

    public User getUserById(String id) {
        return userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User updateUser(User user) {
        if (!userRepo.existsById(user.getUserId()))
            throw new ResourceNotFoundException("User", "id", user.getUserId());
        return userRepo.save(user);
    }

    public void deleteUser(String id) {
        if (!userRepo.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }

        try {
            expenseRepo.deleteByExpenseUserId(id);
            expenseGroupRepo.deleteByExGroupOwnerId(id);
            expenseReminderRepo.deleteByReminderUserId(id);
            userRepo.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user and related data: " + e.getMessage(), e);
        }
    }
}
