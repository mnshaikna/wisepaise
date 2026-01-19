package com.appswella.wisepaise.controller;

import com.appswella.wisepaise.model.RefreshToken;
import com.appswella.wisepaise.model.User;
import com.appswella.wisepaise.repository.RefreshTokenRepository;
import com.appswella.wisepaise.repository.UserRepository;
import com.appswella.wisepaise.service.RefreshTokenService;
import com.appswella.wisepaise.utils.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/auth")
public class AuthController {

    Logger log = Logger.getLogger(AuthController.class.getName());

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


    private static final List<String> TRUSTED_CLIENT_IDS = Arrays.asList(
            "460722747757-jpgf1n64l285aq1p3iogl2uovttvppll.apps.googleusercontent.com",
            "460722747757-e0aprsdltc1fe9p4dvtbthp96uo7jrle.apps.googleusercontent.com",
            "460722747757-h3a5jtfr6k9e7eku6jmoblmej460bv4a.apps.googleusercontent.com"
    );

    @GetMapping("/create")
    public Map<String, String> login(@RequestHeader("Authorization") String authHeader) throws GeneralSecurityException, IOException {
        String idToken = authHeader.replace("Bearer ", "");


        log.info("Processing idToken: " + idToken);

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(TRUSTED_CLIENT_IDS)
                .build();

        GoogleIdToken idTokenObj = verifier.verify(idToken);
        if (idTokenObj == null) throw new RuntimeException("Invalid Google idToken");

        log.info("idTokenObj: " + idTokenObj);

        GoogleIdToken.Payload payload = idTokenObj.getPayload();
        String googleId = payload.getSubject(); // Unique Google user ID
        String googleEmail = payload.getEmail();

        log.info("googleId: " + googleId);
        log.info("googleEmail: " + googleEmail);

        User user = userRepo.findByUserEmail(googleEmail).orElseGet(() -> {
            User newUser = new User();
            newUser.setUserGoogleId(googleId);
            newUser.setUserName((String) payload.get("name"));
            newUser.setUserEmail(googleEmail);
            newUser.setUserCreatedOn(Instant.now().toString());
            newUser.setRegistered(true);
            return userRepo.save(newUser);
        });

        log.info("user: " + user.getUserName());

        String accessToken = jwtUtil.generateToken(user.getUserId(), user.getUserEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUserId());
        log.info("accessToken: " + accessToken);
        log.info("refreshToken: " + refreshToken.getToken());

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
