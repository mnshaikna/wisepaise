package com.appswella.wisepaise.service;

import com.appswella.wisepaise.exception.ResourceNotFoundException;
import com.appswella.wisepaise.model.ResetToken;
import com.appswella.wisepaise.model.User;
import com.appswella.wisepaise.repository.ExpenseGroupRepository;
import com.appswella.wisepaise.repository.ExpenseReminderRepository;
import com.appswella.wisepaise.repository.ExpenseRepository;
import com.appswella.wisepaise.repository.UserRepository;
import com.appswella.wisepaise.utils.PinHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
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

    @Autowired
    private ResetTokenService resetTokenService;

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

    public User sendResetPinEmail(String userId) {
        User user = userRepo.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        SecureRandom random = new SecureRandom();
        int token = random.nextInt(900000) + 100000;

        ResetToken resetToken = new ResetToken();
        resetToken.setToken(String.valueOf(token));
        resetToken.setUserId(userId);
        resetToken.setExpiresAt(new Date(System.currentTimeMillis() + 15 * 60 * 1000));

        ResetToken resetTokenResp = resetTokenService.createResetToken(resetToken);

        emailService.sendPinResetEmail(user, resetTokenResp.getToken());
        return userRepo.save(user);
    }

    public User resetDefaultPin(String token) {
        ResetToken resetToken = resetTokenService.getResetToken(token);
        if (resetToken == null) {
            throw new ResourceNotFoundException("ResetToken", "token", token);
        }

        User user = userRepo.findByUserId(resetToken.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", resetToken.getUserId()));

        String newSalt = PinHasher.generateSalt(16);
        String newHash = null;
        try {
            newHash = PinHasher.hashPin("0000", newSalt, 100000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        user.setUserPin(newHash);
        user.setUserPinSalt(newSalt);
        resetTokenService.deleteResetToken(token);
        return userRepo.save(user);
    }
}
