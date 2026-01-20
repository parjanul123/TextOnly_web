package com.messenger.controller;

import com.messenger.model.User;
import com.messenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EmailAuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String displayName = request.get("displayName");
        
        // Check if email already exists
        if (userRepository.findByEmail(email).isPresent()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Email already registered");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
        
        // Create new user
        User user = new User();
        user.setEmail(email);
        user.setPassword(password); // TODO: Hash password with BCrypt
        user.setDisplayName(displayName);
        user.setStatus("offline");
        user.setIsActive(true);
        user.setCoinBalance(100); // Starting balance
        
        User savedUser = userRepository.save(user);
        
        // Return user info with mock token
        Map<String, Object> response = new HashMap<>();
        response.put("userId", savedUser.getId());
        response.put("email", savedUser.getEmail());
        response.put("displayName", savedUser.getDisplayName());
        response.put("status", savedUser.getStatus());
        response.put("token", "mock-jwt-token-" + savedUser.getId());
        response.put("tokenExpiresAt", "2025-01-21T00:00:00");
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        
        User user = userOpt.get();
        
        // TODO: Verify password with BCrypt
        if (!user.getPassword().equals(password)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        
        // Update status to online
        user.setStatus("online");
        userRepository.save(user);
        
        // Return user info with mock token
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("email", user.getEmail());
        response.put("displayName", user.getDisplayName());
        response.put("avatarUrl", user.getAvatarUrl());
        response.put("status", user.getStatus());
        response.put("coinBalance", user.getCoinBalance());
        response.put("token", "mock-jwt-token-" + user.getId());
        response.put("tokenExpiresAt", "2025-01-21T00:00:00");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        // Mock token validation
        if (authHeader != null && authHeader.startsWith("Bearer mock-jwt-token-")) {
            Map<String, Boolean> response = new HashMap<>();
            response.put("valid", true);
            return ResponseEntity.ok(response);
        }
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", false);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        // Extract userId from token
        if (authHeader != null && authHeader.startsWith("Bearer mock-jwt-token-")) {
            String userIdStr = authHeader.replace("Bearer mock-jwt-token-", "");
            try {
                Long userId = Long.parseLong(userIdStr);
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    user.setStatus("offline");
                    userRepository.save(user);
                }
            } catch (NumberFormatException e) {
                // Invalid token format
            }
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
}
