package com.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.demo.entity.Order;
import com.demo.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // ✅ Find all orders of a specific user
    List<Order> findByUser(User user);

    // ✅ Find orders by userId
    List<Order> findByUserId(Long userId);

    // ✅ Find orders by status (e.g., "PENDING", "DELIVERED")
    List<Order> findByStatus(String status);
}