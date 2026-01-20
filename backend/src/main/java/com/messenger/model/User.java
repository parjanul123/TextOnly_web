package com.messenger.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "display_name")
    private String displayName;
    
    @Column(name = "avatar_url")
    private String avatarUrl;
    
    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'offline'")
    private String status; // online, offline, away, busy
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    // Extra fields from Android (OnlyCoins system)
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;
    
    @Column(name = "coin_balance")
    private Integer coinBalance = 100; // Starting balance
    
    @Column(name = "wallet_balance")
    private Double walletBalance = 0.0;
    
    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public Integer getCoinBalance() { return coinBalance; }
    public void setCoinBalance(Integer coinBalance) { this.coinBalance = coinBalance; }
    
    public Double getWalletBalance() { return walletBalance; }
    public void setWalletBalance(Double walletBalance) { this.walletBalance = walletBalance; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
