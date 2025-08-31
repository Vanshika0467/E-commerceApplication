package com.demo.service;

import com.demo.entity.Cart;
import com.demo.entity.CartItem;
import com.demo.entity.Inventory;
import com.demo.entity.Order;
import com.demo.entity.OrderItem;
import com.demo.entity.Product;
import com.demo.entity.User;
import com.demo.repository.CartRepository;
import com.demo.repository.InventoryRepository;
import com.demo.repository.OrderItemRepository;
import com.demo.repository.OrderRepository;
import com.demo.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    /**
     * ✅ Place an order from a user's cart with dynamic input
     */
    @Transactional
    public Order placeOrder(Long userId, String paymentMethod) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setPaymentMethod(paymentMethod);
        order.setBillingAddress(user.getBillingAddress());
        order.setShippingAddress(user.getShippingAddress());

        order = orderRepository.save(order); // Persist first

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            Inventory inventory = inventoryRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new IllegalStateException("Inventory not found for product: " + product.getName()));

            if (inventory.getQuantity() < quantity) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }

            inventory.setQuantity(inventory.getQuantity() - quantity);
            inventoryRepository.save(inventory);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(BigDecimal.valueOf(product.getPrice()));
            orderItem.setProductName(product.getName());
            orderItem.setProductPrice(BigDecimal.valueOf(product.getPrice()));

            totalAmount = totalAmount.add(orderItem.getItemTotal());
            orderItems.add(orderItem);
        }

        orderItemRepository.saveAll(orderItems);
        order.setTotalAmount(totalAmount.doubleValue());
        orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        return order;
    }

    /**
     * ✅ Get all orders placed by a user
     */
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    /**
     * ✅ Update the status of an existing order
     */
    @Transactional
    public Order updateStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        if ("DELIVERED".equalsIgnoreCase(status)) {
            unlinkProductsFromOrderItems(orderId);
        }

        return updatedOrder;
    }

    /**
     * ✅ Unlink product from order items once delivered
     */
    @Transactional
    public void unlinkProductsFromOrderItems(Long orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        for (OrderItem item : items) {
            Product product = item.getProduct();
            if (product != null) {
                item.setProductName(product.getName());
                item.setProductPrice(BigDecimal.valueOf(product.getPrice()));
                item.setProduct(null);
            }
        }

        orderItemRepository.saveAll(items);
        log.info("Unlinked products from delivered order items for order ID {}", orderId);
    }

    /**
     * ✅ Get a single order by ID (used by InvoiceService, etc.)
     */
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }
}