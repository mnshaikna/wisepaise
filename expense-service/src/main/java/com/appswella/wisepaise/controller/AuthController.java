package com.appswella.wisepaise.controller;

import com.appswella.wisepaise.model.RefreshToken;
import com.appswella.wisepaise.model.User;
import com.appswella.wisepaise.repository.RefreshTokenRepository;
import com.appswella.wisepaise.repository.UserRepository;
import com.appswella.wisepaise.service.RefreshTokenService;
import com.appswella.wisepaise.utils.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepo;

    @Autowired
    private GoogleIdTokenVerifier googleIdTokenVerifier;

    @GetMapping("/create")
    public Map<String, String> login(@RequestHeader("Authorization") String authHeader) throws GeneralSecurityException, IOException {
        String idToken = authHeader.replace("Bearer ", "");

        GoogleIdToken idTokenObj = googleIdTokenVerifier.verify(idToken);
        if (idTokenObj == null) throw new RuntimeException("Invalid Google idToken");

        GoogleIdToken.Payload payload = idTokenObj.getPayload();
        String googleId = payload.getSubject(); // Unique Google user ID
        String googleEmail = payload.getEmail();

        User user = userRepo.findByUserEmail(googleEmail).orElseGet(() -> {
            User newUser = new User();
            newUser.setUserGoogleId(googleId);
            newUser.setUserName((String) payload.get("name"));
            newUser.setUserEmail(googleEmail);
            newUser.setUserCreatedOn(Instant.now().toString());
            newUser.setRegistered(true);
            return userRepo.save(newUser);
        });

        String accessToken = jwtUtil.generateToken(user.getUserId(), user.getUserEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUserId());

        return Map.of("accessToken", accessToken, "refreshToken", refreshToken.getToken());
    }


    // Refresh endpoint
    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestBody Map<String, String> request) {
        String requestToken = request.get("refreshToken");
        RefreshToken refreshToken = refreshTokenRepo.findByToken(requestToken).orElseThrow(() -> new RuntimeException("Refresh token not found"));

        refreshTokenService.verifyExpiration(refreshToken);

        User user = userRepo.findById(refreshToken.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtUtil.generateToken(user.getUserId(), user.getUserEmail());
        return Map.of("accessToken", newAccessToken);
    }
}
