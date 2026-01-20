package com.messenger.controller;

import com.messenger.model.Message;
import com.messenger.model.User;
import com.messenger.repository.MessageRepository;
import com.messenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MessageApiController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> sendMessage(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, Object> request) {
        
        // Extract userId from token
        Long senderId = extractUserIdFromToken(authHeader);
        if (senderId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }
        
        Long receiverId = Long.valueOf(request.get("receiverId").toString());
        String content = request.get("content").toString();
        
        Optional<User> senderOpt = userRepository.findById(senderId);
        Optional<User> receiverOpt = userRepository.findById(receiverId);
        
        if (senderOpt.isEmpty() || receiverOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
        
        Message message = new Message();
        message.setSender(senderOpt.get());
        message.setReceiver(receiverOpt.get());
        message.setContent(content);
        message.setType("TEXT");
        message.setIsRead(false);
        
        Message savedMessage = messageRepository.save(message);
        
        // Create response DTO
        Map<String, Object> response = new HashMap<>();
        response.put("id", savedMessage.getId());
        response.put("senderId", savedMessage.getSender().getId());
        response.put("senderName", savedMessage.getSender().getDisplayName());
        response.put("receiverId", savedMessage.getReceiver().getId());
        response.put("content", savedMessage.getContent());
        response.put("isRead", savedMessage.getIsRead());
        response.put("createdAt", savedMessage.getCreatedAt().toString());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<?> getConversation(
            @PathVariable Long otherUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Long currentUserId = extractUserIdFromToken(authHeader);
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }
        
        Optional<User> currentUserOpt = userRepository.findById(currentUserId);
        Optional<User> otherUserOpt = userRepository.findById(otherUserId);
        
        if (currentUserOpt.isEmpty() || otherUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
        
        List<Message> messages = messageRepository.findConversationBetween(currentUserOpt.get(), otherUserOpt.get());
        
        List<Map<String, Object>> response = messages.stream()
            .map(msg -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", msg.getId());
                dto.put("senderId", msg.getSender().getId());
                dto.put("senderName", msg.getSender().getDisplayName());
                dto.put("receiverId", msg.getReceiver().getId());
                dto.put("content", msg.getContent());
                dto.put("isRead", msg.getIsRead());
                dto.put("createdAt", msg.getCreatedAt().toString());
                return dto;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadMessages(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Long userId = extractUserIdFromToken(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
        
        List<Message> messages = messageRepository.findUnreadMessages(userOpt.get());
        
        List<Map<String, Object>> response = messages.stream()
            .map(msg -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", msg.getId());
                dto.put("senderId", msg.getSender().getId());
                dto.put("senderName", msg.getSender().getDisplayName());
                dto.put("receiverId", msg.getReceiver().getId());
                dto.put("content", msg.getContent());
                dto.put("isRead", msg.getIsRead());
                dto.put("createdAt", msg.getCreatedAt().toString());
                return dto;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        Optional<Message> messageOpt = messageRepository.findById(id);
        if (messageOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Message not found"));
        }
        
        Message message = messageOpt.get();
        message.setIsRead(true);
        messageRepository.save(message);
        
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
