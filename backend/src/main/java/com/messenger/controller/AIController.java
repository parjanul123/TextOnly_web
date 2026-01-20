package com.messenger.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "*")
public class AIController {

    // POST /ai/chat - Chat with GPT
    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        
        // TODO: Integrate with actual GPT-5 API
        // For now, return mock response
        String reply = "Răspuns de la GPT-5: " + userMessage;
        
        return ResponseEntity.ok(Map.of(
            "reply", reply,
            "model", "gpt-5"
        ));
    }
}
