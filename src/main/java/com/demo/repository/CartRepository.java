package com.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.entity.Cart;
import com.demo.entity.User;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUser(User user);  // ✅ Not Optional
    Optional<Cart> findByUserId(Long userId);
}