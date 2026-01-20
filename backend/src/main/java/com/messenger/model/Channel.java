package com.messenger.model;

import jakarta.persistence.*;

@Entity
@Table(name = "channels")
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long serverId; // Foreign key to Server
    private String name;
    private String type; // "TEXT" or "VOICE"
    
    // Constructors
    public Channel() {}
    
    public Channel(Long serverId, String name, String type) {
        this.serverId = serverId;
        this.name = name;
        this.type = type;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getServerId() { return serverId; }
    public void setServerId(Long serverId) { this.serverId = serverId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
