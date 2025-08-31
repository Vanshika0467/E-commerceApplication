package com.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents an item in an order.
 * Supports audit snapshot of product details and allows unlinking product post-delivery.
 */
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== Product relation =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = true) // ✅ allow null for FK unlinking
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // ✅ Snapshot fields for audit/history
    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_price", precision = 10, scale = 2)
    private BigDecimal productPrice;

    // ===== Order relation =====
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    // ===== Constructors =====
    public OrderItem() {}

    public OrderItem(Product product, int quantity, Order order) {
        this.product = product;
        this.quantity = quantity;
        this.order = order;
        this.price = BigDecimal.valueOf(product.getPrice());
        this.productName = product.getName();
        this.productPrice = BigDecimal.valueOf(product.getPrice());
    }

    // ===== Derived Fields =====
    @Transient
    public BigDecimal getItemTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getProductPrice() { return productPrice; }
    public void setProductPrice(BigDecimal productPrice) { this.productPrice = productPrice; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    // ===== Equality & Hashing =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem)) return false;
        OrderItem that = (OrderItem) o;
        return Objects.equals(product, that.product) &&
               Objects.equals(order, that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, order);
    }

    // ===== Debugging =====
    @Override
    public String toString() {
        return "OrderItem{" +
               "id=" + id +
               ", product=" + (product != null ? product.getName() : null) +
               ", quantity=" + quantity +
               ", unitPrice=" + price +
               ", total=" + getItemTotal() +
               '}';
    }
}