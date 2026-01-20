package com.messenger.model;

import jakarta.persistence.*;

@Entity
@Table(name = "servers")
public class Server {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String creatorPhoneNumber;
    
    // Constructors
    public Server() {}
    
    public Server(String name, String creatorPhoneNumber) {
        this.name = name;
        this.creatorPhoneNumber = creatorPhoneNumber;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCreatorPhoneNumber() { return creatorPhoneNumber; }
    public void setCreatorPhoneNumber(String creatorPhoneNumber) { this.creatorPhoneNumber = creatorPhoneNumber; }
}
