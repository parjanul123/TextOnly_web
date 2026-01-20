package com.messenger.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private String phoneNumber; // User identifier
    private String itemName;
    private String itemType; // "EMOTICON", "FRAME", "GIFT"
    private String resourceName;
    private LocalDateTime acquiredDate;
    
    // Constructors
    public InventoryItem() {
        this.acquiredDate = LocalDateTime.now();
    }
    
    public InventoryItem(String phoneNumber, String itemName, String itemType, String resourceName) {
        this.phoneNumber = phoneNumber;
        this.itemName = itemName;
        this.itemType = itemType;
        this.resourceName = resourceName;
        this.acquiredDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    
    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }
    
    public LocalDateTime getAcquiredDate() { return acquiredDate; }
    public void setAcquiredDate(LocalDateTime acquiredDate) { this.acquiredDate = acquiredDate; }
}
