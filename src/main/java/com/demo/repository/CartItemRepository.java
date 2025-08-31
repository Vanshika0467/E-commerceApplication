package com.demo.repository;

import com.demo.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 🔍 Fetch all items by Cart ID
    List<CartItem> findByCartId(Long cartId);

    // 🔍 Fetch all items by User ID (via cart)
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.user.id = :userId")
    List<CartItem> findByUserId(Long userId);

    // 🧹 Delete all items by Cart ID
    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteByCartId(Long cartId);

    // 🧹 Delete all items by User ID (via cart)
    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.user.id = :userId")
    void deleteByUserId(Long userId);
}