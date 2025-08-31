package com.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.demo.entity.Cart;
import com.demo.service.CartService;

@RestController
@RequestMapping("/api/carts")
public class CartController {
	@Autowired
	private  CartService cartService;
    
    // ✅ Get cart by ID
    @GetMapping("/{cartId}")
    public ResponseEntity<Cart> getCart(@PathVariable Long cartId) {
        return ResponseEntity.ok(cartService.getCart(cartId));
    }

    // ✅ Add item to cart
    @PostMapping("/{cartId}/add/{productId}")
    public ResponseEntity<Cart> addItem(
            @PathVariable Long cartId,
            @PathVariable Long productId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.addItemToCart(cartId, productId, quantity));
    }

    // ✅ Remove item from cart
    @DeleteMapping("/{cartId}/remove/{itemId}")
    public ResponseEntity<Cart> removeItem(
            @PathVariable Long cartId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(cartId, itemId));
    }
}