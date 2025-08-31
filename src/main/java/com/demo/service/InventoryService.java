package com.demo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.entity.Inventory;
import com.demo.entity.Order;
import com.demo.entity.OrderItem;
import com.demo.entity.Product;
import com.demo.exception.CustomException;
import com.demo.repository.InventoryRepository;
import com.demo.repository.ProductRepository;

@Service
public class InventoryService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryRepository inventoryRepository;


    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);
    private static final int LOW_STOCK_THRESHOLD = 5;

    /**
     * ✅ Deduct stock based on Order entity (used in synchronous flow)
     */
    @Transactional
    public void deductStock(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Product product = fetchProduct(item.getProduct().getId());
            int remainingStock = product.getStock() - item.getQuantity();

            if (remainingStock < 0) {
                throw new CustomException("Insufficient stock", "Available", product.getStock());
            }

            product.setStock(remainingStock);
            productRepository.save(product);

            log.info("✅ Stock updated for '{}': remaining {}", product.getName(), remainingStock);
            checkLowStock(product);
        }
    }

    /**
     * ✅ Deduct stock based on Kafka event payload
     */
    
     
    @Transactional
    public void restoreStock(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Product product = fetchProduct(item.getProduct().getId());
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);

            log.info("↩️ Stock restored for '{}': new stock {}", product.getName(), product.getStock());
        }
    }

    /**
     * ✅ Validate stock before adding to cart
     */
    public void validateStockBeforeCartAdd(Long productId, int requestedQty) {
        Product product = fetchProduct(productId);
        if (requestedQty > product.getStock()) {
            throw new CustomException("Requested quantity exceeds available stock", "Available", product.getStock());
        }
    }

    /**
     * ✅ Get products below threshold
     */
    public List<Product> getLowStockProducts() {
        return productRepository.findByStockLessThan(LOW_STOCK_THRESHOLD);
    }

    /**
     * ✅ Daily audit log for low stock products
     */
    @Scheduled(cron = "0 0 9 * * ?") // Daily at 9 AM
    public void auditLowStockProducts() {
        List<Product> lowStock = getLowStockProducts();
        if (!lowStock.isEmpty()) {
            log.warn("📉 Daily Inventory Audit: {} products below threshold", lowStock.size());
            lowStock.forEach(p -> log.warn(" - {} (Stock: {})", p.getName(), p.getStock()));
        }
    }

    /**
     * ✅ Fetch product safely
     */
    private Product fetchProduct(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new CustomException("Product not found", "with ID", productId));
    }

    /**
     * ✅ Log low stock warning
     */
    private void checkLowStock(Product product) {
        if (product.getStock() < LOW_STOCK_THRESHOLD) {
            log.warn("⚠️ Low stock alert for '{}': only {} left", product.getName(), product.getStock());
        }
    }
    
    @Transactional
    public void initializeInventory(Product product, int quantity) {
        Inventory inventory = new Inventory(product, quantity);
        inventoryRepository.save(inventory);
        log.info("📦 Inventory initialized for product '{}' with quantity {}", product.getName(), quantity);
    }

    
    /**
     * ✅ Unlink product from inventory before deletion
     */
    @Transactional
    public void unlinkProductFromInventory(Product product) {
        Inventory inventory = inventoryRepository.findByProduct(product);
        if (inventory != null) {
            inventory.setProduct(null);
            inventoryRepository.save(inventory);
            log.info("🧹 Unlinked product '{}' from inventory", product.getName());
        } else {
            log.info("ℹ️ No inventory record found for product '{}'", product.getName());
        }
    }
}