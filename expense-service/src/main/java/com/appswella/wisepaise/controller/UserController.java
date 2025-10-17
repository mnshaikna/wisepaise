package com.appswella.wisepaise.controller;

import com.appswella.wisepaise.model.User;
import com.appswella.wisepaise.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Users API")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/create")
    @Operation(summary = "Create a new User")
    public User createUser(User user) {
        return userService.createUser(user);
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Get a User by Id")
    public User getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }
    @GetMapping("/all")
    @Operation(summary = "Get all Users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/update")
    @Operation(summary = "Update a User")
    public User updateUser(User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete a User")
    public void deleteUser(String id) {
        userService.deleteUser(id);
    }
}
