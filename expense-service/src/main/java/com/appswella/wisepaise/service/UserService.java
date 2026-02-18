package com.appswella.wisepaise.service;

import com.appswella.wisepaise.exception.ResourceNotFoundException;
import com.appswella.wisepaise.model.User;
import com.appswella.wisepaise.repository.ExpenseGroupRepository;
import com.appswella.wisepaise.repository.ExpenseReminderRepository;
import com.appswella.wisepaise.repository.ExpenseRepository;
import com.appswella.wisepaise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Autowired
    private EmailService emailService;

    public User createUser(User user) {
        if (userRepo.existsByUserEmail(user.getUserEmail())) {
            User thisUser = userRepo.findByUserEmail(user.getUserEmail()).orElseThrow(() -> new ResourceNotFoundException("User", "email", user.getUserEmail()));
            if (user.getUserContacts().isEmpty()) {
                user.setUserContacts(thisUser.getUserContacts());
            }
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
        if (!userRepo.existsByUserEmail(user.getUserEmail()))
            throw new ResourceNotFoundException("User", "email", user.getUserEmail());

        User thisUser = userRepo.findByUserEmail(user.getUserEmail()).orElseThrow(() -> new ResourceNotFoundException("User", "email", user.getUserEmail()));

        if (thisUser.getUserContacts() != null && !thisUser.getUserContacts().isEmpty()) {
            List<String> thisUserContact = thisUser.getUserContacts();
            List<String> userContact = user.getUserContacts();

            Set<String> merged = new LinkedHashSet<>();
            merged.addAll(thisUserContact);
            merged.addAll(userContact);

            List<String> mergedList = new ArrayList<>(merged);

            user.setUserContacts(mergedList);
        }

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

    public List<User> getFriends(List<String> userIdList) {
        if (userIdList == null || userIdList.isEmpty()) {
            return Collections.emptyList();
        }
        return userRepo.findByUserIdIn(userIdList);
    }

    public User resetPin(String userId) {
        User user = userRepo.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        emailService.sendPinResetEmail(user.getUserEmail(), "http://localhost:8082/reset-pin?userId=" + userId);
        return userRepo.save(user);
    }
}
