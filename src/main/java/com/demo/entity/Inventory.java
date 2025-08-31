package com.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents inventory for a product.
 * Supports unlinking product post-deletion while preserving inventory record.
 */
@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Link to Product (nullable for safe unlinking)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = true, unique = true)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    // ✅ Optional: Track last updated timestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    // ===== Constructors =====
    public Inventory() {}

    public Inventory(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.lastUpdated = LocalDateTime.now();
    }

    // ===== Business Logic =====
    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.lastUpdated = LocalDateTime.now();
    }

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}