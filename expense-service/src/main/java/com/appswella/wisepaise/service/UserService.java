package com.appswella.wisepaise.service;

import com.appswella.wisepaise.exception.ResourceNotFoundException;
import com.appswella.wisepaise.model.User;
import com.appswella.wisepaise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepo;

    public User createUser(User user) {
        if (userRepo.existsById(user.getId())) {
            return user;
        }
        return userRepo.save(user);
    }

    public User getUserById(String id) {
        return userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User updateUser(User user) {
        if (!userRepo.existsById(user.getId()))
            throw new ResourceNotFoundException("User", "id", user.getId());
        return userRepo.save(user);
    }

    public void deleteUser(String id) {
        if (!userRepo.existsById(id))
            throw new ResourceNotFoundException("User", "id", id);
        userRepo.deleteById(id);
    }
}
