package com.demo.controller;

import com.demo.entity.Order;
import com.demo.entity.Product;
import com.demo.service.InventoryService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/deduct")
    public ResponseEntity<String> deductStock(@RequestBody Order order) {
        try {
            inventoryService.deductStock(order);
            logger.info("Stock deducted for order ID: {}", order.getId());
            return ResponseEntity.ok("Stock deducted successfully");
        } catch (IllegalStateException e) {
            logger.warn("Stock deduction failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/restore")
    public ResponseEntity<String> restoreStock(@RequestBody Order order) {
        try {
            inventoryService.restoreStock(order);
            logger.info("Stock restored for order ID: {}", order.getId());
            return ResponseEntity.ok("Stock restored successfully");
        } catch (EntityNotFoundException e) {
            logger.warn("Restore failed: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts() {
        logger.info("Fetching low-stock products");
        List<Product> lowStockProducts = inventoryService.getLowStockProducts();
        return ResponseEntity.ok(lowStockProducts);
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateStock(
            @RequestParam Long productId,
            @RequestParam int quantity
    ) {
        try {
            inventoryService.validateStockBeforeCartAdd(productId, quantity);
            logger.info("Stock validated for product ID: {}", productId);
            return ResponseEntity.ok("Stock is sufficient");
        } catch (IllegalStateException e) {
            logger.warn("Stock validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}