package com.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String invoiceNumber; // e.g. INV-20250826-0001

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal taxAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal shippingFee;

    @Column(nullable = false)
    private String paymentMethod; // e.g. "COD", "UPI", "Card"

    @Column(nullable = false)
    private String billingAddress;

    @Column(nullable = false)
    private String shippingAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status; // e.g. GENERATED, SENT, CANCELLED

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @Column
    private String filePath; // Optional: path to stored PDF

    // ===== Constructors =====
    public Invoice() {}

    public Invoice(Long id, String invoiceNumber, LocalDateTime generatedAt, BigDecimal totalAmount,
                   BigDecimal taxAmount, BigDecimal shippingFee, String paymentMethod,
                   String billingAddress, String shippingAddress, InvoiceStatus status,
                   Order order, User user, String filePath) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.generatedAt = generatedAt;
        this.totalAmount = totalAmount;
        this.taxAmount = taxAmount;
        this.shippingFee = shippingFee;
        this.paymentMethod = paymentMethod;
        this.billingAddress = billingAddress;
        this.shippingAddress = shippingAddress;
        this.status = status;
        this.order = order;
        this.user = user;
        this.filePath = filePath;
    }

    // ===== Getters & Setters =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    // ===== Debugging =====
    @Override
    public String toString() {
        return "Invoice{" +
               "id=" + id +
               ", invoiceNumber='" + invoiceNumber + '\'' +
               ", generatedAt=" + generatedAt +
               ", totalAmount=" + totalAmount +
               ", status=" + status +
               ", user=" + (user != null ? user.getId() : null) +
               ", order=" + (order != null ? order.getId() : null) +
               '}';
    }
}