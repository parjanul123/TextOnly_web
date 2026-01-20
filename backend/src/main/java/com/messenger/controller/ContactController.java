package com.messenger.controller;

import com.messenger.model.Contact;
import com.messenger.model.User;
import com.messenger.repository.ContactRepository;
import com.messenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contacts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getContacts(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Long userId = extractUserIdFromToken(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
        
        List<Contact> contacts = contactRepository.findByUser(userOpt.get());
        
        List<Map<String, Object>> response = contacts.stream()
            .map(contact -> {
                User contactUser = contact.getContact();
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", contactUser.getId());
                dto.put("email", contactUser.getEmail());
                dto.put("displayName", contactUser.getDisplayName());
                dto.put("avatarUrl", contactUser.getAvatarUrl());
                dto.put("status", contactUser.getStatus());
                return dto;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{contactId}")
    public ResponseEntity<?> addContact(
            @PathVariable Long contactId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Long userId = extractUserIdFromToken(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<User> contactUserOpt = userRepository.findById(contactId);
        
        if (userOpt.isEmpty() || contactUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
        
        // Check if contact already exists
        Optional<Contact> existingContact = contactRepository.findByUserAndContact(userOpt.get(), contactUserOpt.get());
        if (existingContact.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Contact already exists"));
        }
        
        Contact contact = new Contact();
        contact.setUser(userOpt.get());
        contact.setContact(contactUserOpt.get());
        contactRepository.save(contact);
        
        User contactUser = contactUserOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("id", contactUser.getId());
        response.put("email", contactUser.getEmail());
        response.put("displayName", contactUser.getDisplayName());
        response.put("avatarUrl", contactUser.getAvatarUrl());
        response.put("status", contactUser.getStatus());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<?> removeContact(
            @PathVariable Long contactId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Long userId = extractUserIdFromToken(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<User> contactUserOpt = userRepository.findById(contactId);
        
        if (userOpt.isEmpty() || contactUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
        
        contactRepository.deleteByUserAndContact(userOpt.get(), contactUserOpt.get());
        
        return ResponseEntity.noContent().build();
    }

    private Long extractUserIdFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer mock-jwt-token-")) {
            String userIdStr = authHeader.replace("Bearer mock-jwt-token-", "");
            try {
                return Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
