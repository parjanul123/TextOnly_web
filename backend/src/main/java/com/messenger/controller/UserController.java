package com.messenger.controller;

import com.messenger.model.User;
import com.messenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // GET /users/profile/{phoneNumber} - Get user profile
    @GetMapping("/profile/{phoneNumber}")
    public ResponseEntity<?> getProfile(@PathVariable String phoneNumber) {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        
        if (!userOpt.isPresent()) {
            // Create default user if doesn't exist
            User newUser = new User();
            newUser.setPhoneNumber(phoneNumber);
            newUser.setDisplayName("User");
            newUser.setCoinBalance(100);
            newUser.setWalletBalance(0.0);
            userRepository.save(newUser);
            return ResponseEntity.ok(newUser);
        }
        
        return ResponseEntity.ok(userOpt.get());
    }

    // POST /users/profile - Update user profile
    @PostMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> request) {
        String phoneNumber = (String) request.get("phoneNumber");
        String displayName = (String) request.get("displayName");
        String avatarUrl = (String) request.get("avatarUrl");

        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        User user;
        
        if (!userOpt.isPresent()) {
            user = new User();
            user.setPhoneNumber(phoneNumber);
            user.setCoinBalance(100);
        } else {
            user = userOpt.get();
        }

        if (displayName != null) {
            user.setDisplayName(displayName);
        }
        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
        }

        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Profile updated successfully",
            "user", user
        ));
    }

    // GET /users/balance/{phoneNumber} - Get user balance
    @GetMapping("/balance/{phoneNumber}")
    public ResponseEntity<?> getBalance(@PathVariable String phoneNumber) {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        
        if (!userOpt.isPresent()) {
            return ResponseEntity.ok(Map.of(
                "coinBalance", 0,
                "walletBalance", 0.0
            ));
        }

        User user = userOpt.get();
        return ResponseEntity.ok(Map.of(
            "coinBalance", user.getCoinBalance(),
            "walletBalance", user.getWalletBalance()
        ));
    }
}
