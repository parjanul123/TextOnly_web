package com.messenger.controller;

import com.messenger.model.Message;
import com.messenger.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public Message sendMessage(@RequestBody Message message) {
        return messageService.sendMessage(message);
    }

    // This endpoint is deprecated - use MessageApiController instead
    @GetMapping("/{phoneNumber}")
    public List<Message> getMessages(@PathVariable String phoneNumber) {
        return List.of(); // Return empty list - use /api/messages endpoints
    }
}
