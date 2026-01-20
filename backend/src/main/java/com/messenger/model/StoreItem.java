package com.messenger.model;

import jakarta.persistence.*;

@Entity
@Table(name = "store_items")
public class StoreItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String type; // "EMOTICON", "FRAME", "GIFT"
    private Integer price;
    private String resourceName;
    
    // Constructors
    public StoreItem() {}
    
    public StoreItem(String name, String type, Integer price, String resourceName) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.resourceName = resourceName;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    
    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }
}
