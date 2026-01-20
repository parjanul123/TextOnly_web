package com.messenger.service;

import com.messenger.model.Message;
import com.messenger.model.User;
import com.messenger.repository.MessageRepository;
import com.messenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> getMessagesForUsers(User user1, User user2) {
        return messageRepository.findConversationBetween(user1, user2);
    }
}
