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
@Tag(name = "Users", description = "APIs to manage Users")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/create")
    @Operation(summary = "Create a new User")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/get/emailId/{emailId}")
    @Operation(summary = "Get a User by EmailId")
    public User getUserByEmailId(@PathVariable String emailId) {
        return userService.getUserByEmailId(emailId);
    }

    @GetMapping("/resetPin/{userId}")
    @Operation(summary = "Send Email with Reset User PIN Link")
    public User resetPin(@PathVariable String userId) {
        return userService.sendResetPinEmail(userId);
    }

    @GetMapping("/reset-pin")
    @Operation(summary = "Reset User PIN to Default")
    public User resetDefaultPin(@RequestParam("token") String token) {
        return userService.resetDefaultPin(token);
    }

    @GetMapping("/get/userId/{userId}")
    @Operation(summary = "Get a User by userId")
    public User getUserByUserId(@PathVariable String userId) {
        return userService.getUserByUserId(userId);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all Users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/update")
    @Operation(summary = "Update a User")
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete a User")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }

    @PostMapping("/friends")
    public List<User> getFriends(@RequestBody List<String> userIdList) {
        return userService.getFriends(userIdList);
    }
}
