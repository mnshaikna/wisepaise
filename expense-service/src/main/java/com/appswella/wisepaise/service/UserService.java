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
import java.util.Optional;

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
        if (userRepo.existsByUserEmail(user.getUserEmail())) {
            User thisUser = userRepo.findByUserEmail(user.getUserEmail()).orElseThrow(() -> new ResourceNotFoundException("User", "email", user.getUserEmail()));
            if (!thisUser.isRegistered()) {
                user.setUserId(thisUser.getUserId());
                return updateUser(user);
            } else {
                return thisUser;
            }
        }
        if (user.getUserId().isEmpty()) user.setUserId(null);
        return userRepo.save(user);
    }

    public User getUserByEmailId(String emailId) {
        return userRepo.findByUserEmail(emailId).orElseThrow(() -> new ResourceNotFoundException("User", "id", emailId));
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User updateUser(User user) {
        if (!userRepo.existsById(user.getUserId())) throw new ResourceNotFoundException("User", "id", user.getUserId());
        return userRepo.save(user);
    }

    public void deleteUser(String id) {
        if (!userRepo.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }

        try {
            expenseRepo.deleteByExpenseUserId(id);
            expenseGroupRepo.deleteByGroupOwnerId(id);
            expenseReminderRepo.deleteByReminderUserId(id);
            userRepo.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user and related data: " + e.getMessage(), e);
        }
    }

    public User getUserByUserId(String userId) {
        return userRepo.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}
