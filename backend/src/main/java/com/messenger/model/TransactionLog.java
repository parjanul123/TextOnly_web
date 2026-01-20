package com.messenger.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_logs")
public class TransactionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private String phoneNumber; // User identifier
    private String description;
    private Integer amount; // Negative for spent, positive for gained
    private String type; // "PURCHASE", "GIFT_SENT", "GIFT_RECEIVED", "GIFT_SENT_VOICE"
    private LocalDateTime timestamp;
    
    // Constructors
    public TransactionLog() {
        this.timestamp = LocalDateTime.now();
    }
    
    public TransactionLog(String phoneNumber, String description, Integer amount, String type) {
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
