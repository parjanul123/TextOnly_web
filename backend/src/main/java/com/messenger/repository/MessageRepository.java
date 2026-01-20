package com.messenger.repository;

import com.messenger.model.Message;
import com.messenger.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE " +
           "(m.sender = :user1 AND m.receiver = :user2) OR " +
           "(m.sender = :user2 AND m.receiver = :user1) " +
           "ORDER BY m.createdAt DESC")
    List<Message> findConversationBetween(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT m FROM Message m WHERE m.receiver = :user AND m.isRead = false ORDER BY m.createdAt DESC")
    List<Message> findUnreadMessages(@Param("user") User user);
    
    List<Message> findBySenderOrReceiverOrderByCreatedAtDesc(User sender, User receiver);
}
