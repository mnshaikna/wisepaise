package com.appswella.wisepaise.controller;

import com.appswella.wisepaise.model.User;
import com.appswella.wisepaise.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> resetDefaultPin(@RequestParam("token") String token) {
        User user = userService.resetDefaultPin(token);
        String htmlText = """
                <html>
                <body style="font-family: Arial; text-align:center; margin-top:50px; background-color: #000000; color: #FFFFFF;">
                    <h2>✅ PIN Reset Successful</h2>
                    <p>User: %s</p>
                    <p>Your PIN has been reset to <b style="color:#FFD700;">0000</b></p>
                </body>
                </html>
                """.formatted(user.getUserEmail());
        return ResponseEntity.ok(htmlText);
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
