package com.demo.controller;

import com.demo.entity.Order;
import com.demo.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    /**
     * ✅ Place an order and publish OrderCreatedEvent to Kafka
     */
    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(
            @RequestParam Long userId,
            @RequestParam String paymentMethod) {

        logger.info("Placing order for user ID {} with payment method '{}'", userId, paymentMethod);
        Order order = orderService.placeOrder(userId, paymentMethod);
        return ResponseEntity.ok(order);
    }

    /**
     * ✅ Get all orders placed by a specific user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable Long userId) {
        logger.info("Fetching orders for user ID {}", userId);
        List<Order> orders = orderService.getOrdersByUser(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * ✅ Update the status of an existing order
     * Triggers product unlinking if status is 'DELIVERED'
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {

        logger.info("Updating status of order ID {} to '{}'", orderId, status);
        Order updatedOrder = orderService.updateStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }
}